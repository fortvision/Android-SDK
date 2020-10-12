package com.fortvision.minisites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fortvision.minisites.model.Popup;
import com.fortvision.minisites.utils.Assets;
import com.fortvision.minisites.utils.Utils;

import static androidx.core.content.ContextCompat.getSystemService;
import static java.lang.Math.round;

public class PopupDialogFragment extends DialogFragment {
    static Context context;
    static Popup popup;
    static ButtonViewController buttonViewController;

    public PopupDialogFragment() {
    }

    @SuppressLint("ValidFragment")
    public PopupDialogFragment(Context context, Popup popup, ButtonViewController viewController) {
        this.context = context;
        this.popup = popup;
        this.buttonViewController = viewController;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        buttonViewController.onPopupDismissed();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(context, R.style.FVMinisitesDialogTheme);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = (int) (size.y * 0.93) - Utils.dpToPx(context, popup.getTopMargin()) - Utils.dpToPx(context, popup.getBottomMargin());
        int width = size.x - Utils.dpToPx(context, popup.getStartMargin()) - Utils.dpToPx(context, popup.getEndMargin());

        //int height = buttonContainer.getHeight() - Utils.dpToPx(context, popup.getTopMargin()) - Utils.dpToPx(context, popup.getBottomMargin());
        //int width = buttonContainer.getWidth() - Utils.dpToPx(context, popup.getStartMargin()) - Utils.dpToPx(context, popup.getEndMargin());

        @SuppressLint("InflateParams")
        FrameLayout fl = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.fv_minisites_popup, null, false);

        final View popupContent = fl.findViewById(R.id.fv_minisites_poup_content);
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) popupContent.getLayoutParams();
        if (popup.isIncludeSize()) {
            layoutParams.width = (int) ((popup.getWidth() / 100) * Utils.getScreenWidth(context));
            layoutParams.height = (int) ((popup.getHeight() / 100) * Utils.getScreenHeight(context));
        }
        /*.setMargins(
               Utils.dpToPx(context, popup.getStartMargin()),
               Utils.dpToPx(context, popup.getTopMargin()),
               Utils.dpToPx(context, popup.getEndMargin() == 0 ? 0 : popup.getEndMargin() - 22),
               Utils.dpToPx(context, popup.getBottomMargin() - 24)
        );*/
        popupContent.setLayoutParams(layoutParams);

        Assets.loadPopupCloseImage((ImageView) fl.findViewById(R.id.fv_minisites_close_popup));
        fl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.dismiss();
                return false;
            }
        });
        Utils.configuredPoweredByView(fl.findViewById(R.id.fv_minisites_powered_by));
        final WebView popupWebView = (WebView) fl.findViewById(R.id.fv_minisites_webview);
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) popupWebView.getLayoutParams();
        popupWebView.setLayoutParams(lp);

        dialog.setContentView(fl, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final ProgressBar progressBar = (ProgressBar) fl.findViewById(R.id.fv_minisites_progress_bar);
        Utils.configureWebView(popupWebView, progressBar);
        popupWebView.getSettings().setJavaScriptEnabled(true);
        popupWebView.getSettings().setLoadWithOverviewMode(true);
        popupWebView.getSettings().setAllowContentAccess(true);
        popupWebView.getSettings().setUseWideViewPort(false);

        popupWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                buttonViewController.onLoadPopup();
            }
        });
        popupWebView.addJavascriptInterface(new WebAppInterface(context, popupWebView, lp), "Android");
        popupWebView.loadUrl(popup.getContent());

        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @JavascriptInterface
    public void resize(final float height) {
        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FrameLayout fl = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.fv_minisites_popup, null, false);
                fl.findViewById(R.id.fv_minisites_webview).setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            } // This is your code
        };
        mainHandler.post(myRunnable);


    }
}

class WebAppInterface {
    Context mContext;
    WebView mWebView;
    FrameLayout.LayoutParams layoutParams;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c, WebView v, FrameLayout.LayoutParams lp) {
        mContext = c;
        mWebView = v;
        layoutParams = lp;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(final String width, final String height) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                float newWidth = Float.parseFloat(width) * mContext.getResources().getDisplayMetrics().density; //round(Float.parseFloat(width) * mContext.getResources().getDisplayMetrics().density);
                float newHeight = Float.parseFloat(height) * mContext.getResources().getDisplayMetrics().density;//round(Float.parseFloat(height) * mContext.getResources().getDisplayMetrics().density);
                layoutParams.width = round((newWidth < mContext.getResources().getDisplayMetrics().widthPixels ? newWidth : mContext.getResources().getDisplayMetrics().widthPixels - round(mContext.getResources().getDisplayMetrics().widthPixels * 0.10)));
                layoutParams.height = round((newHeight < mContext.getResources().getDisplayMetrics().heightPixels ? newHeight : mContext.getResources().getDisplayMetrics().heightPixels - round(mContext.getResources().getDisplayMetrics().heightPixels * 0.15)));
                mWebView.setLayoutParams(layoutParams);
                Toast.makeText(mContext, width + " " + height + " " + layoutParams.height, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
