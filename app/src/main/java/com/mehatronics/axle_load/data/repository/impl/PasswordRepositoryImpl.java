package com.mehatronics.axle_load.data.repository.impl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.helper.SingleLiveEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PasswordRepositoryImpl implements PasswordRepository {
    private final SingleLiveEvent<Void> showPasswordDialogEvent = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> isPasswordDialogVisible = new MutableLiveData<>(false);

    private boolean shouldSendPassword = false;
    private boolean isSet = false;
    private String password;
    private String newPassword;
    private boolean isPasswordStandart = false;

    @Inject
    public PasswordRepositoryImpl() {
    }

    @Override
    public void save(String password) {
        this.password = password;
        this.shouldSendPassword = true;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String get() {
        return password == null ? "" : password;
    }

    @Override
    public void clear() {
        password = null;
        newPassword = null;
        shouldSendPassword = false;
        isSet = false;
    }

    @Override
    public boolean isSet() {
        return isSet;
    }

    @Override
    public void setFlag(boolean flag) {
        isSet = flag;
    }

    @Override
    public boolean shouldSendPassword() {
        return shouldSendPassword;
    }

    @Override
    public void setShouldSendPassword(boolean value) {
        this.shouldSendPassword = value;
    }

    @Override
    public LiveData<Void> getShowPasswordDialogEvent() {
        return showPasswordDialogEvent;
    }

    @Override
    public void requestPasswordInput() {
        showPasswordDialogEvent.call();
    }

    @Override
    public LiveData<Boolean> getIsPasswordDialogVisible() {
        return isPasswordDialogVisible;
    }

    @Override
    public void setPasswordDialogVisible(boolean visible) {
        isPasswordDialogVisible.setValue(visible);
    }

    @Override
    public void setNewPassword(String password) {
        this.newPassword = password;
    }

    @Override
    public String getNewPassword() {
        return newPassword == null ? "" : newPassword;
    }

    @Override
    public boolean isPasswordStandart() {
        return isPasswordStandart;
    }

    @Override
    public void setPasswordStandart(boolean value) {
        this.isPasswordStandart = value;
    }
}