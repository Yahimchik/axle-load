package com.mehatronics.axle_load.domain.handler;

import com.mehatronics.axle_load.ui.notification.MessageCallback;

public interface BluetoothHandlerContract extends MessageCallback {
    void showFragment();

    void loadingManagerShowLoading(boolean isLoading);

    void setIsAttemptingToConnect(boolean isAttempting);

    boolean isAttemptingToConnect();

    void initConfigureButton();

    void onDeviceDetailsFragmentClosed();
    void onDeviceDetailsFragmentOpen();
    @Override
    void showMessage(String message);
}
