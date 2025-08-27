package com.mehatronics.axle_load.ui.viewModel;

import android.Manifest;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.data.repository.impl.BluetoothRepository;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.domain.usecase.SaveCalibrationTableUseCase;
import com.mehatronics.axle_load.domain.usecase.SubmitPasswordUseCase;
import com.mehatronics.axle_load.helper.SingleLiveEvent;
import com.mehatronics.axle_load.ui.adapter.listener.GattReadListener;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordDialogListener;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel, отвечающая за управление подключением к Bluetooth-устройствам,
 * конфигурацией сенсоров, таблицей калибровки, управлением паролем и состоянием UI.
 * Используется в архитектуре MVVM для взаимодействия между UI и слоями данных.
 *
 * <p>Основные обязанности:</p>
 * <ul>
 *     <li>Сканирование и подключение к Bluetooth-устройствам</li>
 *     <li>Работа с таблицей калибровки и конфигурацией сенсоров</li>
 *     <li>Назначение устройств на оси и стороны</li>
 *     <li>Проверка и установка пароля</li>
 *     <li>Отслеживание состояния конфигурации и сохранения</li>
 * </ul>
 */
@HiltViewModel
public class DeviceViewModel extends ViewModel {
    private final SubmitPasswordUseCase submitPasswordUseCase;
    private final SaveCalibrationTableUseCase saveUseCase;
    private final BluetoothRepository bluetoothRepository;
    private final PasswordRepository passwordRepository;
    private final DeviceRepository deviceRepository;
    private final AxisDevicesStateCombiner combiner;

    @Inject
    public DeviceViewModel(
            BluetoothRepository bluetoothRepository,
            DeviceRepository deviceRepository,
            SaveCalibrationTableUseCase saveUseCase,
            SubmitPasswordUseCase submitPasswordUseCase,
            PasswordRepository passwordRepository,
            AxisDevicesStateCombiner combiner
    ) {
        this.bluetoothRepository = bluetoothRepository;
        this.deviceRepository = deviceRepository;
        this.saveUseCase = saveUseCase;
        this.submitPasswordUseCase = submitPasswordUseCase;
        this.passwordRepository = passwordRepository;
        this.combiner = combiner;

    }

    public LiveData<Boolean> isConnectedLiveData() {
        return bluetoothRepository.isConnectedLiveData();
    }

    public LiveData<List<Device>> getScannedDevices() {
        return bluetoothRepository.getScannedDevices();
    }

    public LiveData<List<Device>> getBtComMiniDevices() {
        return bluetoothRepository.getBtComMiniDevices();
    }

    public void clearScannedDevices() {
        bluetoothRepository.clearScannedDevices();
    }

    public void startScan(DeviceType deviceType) {
        bluetoothRepository.startScan(deviceType);
    }

    public void stopScan() {
        bluetoothRepository.stopScan();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void connectToDevice(Device device) {
        bluetoothRepository.connectToDevice(device);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void disconnect() {
        bluetoothRepository.disconnect();
    }

    public LiveData<DeviceDetails> getDeviceDetails() {
        return bluetoothRepository.getDeviceDetailsLiveData();
    }

    public void clearDetails() {
        bluetoothRepository.clearDetails();
    }

    public LiveData<SensorConfig> getSensorConfigure() {
        return bluetoothRepository.getSensorConfigureLiveData();
    }

    public void saveSensorConfiguration() {
        if (getSensorConfigure().getValue() != null) {
            bluetoothRepository.saveConfiguration();
        }
    }

    public void rereadCalibrationTable() {
        bluetoothRepository.rereadCalibrationTable();
    }

    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return bluetoothRepository.getCalibrationTable();
    }

    public void setCalibrationTable(List<CalibrationTable> table) {
        bluetoothRepository.setCalibrationTable(table);
    }

    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        bluetoothRepository.updateVirtualPoint(deviceDetails);
    }

    public void deletePoint(CalibrationTable item) {
        bluetoothRepository.deletePoint(item);
    }

    public void addPoint(CalibrationTable newPoint) {
        bluetoothRepository.addPoint(newPoint);
    }

    public int saveTable() {
        return saveUseCase.execute();
    }

    public LiveData<List<AxisModel>> getAxisList() {
        return deviceRepository.getAxisList();
    }

    public void setLoadedAxisList(List<AxisModel> list) {
        deviceRepository.setLoadedAxisList(list);
    }

    public void setDeviceToAxis(String mac, int axisNumber, String side) {
        deviceRepository.setDeviceToAxis(axisNumber, AxisSide.valueOf(side), mac);
    }

    public void resetDevicesForAxis(int axisNumber) {
        deviceRepository.resetDevicesForAxis(axisNumber);
    }

    public String getMacForAxisSide(int axisNumber, AxisSide side) {
        return deviceRepository.getMacForAxisSide(axisNumber, side);
    }

    public void onClick(int axisNumber, AxisSide side) {
        deviceRepository.onWheelClicked(axisNumber, side);
    }

    public LiveData<Event<InstalationPoint>> getAxisClick() {
        return deviceRepository.getAxisClick();
    }

    public Set<String> getMacsForAxis(int axisNumber) {
        return deviceRepository.getMacsForAxis(axisNumber);
    }

    public void onConfigureClicked(String input) {
        deviceRepository.onConfigureClicked(input);
    }

    public LiveData<Boolean> getAllDevicesSaved() {
        return combiner.getAllDevicesSaved();
    }

    public LiveData<Boolean> getIsPasswordDialogVisible() {
        return passwordRepository.getIsPasswordDialogVisible();
    }

    public void setPasswordDialogVisible(boolean visible) {
        passwordRepository.setPasswordDialogVisible(visible);
    }

    public LiveData<Void> getShowPasswordDialogEvent() {
        return passwordRepository.getShowPasswordDialogEvent();
    }

    public void requestPasswordInput() {
        passwordRepository.requestPasswordInput();
    }

    public void submitPassword(String password) {
        submitPasswordUseCase.execute(password);
        bluetoothRepository.clearPasswordDialogShown();
    }

    public boolean isPasswordSet() {
        return passwordRepository.isSet();
    }

    public void clearPassword() {
        passwordRepository.clear();
    }

    public void setPasswordListener(PasswordDialogListener listener) {
        bluetoothRepository.setPasswordListener(listener);
    }

    public void clearPasswordDialogShown() {
        bluetoothRepository.clearPasswordDialogShown();
    }

    public LiveData<Boolean> getSelectionModeLiveData() {
        return deviceRepository.getSelectionModeLiveData();
    }

    public void setSelectionMode(boolean isSelection) {
        deviceRepository.setSelectionMode(isSelection);
    }

    public LiveData<String> getMessage() {
        return deviceRepository.getMessage();
    }

    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return deviceRepository.getScannedDevicesLiveData();
    }

    public void updateScannedDevices(List<Device> newDevices) {
        deviceRepository.updateScannedDevices(newDevices);
    }

    public void markMacAsSelected(Device device) {
        deviceRepository.markMacAsSelected(device);
    }

    public void resetSelectedDevices() {
        deviceRepository.resetSelectedDevices();
    }

    public void resetSelectedDevicesByMacs(Set<String> macs) {
        deviceRepository.resetSelectedDevicesByMacs(macs);
    }

    public LiveData<Set<String>> getFinishedMacs() {
        return deviceRepository.getConfiguredMacs();
    }

    public void addFinishedMac(String mac) {
        deviceRepository.addConfiguredMac(mac);
    }

    public void clearMacs() {
        deviceRepository.clearMacs();
    }

    public void setLastFinishedMac(String mac) {
        deviceRepository.setLastConfiguredMac(mac);
    }

    public LiveData<String> getLastFinishedMac() {
        return deviceRepository.getLastConfiguredMac();
    }

    public LiveData<Boolean> getSavedStateLiveData() {
        return deviceRepository.getSavedStateLiveData();
    }

    public void markAsSaved() {
        deviceRepository.markAsSaved();
    }

    public void markAsUnsaved() {
        deviceRepository.markAsUnsaved();
    }

    public void setDeviceName(String name) {
        deviceRepository.setDeviceName(name);
    }

    public String getDeviceName() {
        return deviceRepository.getDeviceName();
    }

    public void setConfigurationSavedLive(boolean value) {
        bluetoothRepository.setConfigurationSavedLive(value);
    }

    public LiveData<Boolean> getConfigurationSavedLiveData() {
        return bluetoothRepository.getConfigurationSavedLiveData();
    }

    public LiveData<List<AxisUiModel>> getUiAxisModels() {
        return combiner.getUiAxisModels();
    }

    public void refreshScannedDevices() {
        deviceRepository.refreshScannedDevices();
    }

    public void resetPassword(boolean value) {
        bluetoothRepository.resetPassword(value);
    }

    public void setPassword(boolean value) {
        bluetoothRepository.setPassword(value);
    }

    public void setListener(GattReadListener listener) {
        bluetoothRepository.setListener(listener);
    }

    public void saveToBTCOMMini() {
        bluetoothRepository.saveToBTCOMMini();
    }

    public void setSaveToMiniLive(boolean value) {
        bluetoothRepository.setSaveToMiniLive(value);
    }

    public SingleLiveEvent<Boolean> getSaveToMiniLive() {
        return bluetoothRepository.getSaveToMiniLive();
    }

    public void setDeviceInfoToSave(DeviceInfoToSave info) {
        bluetoothRepository.setDeviceInfoToSave(info);
    }

    public LiveData<DeviceInfoToSave> getDeviceInfoToSave() {
        return bluetoothRepository.getDeviceInfoToSave();
    }

    public LiveData<List<AxisModel>> getUiAxisList() {
        return bluetoothRepository.getUiAxisList();
    }

    public void setUiAxisList(List<AxisModel> list) {
        bluetoothRepository.setUiAxisList(list);
    }
}