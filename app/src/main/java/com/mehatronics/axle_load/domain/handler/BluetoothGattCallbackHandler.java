package com.mehatronics.axle_load.domain.handler;

import static com.mehatronics.axle_load.constants.UuidConstants.UUID_MAP;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.data.mapper.GattDataMapper;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.data.service.GattReadService;
import com.mehatronics.axle_load.data.service.GattWriteService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.domain.manager.GattConnectionManager;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;
import com.mehatronics.axle_load.domain.state.impl.CommandAfterAuth;
import com.mehatronics.axle_load.domain.state.impl.FirstAuthCommandState;
import com.mehatronics.axle_load.helper.SingleLiveEvent;
import com.mehatronics.axle_load.ui.adapter.listener.GattReadListener;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordDialogListener;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Обработчик колбэков Bluetooth GATT.
 * <p>
 * Управляет событиями подключения, обнаружения сервисов, чтения и записи характеристик.
 * Координирует работу сервисов чтения и записи ({@link GattReadService} и {@link GattWriteService}),
 * а также управляет состояниями команд через {@link CommandStateHandler}.
 * <p>
 * Предоставляет LiveData для наблюдения за состоянием подключения, деталями устройства и конфигурацией сенсора.
 */
public class BluetoothGattCallbackHandler extends BluetoothGattCallback {

    /**
     * Репозиторий паролей для авторизации устройства
     */
    private final PasswordRepository passwordRepository;

    /**
     * Менеджер управления состоянием подключения BLE
     */
    private final GattConnectionManager connectionManager;

    /**
     * Сервис для записи данных в BLE-устройство
     */
    private final GattWriteService gattWriteService;

    /**
     * Сервис для чтения данных с BLE-устройства
     */
    private final GattReadService gattReadService;

    /**
     * Маппер для преобразования данных BLE в доменные объекты
     */
    private final GattDataMapper gattDataMapper;

    private final DeviceTypeRepository repository;

    /**
     * Обработчик событий подключения и повторных подключений
     */
    private ConnectionHandler connectionHandler;

    /**
     * Текущий обработчик состояния команд
     */
    private CommandStateHandler stateHandler;

    /**
     * Слушатель событий диалога ввода пароля
     */
    private PasswordDialogListener passwordDialogListener;

    /**
     * Флаг, показывающий, был ли уже показан диалог ввода пароля
     */
    private boolean passwordDialogShown = false;

    /**
     * Флаг, указывающий, идет ли сейчас операция записи
     */
    private boolean isWritePending = false;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param passwordRepository репозиторий паролей
     * @param connectionManager  менеджер подключения
     * @param gattReadService    сервис чтения GATT
     * @param gattWriteService   сервис записи GATT
     * @param gattDataMapper     маппер данных
     * @param stateHandler       обработчик состояния команд
     */
    @Inject
    public BluetoothGattCallbackHandler(
            PasswordRepository passwordRepository,
            GattConnectionManager connectionManager,
            GattReadService gattReadService,
            GattWriteService gattWriteService,
            GattDataMapper gattDataMapper,
            CommandStateHandler stateHandler,
            DeviceTypeRepository repository
    ) {
        this.passwordRepository = passwordRepository;
        this.connectionManager = connectionManager;
        this.gattReadService = gattReadService;
        this.gattWriteService = gattWriteService;
        this.gattDataMapper = gattDataMapper;
        this.stateHandler = stateHandler;
        this.repository = repository;
    }

    /**
     * Вызывается при изменении состояния подключения.
     * При подключении обновляет состояние менеджера и внутренние флаги.
     * При отключении инициирует переподключение.
     *
     * @param gatt     экземпляр BluetoothGatt
     * @param status   статус изменения состояния
     * @param newState новое состояние подключения
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (isStatusOk(newState, BluetoothGatt.STATE_CONNECTED)) {
            connectionHandler.onConnected();
            connectionManager.onConnected(gatt);
            updateStateAfterConnect();
        } else if (isStatusOk(newState, BluetoothGatt.STATE_DISCONNECTED)) {
            connectionHandler.reconnect(gatt.getDevice());
        }
    }

    /**
     * Вызывается при обнаружении сервисов BLE-устройства.
     * Если обнаружение успешно, запускает чтение всех характеристик.
     *
     * @param gatt   экземпляр BluetoothGatt
     * @param status статус обнаружения сервисов
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            Log.d("MyTag", "Services discovered, starting to read all characteristics...");
            gattReadService.readAllCharacteristics(gatt);
        }
    }

    /**
     * Вызывается при завершении чтения характеристики.
     * Обрабатывает полученные данные и запускает запись, если необходимо.
     *
     * @param gatt           экземпляр BluetoothGatt
     * @param characteristic прочитанная характеристика
     * @param status         статус операции чтения
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            gattReadService.handleRead(gatt, characteristic);
            if (!gattReadService.isReadingAll()) {
                isWritePending = false;
                writeToCharacteristic(gatt);
            }
        }
    }

    /**
     * Вызывается при завершении записи характеристики.
     * Если запись успешна и UUID характеристики совпадает с WRITE_CHARACTERISTIC_DPS,
     * запускает чтение после записи.
     *
     * @param gatt           экземпляр BluetoothGatt
     * @param characteristic записанная характеристика
     * @param status         статус операции записи
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            UUID[] uuids = UUID_MAP.get(repository.getCurrDeviceType());
            if (uuids == null) return;
            if (characteristic.getUuid().equals(uuids[1])) {
                gattReadService.readNextAfterWrite(gatt);
            }
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gattReadService.rebuildDeviceDetails(gatt, rssi);
        }
    }

    /**
     * Получить репозиторий паролей.
     *
     * @return репозиторий паролей
     */
    public PasswordRepository getPasswordRepository() {
        return passwordRepository;
    }

    public DeviceTypeRepository getRepository() {
        return repository;
    }

    /**
     * Получить MAC-адрес текущего устройства.
     *
     * @return MAC-адрес в виде строки
     */
    public String getCurrentMac() {
        return gattReadService.getCurrentMac();
    }

    /**
     * Установить слушатель диалога ввода пароля.
     *
     * @param listener слушатель {@link PasswordDialogListener}
     */
    public void setPasswordDialogListener(PasswordDialogListener listener) {
        this.passwordDialogListener = listener;
    }

    /**
     * Уведомляет, что требуется ввод пароля.
     * Показывает диалог только если он еще не был показан.
     */
    public void notifyPasswordRequired() {
        if (!passwordDialogShown && passwordDialogListener != null) {
            passwordDialogShown = true;
            passwordDialogListener.onPasswordRequired();
        }
    }

    /**
     * Сбрасывает флаг отображения диалога ввода пароля,
     * чтобы диалог мог быть показан повторно при необходимости.
     */
    public void clearPasswordDialogShown() {
        passwordDialogShown = false;
    }

    /**
     * Установить обработчик переподключения.
     *
     * @param connectionHandler обработчик {@link ConnectionHandler}
     */
    public void setReconnectDelegate(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Получить текущую страницу таблицы калибровки.
     *
     * @return номер страницы
     */
    public int getTablePage() {
        return gattReadService.getTablePage();
    }

    /**
     * Получить LiveData для отслеживания состояния подключения.
     *
     * @return LiveData с булевым значением подключения
     */
    public LiveData<Boolean> isConnectedLiveData() {
        return connectionManager.getConnectionStatus();
    }

    /**
     * Получить LiveData с деталями устройства.
     *
     * @return LiveData с {@link DeviceDetails}
     */
    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return gattReadService.getDeviceDetailsLiveData();
    }

    /**
     * Установить детали устройства в LiveData.
     *
     * @param details новые детали устройства
     */
    public void setDeviceDetailsLiveData(DeviceDetails details) {
        gattReadService.setDeviceDetailsLiveData(details);
    }

    /**
     * Получить LiveData с конфигурацией сенсора.
     *
     * @return LiveData с {@link SensorConfig}
     */
    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return gattReadService.getSensorConfigureLiveData();
    }

    /**
     * Очистить данные деталей устройства.
     */
    public void clearDetails() {
        gattReadService.clearDetails();
    }

    /**
     * Сбросить состояние до начального состояния авторизации
     * и очистить состояние чтения.
     */
    public void resetState() {
        setCommandState(new FirstAuthCommandState());
        gattReadService.updateState(false);
    }

    /**
     * Установить флаг сохранения конфигурации.
     *
     * @param value true, если конфигурация сохранена
     */
    public void setConfigurationSaved(boolean value) {
        gattReadService.setConfigurationSaved(value);
    }

    /**
     * Проверить, сохранена ли конфигурация.
     *
     * @return true, если сохранена
     */
    public boolean isConfigurationSaved() {
        return gattReadService.isConfigurationSaved();
    }

    /**
     * Установить флаг сохранения таблицы калибровки.
     *
     * @param value true, если таблица сохранена
     */
    public void setTableSaved(boolean value) {
        gattReadService.setTableSaved(value);
    }

    /**
     * Проверить, сохранена ли таблица калибровки.
     *
     * @return true, если сохранена
     */
    public boolean isTableSaved() {
        return gattReadService.isTableSaved();
    }

    /**
     * Записать данные в характеристику BLE-устройства.
     * <p>
     * Если в данный момент идет запись, метод ничего не делает.
     * Иначе очищает буфер записи, вызывает обработчик состояния команд
     * для заполнения буфера, затем отправляет данные устройству.
     *
     * @param gatt экземпляр BluetoothGatt
     */
    public void writeToCharacteristic(BluetoothGatt gatt) {
        if (isWritePending) return;
        gattWriteService.clearBuffer();
        stateHandler.handle(this);
        gattWriteService.write(gatt);
        isWritePending = true;
    }

    /**
     * Инициирует повторное чтение таблицы калибровки после авторизации.
     * Устанавливает состояние команд в {@link CommandAfterAuth}.
     */
    public void rereadCalibrationTable() {
        setCommandState(new CommandAfterAuth());
        gattReadService.rereadCalibrationTable();
    }

    /**
     * Подготавливает и сохраняет текущую конфигурацию сенсора в буфер записи.
     * Отмечает конфигурацию как несохраненную.
     */
    public void saveConfiguration() {
        var sensorConfig = gattReadService.getSensorConfigureLiveData().getValue();
        if (sensorConfig == null) {
            return;
        }
        gattDataMapper.setConfigureSettings(sensorConfig, gattWriteService.getBuffer());
        gattReadService.setConfigurationSaved(false);
    }

    /**
     * Сохраняет текущую таблицу калибровки в устройство.
     * Обновляет номер сохраненной страницы и при необходимости запускает повторное чтение.
     */
    public void saveTableToSensor() {
        var deviceDetails = getDeviceDetailsLiveData().getValue();
        if (deviceDetails == null) return;

        var table = deviceDetails.getTable();
        var page = getTablePage();
        var buffer = gattWriteService.getBuffer();

        int writtenPage = gattDataMapper.setCalibrationTable(table, buffer, page);
        gattReadService.setTablePage(writtenPage);

        if (getTablePage() < 0) {
            gattReadService.setTableSaved(false);
            rereadCalibrationTable();
        }
    }

    /**
     * Устанавливает байты команды для отправки устройству.
     *
     * @param commandFirst  первый байт команды
     * @param commandSecond второй байт команды
     */
    public void setCommand(int commandFirst, int commandSecond) {
        gattWriteService.setCommand(commandFirst, commandSecond);
    }

    /**
     * Устанавливает текущий обработчик состояния команд.
     *
     * @param newState новый обработчик {@link CommandStateHandler}
     */
    public void setCommandState(CommandStateHandler newState) {
        this.stateHandler = newState;
    }

    /**
     * Проверяет, равен ли фактический статус ожидаемому.
     *
     * @param actual   фактический статус
     * @param expected ожидаемый статус
     * @return true, если статусы совпадают
     */
    private boolean isStatusOk(int actual, int expected) {
        return actual == expected;
    }

    /**
     * Обновляет внутреннее состояние после успешного подключения.
     */
    private void updateStateAfterConnect() {
        gattDataMapper.resetAxisData();
        gattReadService.updateState(true);
        Log.d("MyTag", "Connected to device");
    }

    /**
     * Устанавливает LiveData-флаг сохранения конфигурации.
     *
     * @param value новое значение флага
     */
    public void setConfigurationSavedLive(boolean value) {
        gattReadService.setConfigurationSavedLive(value);
    }

    /**
     * Получает LiveData-флаг сохранения конфигурации.
     *
     * @return LiveData<Boolean> с состоянием сохранения конфигурации
     */
    public LiveData<Boolean> getConfigurationSavedLiveData() {
        return gattReadService.getConfigurationSavedLiveData();
    }

    public void resetPassword(boolean value) {
        gattReadService.resetPassword(value);
    }

    public boolean isResetPassword() {
        return gattReadService.isResetPassword();
    }

    public void setPassword(boolean value) {
        gattReadService.setPassword(value);
    }

    public boolean isPasswordSet() {
        return gattReadService.isPasswordSet();
    }

    public void setListener(GattReadListener listener) {
        stateHandler = new FirstAuthCommandState();
        gattReadService.setListener(listener);
    }

    public void setSavedToBTCOMMini(boolean value) {
        gattReadService.setSaveToBTCOMMini(value);
    }

    public boolean isSavedToBTCOMMini() {
        return gattReadService.isSavedToBTCOMMini();
    }

    public void saveToBTCOMMini() {
        var save = getDeviceInfoToSave().getValue();
        gattDataMapper.setBTCOMMiniSettings(save, gattWriteService.getBuffer());
        Log.d("MyTag", String.valueOf(save));

        gattReadService.setSaveToBTCOMMini(false);
    }

    public void setSaveToMiniLive(boolean value) {
        gattReadService.setSaveToMiniLive(value);
    }

    public SingleLiveEvent<Boolean> getSaveToMiniLive() {
        return gattReadService.getSaveToMiniLive();
    }

    public void setDeviceInfoToSave(DeviceInfoToSave info) {
        gattReadService.setDeviceInfoToSave(info);
    }

    public LiveData<DeviceInfoToSave> getDeviceInfoToSave() {
        return gattReadService.getDeviceInfoToSave();
    }

    public LiveData<List<AxisModel>> getUiAxisList() {
        return gattReadService.getUiAxisList();
    }

    public void setUiAxisList(List<AxisModel> list) {
        gattReadService.setUiAxisList(list);
    }

    public boolean isComplete() {
        return gattReadService.getIsComplete();
    }

    public boolean isSensorsNumberGreaterThenEight() {
        return gattReadService.isSensorsNumberGreaterThenEight();
    }
}