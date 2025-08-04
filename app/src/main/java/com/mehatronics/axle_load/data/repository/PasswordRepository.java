package com.mehatronics.axle_load.data.repository;

import androidx.lifecycle.LiveData;

public interface PasswordRepository {
    void save(String password);

    String get();

    void clear();

    boolean isSet();

    void setFlag(boolean flag);

    boolean shouldSendPassword();

    void setShouldSendPassword(boolean value);

    LiveData<Void> getShowPasswordDialogEvent();

    void requestPasswordInput();

    LiveData<Boolean> getIsPasswordDialogVisible();

    void setPasswordDialogVisible(boolean visible);
}
