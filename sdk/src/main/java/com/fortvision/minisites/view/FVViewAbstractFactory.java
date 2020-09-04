package com.fortvision.minisites.view;

import androidx.annotation.NonNull;

import com.fortvision.minisites.FVButtonContext;
import com.fortvision.minisites.model.FVButton;

public abstract class FVViewAbstractFactory {

    public abstract FVView create(@NonNull FVButtonContext fvContext, @NonNull final FVButton button);
}
