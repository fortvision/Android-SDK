package com.fortvision.minisites.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fortvision.minisites.R;
import com.fortvision.minisites.model.Anchor;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.model.FVButtonType;
import com.fortvision.minisites.model.ImageButton;

/**
 * A view that designed to display a simple {@link ImageButton}
 */

public class FVButtonImageView extends FVButtonView {

    private ImageView imageBtn;

    public FVButtonImageView(@NonNull Context context) {
        this(context, null);
    }

    public FVButtonImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FVButtonImageView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.fv_minisites_image_content, this);
        imageBtn = (ImageView) findViewById(R.id.fv_minisites_image_btn);
    }

    @Override
    public View getContentView() {
        return imageBtn;
    }

    @Override
    public void accept(@NonNull final FVButton button) {
        super.accept(button);
        if (button.getButtonType() != FVButtonType.SIMPLE_IMAGE)
            return;

        ImageButton ib = (ImageButton) button;
        Context context = getContext();
        Glide.with(context).load(ib.getAnchorImageURL(ib.getAnchor())).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (listener != null)
                    listener.onFinishedLoadingData(FVButtonImageView.this);
                return false;
            }
        }).into(imageBtn);
        if (ib.getImageLeftURL() != null)
            Glide.with(context).load(ib.getImageLeftURL()).preload();
        if (ib.getImageRightURL() != null)
            Glide.with(context).load(ib.getImageRightURL()).preload();
    }

    @Override
    public void onButtonDragged(int dx, int dy) {
        super.onButtonDragged(dx, dy);
        ImageButton ib = (ImageButton) button;
        Glide.with(getContext()).load(ib.getImageDragURL()).into(imageBtn);
    }

    @Override
    public void updateButtonAnchor(@NonNull Anchor anchor) {
        super.updateButtonAnchor(anchor);
        ImageButton ib = (ImageButton) button;
        Glide.with(getContext()).load(ib.getAnchorImageURL(anchor)).into(imageBtn);

    }
}
