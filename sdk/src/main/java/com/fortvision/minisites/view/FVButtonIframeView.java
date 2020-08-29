package com.fortvision.minisites.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fortvision.minisites.R;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.model.FVButtonType;
import com.fortvision.minisites.model.IframeButton;

/**
 * A view that designed to display a simple {@link IframeButton}
 */

public class FVButtonIframeView extends FVButtonView {

    private WebView webView;

    public FVButtonIframeView(@NonNull Context context) {
        this(context, null);
    }

    public FVButtonIframeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("JavascriptInterface")
    public FVButtonIframeView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.fv_minisites_iframe_content, this);
        webView = findViewById(R.id.fv_minisites_iframe_btn);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100 && listener != null)
                    listener.onFinishedLoadingData(FVButtonIframeView.this);
            }


        });
    }

    @Override
    public View getContentView() {
        return webView;
    }

    @Override
    public void accept(@NonNull final FVButton button) {
        super.accept(button);
        if (button.getButtonType() != FVButtonType.IFRAME)
            return;
        IframeButton ib = (IframeButton) button;

        webView.loadUrl(ib.getButtonContentUrl());
        webView.refreshDrawableState();
        //webView.setMaxWidth(webView.getMinimumWidth());
        //webView.setMaxHeight(webView.getMinimumHeight());
    }

}
