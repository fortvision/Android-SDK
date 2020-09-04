package com.fortvision.minisites.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * A collection of utility methods used with the SDK.
 */

public class Utils {

    public  static DisplayMetrics displayMetrics;
    public static float density;

    public static void setDisplayMetrics(DisplayMetrics displayMetrics1, float density1){
        displayMetrics = displayMetrics1;
        density = density1;
    }
    /**
     * @param context the current context.
     * @return the default user agent for http connections.
     */
    public static String getUserAgent(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return WebSettings.getDefaultUserAgent(context);
        else
            return System.getProperty("http.agent");
    }

    /**
     * @return the device ip as a string
     */
    @SuppressWarnings("EmptyCatchBlock")
    public static String getDeviceIpAsStr() {
        String ip4Address = null;
        String ip6Address = null;
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            outer: for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                         boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (isIPv4){
                            ip4Address = sAddr;
                            break outer;
                        }
                        else if (ip6Address == null){
                            int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                            ip6Address =  delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return ip4Address != null ? ip4Address : ip6Address != null ? ip6Address : "";
    }

    public static int getScreenWidth(@NonNull Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(@NonNull Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int dpToPx(@NonNull Context context, int size) {
        return (int) Math.ceil(context.getResources().getDisplayMetrics().density * size);
    }

    public static float dpToPx(@NonNull Context context, float size) {
        return context.getResources().getDisplayMetrics().density * size;
    }

    public static int getJsonElementAsInt(@Nullable JsonElement element, int defaultValue) {
        return element == null || element.isJsonNull() ? defaultValue : element.getAsInt();
    }

    public static float getJsonElementAsFloat(@Nullable JsonElement element, float defaultValue) {
        return element == null || element.isJsonNull() ? defaultValue : element.getAsFloat();
    }

    public static boolean getJsonElementAsBoolean(@Nullable JsonElement element, boolean defaultValue) {
        return element == null || element.isJsonNull() ? defaultValue : element.getAsBoolean();
    }

    public static String getJsonElementAsString(@Nullable JsonElement element, String defaultValue) {
        return element == null || element.isJsonNull() ? defaultValue : element.getAsString();
    }

    public static String readFully(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String res = "";
        String temp;
        try {
            while ((temp = reader.readLine()) != null)
                res += temp;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static void configuredPoweredByView(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.fortvision.com")));
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    public static void configureWebView(final WebView webView, final View progressView) {
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings settings = webView.getSettings();
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                progressView.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("EmptyCatchBlock")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("http")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        webView.getContext().startActivity(intent);
                    } catch (Exception e) {
                    }
                } else {
                    webView.loadUrl(url);
                    progressView.setVisibility(View.VISIBLE);
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                progressView.setVisibility(View.GONE);
            }
        });
    }

    public static void loadImage(@NonNull ImageView target, String url, @DrawableRes int fallbackRes) {
        if (TextUtils.isEmpty(url))
            target.setImageResource(fallbackRes);
        else
            Glide.with(target.getContext()).load(url)
                    .apply(new RequestOptions().error(fallbackRes)).into(target);
    }

    public static void loadImage(@NonNull Context context, @NonNull Target<Drawable> target, String url, @DrawableRes int fallbackRes) {
        if (TextUtils.isEmpty(url))
            target.onResourceReady(ContextCompat.getDrawable(context, fallbackRes), null);
        else
            Glide.with(context).load(url)
                    .apply(new RequestOptions().error(fallbackRes)).into(target);
    }


}
