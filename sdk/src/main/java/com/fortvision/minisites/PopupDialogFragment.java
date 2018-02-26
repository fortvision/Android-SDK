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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fortvision.minisites.model.Popup;
import com.fortvision.minisites.utils.Assets;
import com.fortvision.minisites.utils.Utils;

public class PopupDialogFragment extends DialogFragment {
    static Context context;
    static Popup popup;

    public PopupDialogFragment() {
    }

    @SuppressLint("ValidFragment")
    public PopupDialogFragment(Context context, Popup popup) {
        this.context = context;
        this.popup = popup;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(context, R.style.FVMinisitesDialogTheme);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x - Utils.dpToPx(context, popup.getTopMargin()) - Utils.dpToPx(context, popup.getBottomMargin());
        int height = size.y - Utils.dpToPx(context, popup.getStartMargin()) - Utils.dpToPx(context, popup.getEndMargin());
        //int height = buttonContainer.getHeight() - Utils.dpToPx(context, popup.getTopMargin()) - Utils.dpToPx(context, popup.getBottomMargin());
        //int width = buttonContainer.getWidth() - Utils.dpToPx(context, popup.getStartMargin()) - Utils.dpToPx(context, popup.getEndMargin());

        @SuppressLint("InflateParams")
        FrameLayout fl = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.fv_minisites_popup, null, false);
        View popupContent = fl.findViewById(R.id.fv_minisites_poup_content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) popupContent.getLayoutParams();
        layoutParams.setMargins(Utils.dpToPx(context, popup.getStartMargin()), Utils.dpToPx(context, popup.getTopMargin()),
                Utils.dpToPx(context, popup.getEndMargin() - 18), Utils.dpToPx(context, popup.getBottomMargin() - 24));
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
        //progressBar.loadData(Utils.readFully(context.getResources().openRawResource(R.raw.fv_minisites_progress)),"text/html", "utf-8");
        popupWebView.loadUrl(popup.getContent());
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

}