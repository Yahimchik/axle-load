package com.mehatronics.axle_load.adapter;

import android.app.Activity;
import android.view.View;

import com.mehatronics.axle_load.R;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class LoadingManager {
    private View progressBar;
    private View overlayView;

    @Inject
    public LoadingManager() {
    }

    public void init(View rootView) {
        this.progressBar = rootView.findViewById(R.id.progressBar);
        this.overlayView = rootView.findViewById(R.id.overlayView);
    }

    public void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (overlayView != null) {
            overlayView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}