package com.mehatronics.axle_load.ble.handler;

import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.ble.manager.GattConnectionManager;
import com.mehatronics.axle_load.ble.parser.GattDataParser;
import com.mehatronics.axle_load.ble.processor.GattReadProcessor;
import com.mehatronics.axle_load.ble.processor.GattWriteProcessor;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.state.CommandStateHandler;
import com.mehatronics.axle_load.state.impl.CommandAfterAuth;
import com.mehatronics.axle_load.state.impl.FirstAuthCommandState;

import javax.inject.Inject;

/**
 * Handles Bluetooth GATT callback events and manages communication with the BLE device.
 * <p>
 * This class manages the connection state changes, services discovery, characteristic read/write events,
 * and coordinates reading/writing operations via {@link GattReadProcessor} and {@link GattWriteProcessor}.
 * It also manages the command state machine using {@link CommandStateHandler}.
 * <p>
 * Provides LiveData to observe device connection status, device details, and sensor configuration.
 */
public class BluetoothGattCallbackHandler extends BluetoothGattCallback {

    private final GattConnectionManager connectionManager;
    private final GattReadProcessor gattReadProcessor;
    private final GattWriteProcessor writeProcessor;
    private final GattDataParser gattDataParser;
    private CommandStateHandler stateHandler;

    /**
     * Constructs the BluetoothGattCallbackHandler with injected dependencies.
     *
     * @param connectionManager manages BLE connection state
     * @param gattReadProcessor processes GATT read operations and parsing
     * @param writeProcessor    processes GATT write operations
     * @param gattDataParser    parses and serializes data to/from the BLE device
     * @param stateHandler      current command state handler for managing BLE commands
     */
    @Inject
    public BluetoothGattCallbackHandler(
            GattConnectionManager connectionManager,
            GattReadProcessor gattReadProcessor,
            GattWriteProcessor writeProcessor,
            GattDataParser gattDataParser,
            CommandStateHandler stateHandler
    ) {
        this.connectionManager = connectionManager;
        this.gattReadProcessor = gattReadProcessor;
        this.writeProcessor = writeProcessor;
        this.gattDataParser = gattDataParser;
        this.stateHandler = stateHandler;
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
            connectionManager.onConnected(gatt);
            updateStateAfterConnect();
        } else if (isStatusOk(newState, BluetoothGatt.STATE_DISCONNECTED)) {
            connectionManager.onDisconnected();
            resetStateAfterDisconnect();
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
            gattReadProcessor.readAllCharacteristics(gatt);
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
            gattReadProcessor.handleRead(gatt, characteristic);
            if (!gattReadProcessor.isReadingAll()) {
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
                gattReadProcessor.readNextAfterWrite(gatt);
            }
        }
    }

    /**
     * Returns the current page of the calibration table being handled.
     *
     * @return the current table page index
     */
    public int getTablePage() {
        return gattReadProcessor.getTablePage();
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
        return gattReadProcessor.getDeviceDetailsLiveData();
    }

    /**
     * Updates the current device details LiveData.
     *
     * @param details the new device details
     */
    public void setDeviceDetailsLiveData(DeviceDetails details) {
        gattReadProcessor.setDeviceDetailsLiveData(details);
    }

    /**
     * Returns LiveData holding the current sensor configuration.
     *
     * @return LiveData<SensorConfig> with sensor configuration
     */
    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return gattReadProcessor.getSensorConfigureLiveData();
    }

    /**
     * Clears device details from the internal processor.
     */
    public void clearDetails() {
        gattReadProcessor.clearDetails();
    }

    /**
     * Resets internal state to the initial authorization command state and clears read processor state.
     */
    public void resetState() {
        setCommandState(new FirstAuthCommandState());
        gattReadProcessor.updateState(false);
    }

    /**
     * Sets whether the configuration has been saved to the device.
     *
     * @param value true if saved, false otherwise
     */
    public void setConfigurationSaved(boolean value) {
        gattReadProcessor.setConfigurationSaved(value);
    }

    /**
     * Returns whether the configuration has been saved.
     *
     * @return true if configuration is saved, false otherwise
     */
    public boolean isConfigurationSaved() {
        return gattReadProcessor.isConfigurationSaved();
    }

    /**
     * Sets whether the calibration table has been saved to the device.
     *
     * @param value true if saved, false otherwise
     */
    public void setTableSaved(boolean value) {
        gattReadProcessor.setTableSaved(value);
    }

    /**
     * Returns whether the calibration table has been saved.
     *
     * @return true if the table is saved, false otherwise
     */
    public boolean isTableSaved() {
        return gattReadProcessor.isTableSaved();
    }

    /**
     * Initiates writing data to the BLE device characteristic.
     * Clears the write buffer, executes the current command state handler,
     * then writes the data.
     *
     * @param gatt the BluetoothGatt instance
     */
    public void writeToCharacteristic(BluetoothGatt gatt) {
        writeProcessor.clearBuffer();
        stateHandler.handle(gatt, this);
        writeProcessor.write(gatt);
    }

    /**
     * Requests rereading of the calibration table after successful authentication.
     * Sets command state accordingly.
     */
    public void rereadCalibrationTable() {
        setCommandState(new CommandAfterAuth());
        gattReadProcessor.rereadCalibrationTable();
    }

    /**
     * Prepares and saves the current sensor configuration to the write buffer.
     * Marks configuration as unsaved until written.
     */
    public void saveConfiguration() {
        var sensorConfig = gattReadProcessor.getSensorConfigureLiveData().getValue();
        if (sensorConfig == null) {
            return;
        }
        gattDataParser.setConfigureSettings(sensorConfig, writeProcessor.getBuffer());
        gattReadProcessor.setConfigurationSaved(false);
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
        var buffer = writeProcessor.getBuffer();

        int writtenPage = gattDataParser.setCalibrationTable(table, buffer, page);
        gattReadProcessor.setTablePage(writtenPage);

        if (getTablePage() < 0) {
            gattReadProcessor.setTableSaved(false);
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
        writeProcessor.setCommand(commandFirst, commandSecond);
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
        gattReadProcessor.updateState(true);
        Log.d("MyTag", "Connected to device");
    }

    /**
     * Resets internal state after disconnecting from device.
     */
    private void resetStateAfterDisconnect() {
        gattReadProcessor.updateState(false);
        Log.d("MyTag", "Disconnected from device");
    }
}
