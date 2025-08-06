package com.mehatronics.axle_load.ui.viewModel;

import android.Manifest;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.data.dto.ConfiguredDeviceDTO;
import com.mehatronics.axle_load.data.mapper.ConfiguredDeviceMapper;
import com.mehatronics.axle_load.data.repository.BluetoothRepository;
import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.data.repository.impl.DeviceRepositoryImpl;
import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.domain.usecase.SaveCalibrationTableUseCase;
import com.mehatronics.axle_load.domain.usecase.SubmitPasswordUseCase;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordDialogListener;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ConfiguredDeviceMapper mapper;

    private final MediatorLiveData<Boolean> allDevicesSaved = new MediatorLiveData<>();

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param bluetoothRepository   Репозиторий Bluetooth
     * @param deviceRepository      Репозиторий устройств и осей
     * @param saveUseCase           UseCase сохранения таблицы калибровки
     * @param submitPasswordUseCase UseCase отправки пароля
     * @param passwordRepository    Репозиторий пароля
     */
    @Inject
    public DeviceViewModel(
            BluetoothRepository bluetoothRepository,
            DeviceRepositoryImpl deviceRepository,
            SaveCalibrationTableUseCase saveUseCase,
            SubmitPasswordUseCase submitPasswordUseCase,
            PasswordRepository passwordRepository,
            ConfiguredDeviceMapper mapper
    ) {
        this.bluetoothRepository = bluetoothRepository;
        this.deviceRepository = deviceRepository;
        this.saveUseCase = saveUseCase;
        this.submitPasswordUseCase = submitPasswordUseCase;
        this.passwordRepository = passwordRepository;
        this.mapper = mapper;

        allDevicesSaved.addSource(getAxisList(), list -> checkAllSaved());
        allDevicesSaved.addSource(getFinishedMacs(), macs -> checkAllSaved());

        uiAxisModels.addSource(getAxisList(), axisList -> combine(axisList, getScannedDevices().getValue()));
        uiAxisModels.addSource(getScannedDevices(), devices -> combine(getAxisList().getValue(), devices));
    }

    // === Bluetooth Device Management ===

    /**
     * Возвращает LiveData с состоянием подключения к Bluetooth-устройству.
     *
     * @return LiveData с булевым значением подключения
     */
    public LiveData<Boolean> isConnectedLiveData() {
        return bluetoothRepository.isConnectedLiveData();
    }

    /**
     * Возвращает LiveData со списком найденных Bluetooth-устройств.
     *
     * @return LiveData со списком устройств
     */
    public LiveData<List<Device>> getScannedDevices() {
        return bluetoothRepository.getScannedDevices();
    }

    /**
     * Очищает список отсканированных устройств.
     */
    public void clearScannedDevices() {
        bluetoothRepository.clearScannedDevices();
    }

    /**
     * Запускает сканирование Bluetooth-устройств указанного типа.
     *
     * @param deviceType Тип устройства для сканирования
     */
    public void startScan(DeviceType deviceType) {
        bluetoothRepository.startScan(deviceType);
    }

    /**
     * Останавливает сканирование Bluetooth-устройств.
     */
    public void stopScan() {
        bluetoothRepository.stopScan();
    }

    /**
     * Подключается к выбранному Bluetooth-устройству.
     *
     * @param device Устройство для подключения
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void connectToDevice(Device device) {
        bluetoothRepository.connectToDevice(device);
    }

    /**
     * Отключается от текущего устройства.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void disconnect() {
        bluetoothRepository.disconnect();
    }

    /**
     * Возвращает LiveData с деталями подключенного устройства.
     *
     * @return LiveData с DeviceDetails
     */
    public LiveData<DeviceDetails> getDeviceDetails() {
        return bluetoothRepository.getDeviceDetailsLiveData();
    }

    /**
     * Очищает информацию о текущем подключенном устройстве.
     */
    public void clearDetails() {
        bluetoothRepository.clearDetails();
    }

    // === Sensor & Calibration Configuration ===

    /**
     * Возвращает LiveData с текущей конфигурацией сенсора.
     *
     * @return LiveData с SensorConfig
     */
    public LiveData<SensorConfig> getSensorConfigure() {
        return bluetoothRepository.getSensorConfigureLiveData();
    }

    /**
     * Сохраняет конфигурацию сенсора, если конфигурация установлена.
     */
    public void saveSensorConfiguration() {
        if (getSensorConfigure().getValue() != null) {
            bluetoothRepository.saveConfiguration();
        }
    }

    /**
     * Повторно запрашивает таблицу калибровки с устройства.
     */
    public void rereadCalibrationTable() {
        bluetoothRepository.rereadCalibrationTable();
    }

    /**
     * Возвращает LiveData со списком точек таблицы калибровки.
     *
     * @return LiveData со списком CalibrationTable
     */
    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return bluetoothRepository.getCalibrationTable();
    }

    /**
     * Обновляет виртуальную точку в таблице калибровки на основе деталей устройства.
     *
     * @param deviceDetails Детали устройства
     */
    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        bluetoothRepository.updateVirtualPoint(deviceDetails);
    }

    /**
     * Удаляет указанную точку из таблицы калибровки.
     *
     * @param item Точка калибровки для удаления
     */
    public void deletePoint(CalibrationTable item) {
        bluetoothRepository.deletePoint(item);
    }

    /**
     * Добавляет новую точку в таблицу калибровки.
     *
     * @param newPoint Новая точка калибровки
     */
    public void addPoint(CalibrationTable newPoint) {
        bluetoothRepository.addPoint(newPoint);
    }

    /**
     * Сохраняет таблицу калибровки через соответствующий UseCase.
     *
     * @return Результат сохранения (например, количество ошибок)
     */
    public int saveTable() {
        return saveUseCase.execute();
    }

    /*=== Axis and Device Assignment ===*/

    /**
     * Возвращает LiveData со списком моделей осей.
     *
     * @return LiveData со списком AxisModel
     */
    public LiveData<List<AxisModel>> getAxisList() {
        return deviceRepository.getAxisList();
    }

    /**
     * Возвращает количество осей.
     *
     * @return Количество осей
     */
    public int getAxisCount() {
        return deviceRepository.getAxisCount();
    }

    /**
     * Назначает устройство по MAC-адресу на ось и сторону.
     *
     * @param mac        MAC-адрес устройства
     * @param axisNumber Номер оси
     * @param side       Сторона оси (левая/правая) в виде строки
     */
    public void setDeviceToAxis(String mac, int axisNumber, String side) {
        deviceRepository.setDeviceToAxis(axisNumber, AxisSide.valueOf(side), mac);
    }

    /**
     * Сбрасывает назначение устройств для указанной оси.
     *
     * @param axisNumber Номер оси
     */
    public void resetDevicesForAxis(int axisNumber) {
        deviceRepository.resetDevicesForAxis(axisNumber);
    }

    /**
     * Получает MAC-адрес устройства для заданной оси и стороны.
     *
     * @param axisNumber Номер оси
     * @param side       Сторона оси
     * @return MAC-адрес устройства или null
     */
    public String getMacForAxisSide(int axisNumber, AxisSide side) {
        return deviceRepository.getMacForAxisSide(axisNumber, side);
    }

    /**
     * Обработка клика по оси и стороне.
     *
     * @param axisNumber Номер оси
     * @param side       Сторона оси
     */
    public void onClick(int axisNumber, AxisSide side) {
        deviceRepository.onWheelClicked(axisNumber, side);
    }

    /**
     * Возвращает LiveData с событием клика по точке установки.
     *
     * @return LiveData с Event<InstalationPoint>
     */
    public LiveData<Event<InstalationPoint>> getAxisClick() {
        return deviceRepository.getAxisClick();
    }

    /**
     * Возвращает набор MAC-адресов для указанной оси.
     *
     * @param axisNumber Номер оси
     * @return Множество MAC-адресов устройств
     */
    public Set<String> getMacsForAxis(int axisNumber) {
        return deviceRepository.getMacsForAxis(axisNumber);
    }

    /**
     * Обрабатывает событие клика на кнопку конфигурации с заданным входным значением.
     *
     * @param input Входные данные для конфигурации
     */
    public void onConfigureClicked(String input) {
        deviceRepository.onConfigureClicked(input);
    }

    // === Device Saving State Tracking ===

    /**
     * Проверяет, что все назначенные устройства сохранены и обновляет состояние allDevicesSaved.
     */
    private void checkAllSaved() {
        List<AxisModel> axes = getAxisList().getValue();
        Set<String> finished = getFinishedMacs().getValue();

        if (axes == null || finished == null) {
            allDevicesSaved.setValue(false);
            return;
        }

        Set<String> selectedMacs = axes.stream()
                .flatMap(axis -> axis.getSideDeviceMap().values().stream())
                .filter(mac -> mac != null && !mac.isEmpty())
                .collect(Collectors.toSet());

        boolean saved = !axes.isEmpty()
                && axes.stream().allMatch(axis ->
                axis.getSideDeviceMap().values().stream().anyMatch(mac -> mac != null && !mac.isEmpty())
        )
                && finished.containsAll(selectedMacs)
                && selectedMacs.containsAll(finished);

        allDevicesSaved.setValue(saved);
    }

    /**
     * Возвращает LiveData с состоянием, что все устройства сохранены.
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getAllDevicesSaved() {
        return allDevicesSaved;
    }

    // === Password Handling ===

    public LiveData<Boolean> getIsPasswordDialogVisible() {
        return passwordRepository.getIsPasswordDialogVisible();
    }

    public void setPasswordDialogVisible(boolean visible) {
        passwordRepository.setPasswordDialogVisible(visible);
    }

    /**
     * Возвращает LiveData события для показа диалога ввода пароля.
     *
     * @return LiveData<Void>
     */
    public LiveData<Void> getShowPasswordDialogEvent() {
        return passwordRepository.getShowPasswordDialogEvent();
    }

    /**
     * Запрашивает показ диалога для ввода пароля.
     */
    public void requestPasswordInput() {
        passwordRepository.requestPasswordInput();
    }

    /**
     * Отправляет введённый пароль через UseCase.
     *
     * @param password Введённый пароль
     */
    public void submitPassword(String password) {
        submitPasswordUseCase.execute(password);
        bluetoothRepository.clearPasswordDialogShown();
    }

    /**
     * Проверяет, установлен ли пароль.
     *
     * @return true, если пароль установлен; false — иначе
     */
    public boolean isPasswordSet() {
        return passwordRepository.isSet();
    }

    /**
     * Очищает сохранённый пароль.
     */
    public void clearPassword() {
        passwordRepository.clear();
    }

    /**
     * Сбрасывает пароль и флаг установки.
     *
     * @param view Не используется, параметр для возможности использования в XML
     */
    public void resetPassword(View view) {
        passwordRepository.clear();
        passwordRepository.setFlag(false);
    }

    /**
     * Инициирует установку нового пароля (вызывает диалог).
     *
     * @param view Не используется, параметр для возможности использования в XML
     */
    public void setNewPassword(View view) {
        requestPasswordInput();
    }

    /**
     * Устанавливает слушатель событий диалога ввода пароля.
     *
     * @param listener Слушатель PasswordDialogListener
     */
    public void setPasswordListener(PasswordDialogListener listener) {
        bluetoothRepository.setPasswordListener(listener);
    }

    /**
     * Очищает флаг, что диалог ввода пароля уже был показан.
     */
    public void clearPasswordDialogShown() {
        bluetoothRepository.clearPasswordDialogShown();
    }

    // === UI and Debug Helpers ===

    /**
     * Вспомогательный метод для логирования списка MAC-адресов устройств на осях.
     *
     * @param owner LifecycleOwner для привязки наблюдателя
     */
    public void method(LifecycleOwner owner) {
        getAxisList().observe(owner, list ->
                Log.d("MyTag", String.valueOf(list.stream()
                        .flatMap(axis -> axis.getSideDeviceMap()
                                .values()
                                .stream()
                        ).collect(Collectors.toList()))));
    }

    /**
     * Возвращает LiveData режима выбора устройств.
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getSelectionModeLiveData() {
        return deviceRepository.getSelectionModeLiveData();
    }

    /**
     * Устанавливает режим выбора устройств.
     *
     * @param isSelection true — режим выбора включён; false — выключен
     */
    public void setSelectionMode(boolean isSelection) {
        deviceRepository.setSelectionMode(isSelection);
    }

    /**
     * Возвращает LiveData с сообщениями для отображения.
     *
     * @return LiveData<String>
     */
    public LiveData<String> getMessage() {
        return deviceRepository.getMessage();
    }

    /**
     * Возвращает LiveData со списком отсканированных устройств из DeviceRepository.
     *
     * @return LiveData<List < Device>>
     */
    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return deviceRepository.getScannedDevicesLiveData();
    }

    /**
     * Обновляет список отсканированных устройств.
     *
     * @param newDevices Новый список устройств
     */
    public void updateScannedDevices(List<Device> newDevices) {
        deviceRepository.updateScannedDevices(newDevices);
    }

    /**
     * Отмечает устройство как выбранное.
     *
     * @param device Выбранное устройство
     */
    public void markMacAsSelected(Device device) {
        deviceRepository.markMacAsSelected(device);
    }

    /**
     * Сбрасывает выбранные устройства.
     */
    public void resetSelectedDevices() {
        deviceRepository.resetSelectedDevices();
    }

    /**
     * Сбрасывает выбранные устройства по заданному набору MAC-адресов.
     *
     * @param macs Множество MAC-адресов
     */
    public void resetSelectedDevicesByMacs(Set<String> macs) {
        deviceRepository.resetSelectedDevicesByMacs(macs);
    }

    /**
     * Возвращает LiveData с множеством MAC-адресов уже сконфигурированных устройств.
     *
     * @return LiveData<Set < String>>
     */
    public LiveData<Set<String>> getFinishedMacs() {
        return deviceRepository.getConfiguredMacs();
    }

    /**
     * Добавляет MAC-адрес в список сконфигурированных устройств.
     *
     * @param mac MAC-адрес устройства
     */
    public void addFinishedMac(String mac) {
        deviceRepository.addConfiguredMac(mac);
    }

    /**
     * Очищает список сконфигурированных MAC-адресов.
     */
    public void clearMacs() {
        deviceRepository.clearMacs();
    }

    /**
     * Устанавливает последний сконфигурированный MAC-адрес.
     *
     * @param mac MAC-адрес
     */
    public void setLastFinishedMac(String mac) {
        deviceRepository.setLastConfiguredMac(mac);
    }

    /**
     * Возвращает LiveData с последним сконфигурированным MAC-адресом.
     *
     * @return LiveData<String>
     */
    public LiveData<String> getLastFinishedMac() {
        return deviceRepository.getLastConfiguredMac();
    }

    /**
     * Возвращает LiveData состояния сохранения устройства.
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getSavedStateLiveData() {
        return deviceRepository.getSavedStateLiveData();
    }

    /**
     * Отмечает устройство как сохранённое.
     */
    public void markAsSaved() {
        deviceRepository.markAsSaved();
    }

    /**
     * Отмечает устройство как не сохранённое.
     */
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

    private final MediatorLiveData<List<AxisUiModel>> uiAxisModels = new MediatorLiveData<>();

    private void combine(List<AxisModel> axisList, List<Device> scannedDevices) {
        if (axisList == null || scannedDevices == null) return;

        List<ConfiguredDeviceDTO> dtos = scannedDevices.stream()
                .map(mapper::convertToConfiguredDevice)
                .collect(Collectors.toList());

        List<AxisUiModel> uiModels = axisList.stream()
                .map(axis -> mapper.toUiModel(axis, dtos))
                .collect(Collectors.toList());

        uiAxisModels.setValue(uiModels);
    }

    public LiveData<List<AxisUiModel>> getUiAxisModels() {
        return uiAxisModels;
    }
}