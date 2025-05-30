package com.mehatronics.axle_load.handler;

import static com.mehatronics.axle_load.R.id.buttonGoToAxes;

import com.mehatronics.axle_load.notification.MessageCallback;

public interface BluetoothHandlerContract extends MessageCallback {
    void showFragment();

    boolean isFragmentNotVisible();

    void loadingManagerShowLoading(boolean isLoading);

    void setIsAttemptingToConnect(boolean isAttempting);

    boolean isAttemptingToConnect();

    void initConfigureButton();

    @Override
    void showMessage(String message);
}
