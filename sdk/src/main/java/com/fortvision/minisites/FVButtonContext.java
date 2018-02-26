package com.fortvision.minisites;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Represent the context of the FV button.
 */

public class FVButtonContext {

    @NonNull
    private WeakReference<Activity> activity;

    @NonNull
    private String publisherId;

    @Nullable
    private String categoryId;

    @Nullable
    private String internalId;

    @Nullable
    private String userId;

    @NonNull
    private String userAgent;

    public FVButtonContext(@NonNull Activity activity, @NonNull String publisherId, @Nullable String categoryId,
                           @Nullable String internalId, @NonNull String userAgent) {
        this.activity = new WeakReference<>(activity);
        this.publisherId = publisherId;
        this.categoryId = categoryId;
        this.internalId = internalId;
        this.userAgent = userAgent;
    }

    @Nullable
    public Activity getActivity() {
        return activity.get();
    }

    @NonNull
    public String getPublisherId() {
        return publisherId;
    }

    @Nullable
    public String getCategoryId() {
        return categoryId;
    }

    @Nullable
    public String getInternalId() {
        return internalId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getUserAgent() {
        return userAgent;
    }

    public void destroy() {
        activity.clear();
    }

}
