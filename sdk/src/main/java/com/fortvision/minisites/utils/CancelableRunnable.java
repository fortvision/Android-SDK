package com.fortvision.minisites.utils;

/**
 * A simple class to implement both {@link Cancelable} and {@link Runnable}
 */

public class CancelableRunnable implements Cancelable, Runnable {

    private Cancelable cancelable;

    private Runnable runnable;

    public CancelableRunnable(Cancelable cancelable, Runnable runnable) {
        this.cancelable = cancelable;
        this.runnable = runnable;
    }

    public CancelableRunnable() {
        runnable = this;
        cancelable = this;
    }

    @Override
    public void cancel(boolean interrupt) {
        if (cancelable != null)
            cancelable.cancel(interrupt);
    }

    @Override
    public void run() {
        if (runnable != null)
            runnable.run();
    }
}
