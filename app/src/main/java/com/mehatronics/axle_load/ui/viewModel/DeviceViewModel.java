package com.mehatronics.axle_load.ui.viewModel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.domain.usecase.SaveCalibrationTableUseCase;
import com.mehatronics.axle_load.data.repository.BluetoothRepository;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

import java.util.List;

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
 *
 */
@HiltViewModel
public class DeviceViewModel extends ViewModel {

    private final BluetoothRepository bluetoothRepository;
    private final SaveCalibrationTableUseCase saveUseCase;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param bluetoothRepository Репозиторий для работы с Bluetooth-устройствами
     * @param saveUseCase UseCase для сохранения таблицы калибровки
     */
    @Inject
    public DeviceViewModel(BluetoothRepository bluetoothRepository, SaveCalibrationTableUseCase saveUseCase) {
        this.bluetoothRepository = bluetoothRepository;
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
        } else {
            Log.w("MyTag", "Sensor config is null");
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
}


