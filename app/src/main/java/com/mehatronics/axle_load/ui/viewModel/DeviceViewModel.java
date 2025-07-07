package com.mehatronics.axle_load.ui.viewModel;


import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.data.repository.BluetoothRepository;
import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.domain.usecase.SaveCalibrationTableUseCase;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel для управления взаимодействием с устройством через Bluetooth.
 * Используется в архитектуре MVVM для предоставления данных и бизнес-логики представлению.
 * <p>
 * Этот класс обрабатывает:
 * <ul>
 *     <li>Подключение и отключение устройств</li>
 *     <li>Сканирование устройств по типу</li>
 *     <li>Получение и очистку информации об устройстве</li>
 *     <li>Чтение и изменение таблицы калибровки</li>
 *     <li>Сохранение конфигурации сенсоров</li>
 * </ul>
 */
@HiltViewModel
public class DeviceViewModel extends ViewModel {
    private final BluetoothRepository bluetoothRepository;
    private final DeviceRepository deviceRepository;
    private final SaveCalibrationTableUseCase saveUseCase;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param bluetoothRepository Репозиторий для работы с Bluetooth-устройствами
     * @param saveUseCase         UseCase для сохранения таблицы калибровки
     */
    @Inject
    public DeviceViewModel(BluetoothRepository bluetoothRepository, DeviceRepository deviceRepository,
                           SaveCalibrationTableUseCase saveUseCase
    ) {
        this.bluetoothRepository = bluetoothRepository;
        this.deviceRepository = deviceRepository;
        this.saveUseCase = saveUseCase;
    }

    /**
     * @return LiveData, указывающая на состояние подключения к устройству
     */
    public LiveData<Boolean> isConnectedLiveData() {
        return bluetoothRepository.isConnectedLiveData();
    }

    /**
     * @return LiveData со списком найденных Bluetooth-устройств
     */
    public LiveData<List<Device>> getScannedDevices() {
        return bluetoothRepository.getScannedDevices();
    }

    /**
     * Очищает список отсканированных устройств
     */
    public void clearScannedDevices() {
        bluetoothRepository.clearScannedDevices();
    }

    /**
     * Запускает сканирование Bluetooth-устройств по указанному типу
     *
     * @param deviceType Тип устройства для сканирования
     */
    public void startScan(DeviceType deviceType) {
        bluetoothRepository.startScan(deviceType);
    }

    /**
     * Останавливает сканирование Bluetooth-устройств
     */
    public void stopScan() {
        bluetoothRepository.stopScan();
    }

    /**
     * @return LiveData с подробной информацией о подключенном устройстве
     */
    public LiveData<DeviceDetails> getDeviceDetails() {
        return bluetoothRepository.getDeviceDetailsLiveData();
    }

    /**
     * @return LiveData с текущей конфигурацией сенсора
     */
    public LiveData<SensorConfig> getSensorConfigure() {
        return bluetoothRepository.getSensorConfigureLiveData();
    }

    /**
     * Подключается к выбранному устройству
     *
     * @param device Устройство для подключения
     */
    public void connectToDevice(Device device) {
        bluetoothRepository.connectToDevice(device);
    }

    /**
     * Отключается от текущего устройства
     */
    public void disconnect() {
        bluetoothRepository.disconnect();
    }

    /**
     * Очищает информацию о текущем устройстве
     */
    public void clearDetails() {
        bluetoothRepository.clearDetails();
    }

    /**
     * Повторно запрашивает таблицу калибровки с устройства
     */
    public void rereadCalibrationTable() {
        bluetoothRepository.rereadCalibrationTable();
    }

    /**
     * @return LiveData со списком точек таблицы калибровки
     */
    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return bluetoothRepository.getCalibrationTable();
    }

    /**
     * Обновляет виртуальную точку в таблице калибровки
     *
     * @param deviceDetails Детали устройства, содержащие информацию о виртуальной точке
     */
    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        bluetoothRepository.updateVirtualPoint(deviceDetails);
    }

    /**
     * Удаляет указанную точку из таблицы калибровки
     *
     * @param item Точка калибровки для удаления
     */
    public void deletePoint(CalibrationTable item) {
        bluetoothRepository.deletePoint(item);
    }

    /**
     * Добавляет новую точку в таблицу калибровки
     *
     * @param newPoint Новая точка для добавления
     */
    public void addPoint(CalibrationTable newPoint) {
        bluetoothRepository.addPoint(newPoint);
    }

    /**
     * Сохраняет конфигурацию сенсора, если она доступна
     */
    public void saveSensorConfiguration() {
        if (getSensorConfigure().getValue() != null) {
            bluetoothRepository.saveConfiguration();
        }
    }

    /**
     * Выполняет сохранение таблицы калибровки через соответствующий UseCase
     *
     * @return Результат выполнения операции сохранения
     */
    public int saveTable() {
        return saveUseCase.execute();
    }


    public LiveData<List<AxisModel>> getAxisList() {
        return deviceRepository.getAxisList();
    }

    public LiveData<String> getMessage() {
        return deviceRepository.getMessage();
    }

    public void setDeviceToAxis(int axisNumber, AxisSide side, String mac) {
        deviceRepository.setDeviceToAxis(axisNumber, side, mac);
    }

    public void resetDevicesForAxis(int axisNumber) {
        deviceRepository.resetDevicesForAxis(axisNumber);
    }

    public String getMacForAxisSide(int axisNumber, AxisSide side) {
        return deviceRepository.getMacForAxisSide(axisNumber, side);
    }

    public void onConfigureClicked(String input) {
        deviceRepository.onConfigureClicked(input);
    }

    public LiveData<Event<InstalationPoint>> getAxisClick() {
        return deviceRepository.getAxisClick();
    }

    public void onClick(int axisNumber, AxisSide side) {
        deviceRepository.onWheelClicked(axisNumber, side);
    }

    public Set<String> getMacsForAxis(int axisNumber) {
        return deviceRepository.getMacsForAxis(axisNumber);
    }

    public void setSnackBarCallback(MessageCallback messageCallback) {
        deviceRepository.setSnackBarCallback(messageCallback);
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

    public void method(LifecycleOwner owner) {
        getAxisList().observe(owner, list
                -> Log.d("MyTag", String.valueOf(list.stream()
                .flatMap(axis -> axis.getSideDeviceMap()
                        .values()
                        .stream()
                ).collect(Collectors.toSet()))));
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
}


