package com.fortvision.minisites;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fortvision.minisites.model.Popup;
import com.fortvision.minisites.utils.Assets;
import com.fortvision.minisites.utils.Utils;

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(context, R.style.FVMinisitesDialogTheme);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = size.y - 48 - Utils.dpToPx(context, popup.getTopMargin()) - Utils.dpToPx(context, popup.getBottomMargin());
        int width = size.x - Utils.dpToPx(context, popup.getStartMargin()) - Utils.dpToPx(context, popup.getEndMargin());

        //int height = buttonContainer.getHeight() - Utils.dpToPx(context, popup.getTopMargin()) - Utils.dpToPx(context, popup.getBottomMargin());
        //int width = buttonContainer.getWidth() - Utils.dpToPx(context, popup.getStartMargin()) - Utils.dpToPx(context, popup.getEndMargin());

        @SuppressLint("InflateParams")
        FrameLayout fl = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.fv_minisites_popup, null, false);

        View popupContent = fl.findViewById(R.id.fv_minisites_poup_content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) popupContent.getLayoutParams();
        layoutParams.setMargins(
                Utils.dpToPx(context, popup.getStartMargin()),
                Utils.dpToPx(context, popup.getTopMargin()),
                Utils.dpToPx(context, popup.getEndMargin() == 0 ? 0 : popup.getEndMargin() - 22),
                Utils.dpToPx(context, popup.getBottomMargin() - 24)
        );
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
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) popupWebView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        popupWebView.setLayoutParams(lp);

        dialog.setContentView(fl, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final ProgressBar progressBar = (ProgressBar) fl.findViewById(R.id.fv_minisites_progress_bar);
        Utils.configureWebView(popupWebView, progressBar);
        popupWebView.loadUrl(popup.getContent());
        popupWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                buttonViewController.onLoadPopup();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
