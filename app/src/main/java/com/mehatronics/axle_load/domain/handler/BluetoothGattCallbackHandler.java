package com.mehatronics.axle_load.domain.handler;

import static com.mehatronics.axle_load.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.manager.GattConnectionManager;
import com.mehatronics.axle_load.data.mapper.GattDataMapper;
import com.mehatronics.axle_load.data.service.GattReadService;
import com.mehatronics.axle_load.data.service.GattWriteService;
import com.mehatronics.axle_load.data.service.impl.GattReadServiceImpl;
import com.mehatronics.axle_load.data.service.impl.GattWriteServiceImpl;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;
import com.mehatronics.axle_load.domain.state.impl.CommandAfterAuth;
import com.mehatronics.axle_load.domain.state.impl.FirstAuthCommandState;

import javax.inject.Inject;

/**
 * Handles Bluetooth GATT callback events and manages communication with the BLE device.
 * <p>
 * This class manages the connection state changes, services discovery, characteristic read/write events,
 * and coordinates reading/writing operations via {@link GattReadServiceImpl} and {@link GattWriteServiceImpl}.
 * It also manages the command state machine using {@link CommandStateHandler}.
 * <p>
 * Provides LiveData to observe device connection status, device details, and sensor configuration.
 */
public class BluetoothGattCallbackHandler extends BluetoothGattCallback {

    private final GattConnectionManager connectionManager;
    private final GattWriteService gattWriteService;
    private final GattReadService gattReadService;
    private final GattDataMapper gattDataMapper;
    private ConnectionHandler connectionHandler;
    private CommandStateHandler stateHandler;

    /**
     * Constructs the BluetoothGattCallbackHandler with injected dependencies.
     *
     * @param connectionManager manages BLE connection state
     * @param gattReadService processes GATT read operations and parsing
     * @param gattWriteService    processes GATT write operations
     * @param gattDataMapper    parses and serializes data to/from the BLE device
     * @param stateHandler      current command state handler for managing BLE commands
     */
    @Inject
    public BluetoothGattCallbackHandler(
            GattConnectionManager connectionManager,
            GattReadService gattReadService,
            GattWriteService gattWriteService,
            GattDataMapper gattDataMapper,
            CommandStateHandler stateHandler
    ) {
        this.connectionManager = connectionManager;
        this.gattReadService = gattReadService;
        this.gattWriteService = gattWriteService;
        this.gattDataMapper = gattDataMapper;
        this.stateHandler = stateHandler;
    }

    public void setReconnectDelegate(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Called when the connection state changes (connected/disconnected).
     * Updates connection manager and internal state accordingly.
     *
     * @param gatt     the BluetoothGatt instance
     * @param status   status of the connection change
     * @param newState new connection state
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
     * Called when BLE services are discovered.
     * Initiates reading all characteristics on success.
     *
     * @param gatt   the BluetoothGatt instance
     * @param status status of the service discovery
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            Log.d("MyTag", "Services discovered, starting to read all characteristics...");
            gattReadService.readAllCharacteristics(gatt);
        }
    }

    /**
     * Called when a characteristic read operation completes.
     * Processes the read data and triggers next write if needed.
     *
     * @param gatt           the BluetoothGatt instance
     * @param characteristic the characteristic read
     * @param status         status of the read operation
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            gattReadService.handleRead(gatt, characteristic);
            if (!gattReadService.isReadingAll()) {
                writeToCharacteristic(gatt);
            }
        }
    }

    /**
     * Called when a characteristic write operation completes.
     * On success, continues reading after write if the characteristic matches.
     *
     * @param gatt           the BluetoothGatt instance
     * @param characteristic the characteristic written
     * @param status         status of the write operation
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            if (characteristic.getUuid().equals(WRITE_CHARACTERISTIC_DPS)) {
                gattReadService.readNextAfterWrite(gatt);
            }
        }
    }

    /**
     * Returns the current page of the calibration table being handled.
     *
     * @return the current table page index
     */
    public int getTablePage() {
        return gattReadService.getTablePage();
    }

    /**
     * Returns LiveData representing the connection status.
     *
     * @return LiveData<Boolean> with connection status
     */
    public LiveData<Boolean> isConnectedLiveData() {
        return connectionManager.getConnectionStatus();
    }

    /**
     * Returns LiveData holding the current device details.
     *
     * @return LiveData<DeviceDetails> with device details
     */
    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return gattReadService.getDeviceDetailsLiveData();
    }

    /**
     * Updates the current device details LiveData.
     *
     * @param details the new device details
     */
    public void setDeviceDetailsLiveData(DeviceDetails details) {
        gattReadService.setDeviceDetailsLiveData(details);
    }

    /**
     * Returns LiveData holding the current sensor configuration.
     *
     * @return LiveData<SensorConfig> with sensor configuration
     */
    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return gattReadService.getSensorConfigureLiveData();
    }

    /**
     * Clears device details from the internal processor.
     */
    public void clearDetails() {
        gattReadService.clearDetails();
    }

    /**
     * Resets internal state to the initial authorization command state and clears read processor state.
     */
    public void resetState() {
        setCommandState(new FirstAuthCommandState());
        gattReadService.updateState(false);
    }

    /**
     * Sets whether the configuration has been saved to the device.
     *
     * @param value true if saved, false otherwise
     */
    public void setConfigurationSaved(boolean value) {
        gattReadService.setConfigurationSaved(value);
    }

    /**
     * Returns whether the configuration has been saved.
     *
     * @return true if configuration is saved, false otherwise
     */
    public boolean isConfigurationSaved() {
        return gattReadService.isConfigurationSaved();
    }

    /**
     * Sets whether the calibration table has been saved to the device.
     *
     * @param value true if saved, false otherwise
     */
    public void setTableSaved(boolean value) {
        gattReadService.setTableSaved(value);
    }

    /**
     * Returns whether the calibration table has been saved.
     *
     * @return true if the table is saved, false otherwise
     */
    public boolean isTableSaved() {
        return gattReadService.isTableSaved();
    }

    /**
     * Initiates writing data to the BLE device characteristic.
     * Clears the write buffer, executes the current command state handler,
     * then writes the data.
     *
     * @param gatt the BluetoothGatt instance
     */
    public void writeToCharacteristic(BluetoothGatt gatt) {
        gattWriteService.clearBuffer();
        stateHandler.handle(gatt, this);
        gattWriteService.write(gatt);
    }

    /**
     * Requests rereading of the calibration table after successful authentication.
     * Sets command state accordingly.
     */
    public void rereadCalibrationTable() {
        setCommandState(new CommandAfterAuth());
        gattReadService.rereadCalibrationTable();
    }

    /**
     * Prepares and saves the current sensor configuration to the write buffer.
     * Marks configuration as unsaved until written.
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
     * Saves the current calibration table to the sensor.
     * Updates the saved page and triggers reread if the page is invalid.
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
     * Sets the command bytes that will be sent to the device.
     *
     * @param commandFirst  first command byte
     * @param commandSecond second command byte
     */
    public void setCommand(int commandFirst, int commandSecond) {
        gattWriteService.setCommand(commandFirst, commandSecond);
    }

    /**
     * Sets the current command state handler.
     *
     * @param newState the new CommandStateHandler instance
     */
    public void setCommandState(CommandStateHandler newState) {
        this.stateHandler = newState;
    }

    /**
     * Checks if a given status code matches the expected status.
     *
     * @param actual   the actual status code
     * @param expected the expected status code
     * @return true if equal, false otherwise
     */
    private boolean isStatusOk(int actual, int expected) {
        return actual == expected;
    }

    /**
     * Updates internal state after connecting to device.
     */
    private void updateStateAfterConnect() {
        gattReadService.updateState(true);
        Log.d("MyTag", "Connected to device");
    }

    /**
     * Resets internal state after disconnecting from device.
     */
    private void resetStateAfterDisconnect() {
        gattReadService.updateState(false);
        Log.d("MyTag", "Disconnected from device");
    }
}
