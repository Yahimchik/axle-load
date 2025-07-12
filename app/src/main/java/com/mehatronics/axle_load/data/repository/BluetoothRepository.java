package com.mehatronics.axle_load.data.repository;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.data.service.BleScannerService;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.domain.manager.BluetoothConnectionManager;
import com.mehatronics.axle_load.domain.manager.CalibrationTableManager;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordDialogListener;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Репозиторий для управления BLE-соединениями, сканированием устройств
 * и управлением калибровочными данными.
 * Инкапсулирует доступ к BluetoothConnectionManager, BleScanner и CalibrationTableManager.
 */
@Singleton
public class BluetoothRepository {
    private final BluetoothConnectionManager bluetoothConnectionManager;
    private final CalibrationTableManager calibrationTableManager;
    private final BleScannerService bleScannerService;


    /**
     * Конструктор репозитория с внедрением зависимостей.
     *
     * @param bluetoothConnectionManager Менеджер Bluetooth-соединений.
     * @param bleScannerService          Компонент для сканирования BLE-устройств.
     * @param calibrationTableManager    Менеджер таблицы калибровки.
     */
    @Inject
    public BluetoothRepository(BluetoothConnectionManager bluetoothConnectionManager,
                               BleScannerService bleScannerService,
                               CalibrationTableManager calibrationTableManager) {
        this.bluetoothConnectionManager = bluetoothConnectionManager;
        this.calibrationTableManager = calibrationTableManager;
        this.bleScannerService = bleScannerService;
    }

    /**
     * Возвращает LiveData со списком найденных устройств.
     *
     * @return LiveData со списком {@link Device}.
     */
    public LiveData<List<Device>> getScannedDevices() {
        return bleScannerService.getScannedDevices();
    }

    /**
     * Возвращает LiveData, отражающий состояние подключения.
     *
     * @return LiveData с булевым значением подключения.
     */
    public LiveData<Boolean> isConnectedLiveData() {
        return bluetoothConnectionManager.isConnectedLiveData();
    }

    /**
     * Очищает список найденных устройств.
     */
    public void clearScannedDevices() {
        bleScannerService.clearScannedDevices();
    }

    public void removeDeviceByAddress(Device device) {
        bleScannerService.removeDeviceByAddress(device.getDevice().getAddress());

    }

    /**
     * Запускает сканирование BLE-устройств заданного типа.
     *
     * @param deviceType Тип устройства для сканирования.
     */
    public void startScan(DeviceType deviceType) {
        bleScannerService.startScan(deviceType);
    }

    /**
     * Останавливает сканирование BLE-устройств.
     */
    public void stopScan() {
        bleScannerService.stopScan();
    }

    /**
     * Возвращает LiveData с деталями подключенного устройства.
     *
     * @return LiveData с {@link DeviceDetails}.
     */
    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return bluetoothConnectionManager.getDeviceDetailsLiveData();
    }

    /**
     * Устанавливает детали устройства в BluetoothConnectionManager.
     *
     * @param details Объект {@link DeviceDetails} с информацией об устройстве.
     */
    public void setDeviceDetailsLiveData(DeviceDetails details) {
        bluetoothConnectionManager.setDeviceDetailsLiveData(details);
    }

    /**
     * Возвращает LiveData с конфигурацией сенсора.
     *
     * @return LiveData с {@link SensorConfig}.
     */
    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return bluetoothConnectionManager.getSensorConfigureLiveData();
    }

    /**
     * Устанавливает соединение с устройством.
     *
     * @param device Объект {@link Device}, к которому необходимо подключиться.
     */
    public void connectToDevice(Device device) {
        bluetoothConnectionManager.connect(device);
        bleScannerService.removeDeviceByAddress(device.getMacAddress());
    }

    /**
     * Сохраняет текущую конфигурацию на устройстве.
     */
    public void saveConfiguration() {
        bluetoothConnectionManager.saveConfiguration();
    }

    /**
     * Сохраняет таблицу калибровки на сенсоре.
     */
    public void saveTableToSensor() {
        bluetoothConnectionManager.saveTable();
    }

    /**
     * Запрашивает повторное чтение таблицы калибровки с устройства.
     */
    public void rereadCalibrationTable() {
        bluetoothConnectionManager.rereadCalibrationTable();
    }

    /**
     * Разрывает соединение с устройством.
     */
    public void disconnect() {
        bluetoothConnectionManager.disconnect();
    }

    /**
     * Очищает детали текущего устройства.
     */
    public void clearDetails() {
        bluetoothConnectionManager.clearDetails();
    }

    /**
     * Возвращает LiveData с таблицей калибровки.
     *
     * @return LiveData со списком {@link CalibrationTable}.
     */
    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return calibrationTableManager.getCalibrationTable();
    }

    /**
     * Обновляет виртуальную точку в таблице калибровки.
     *
     * @param deviceDetails Детали устройства, для которого обновляется точка.
     */
    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        calibrationTableManager.updateVirtualPoint(deviceDetails);
    }

    /**
     * Удаляет точку калибровки.
     *
     * @param item Объект {@link CalibrationTable} для удаления.
     */
    public void deletePoint(CalibrationTable item) {
        calibrationTableManager.deletePoint(item);
    }

    /**
     * Добавляет новую точку в таблицу калибровки.
     *
     * @param newPoint Новая точка {@link CalibrationTable} для добавления.
     */
    public void addPoint(CalibrationTable newPoint) {
        calibrationTableManager.addPoint(newPoint);
    }

    /**
     * Конвертирует множитель калибровки.
     *
     * @return Целочисленное значение множителя.
     */
    public int convertMultiplier() {
        return calibrationTableManager.convertMultiplier();
    }

    public void setPasswordListener(PasswordDialogListener listener) {
        bluetoothConnectionManager.setPasswordListener(listener);
    }

    public void clearPasswordDialogShown() {
        bluetoothConnectionManager.clearPasswordDialogShown();
    }
}
