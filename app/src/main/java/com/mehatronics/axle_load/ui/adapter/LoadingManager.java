package com.mehatronics.axle_load.ui.adapter;

import android.view.View;

import com.mehatronics.axle_load.R;

public class LoadingManager {
    private final View progressBar;
    private final View overlayView;

    public LoadingManager(View rootView) {
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
