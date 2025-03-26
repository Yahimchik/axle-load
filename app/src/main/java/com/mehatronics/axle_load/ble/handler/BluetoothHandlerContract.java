package com.mehatronics.axle_load.ble.handler;

public interface BluetoothHandlerContract {
    void showFragment();

    boolean isFragmentNotVisible();

    void loadingManagerShowLoading(boolean isLoading);

    void setIsAttemptingToConnect(boolean isAttempting);

    boolean isAttemptingToConnect();

    void showSnackBar(String message);

    void initConfigureButton();
}
