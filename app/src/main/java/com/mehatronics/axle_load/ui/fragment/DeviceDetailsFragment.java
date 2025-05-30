package com.mehatronics.axle_load.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.notification.MessageCallback;
import com.mehatronics.axle_load.ui.binder.DeviceDetailsBinder;
import com.mehatronics.axle_load.domain.viewModel.DeviceViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Фрагмент для отображения и управления деталями подключенного Bluetooth-устройства.
 * Отвечает за отображение информации об устройстве, таблицы калибровки и конфигурации сенсора.
 * Использует {@link DeviceViewModel} для получения и обновления данных.
 */
@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment implements MessageCallback {

    private DeviceViewModel deviceViewModel;
    private DeviceDetailsBinder detailsBinder;
    private View view;

    /**
     * Инициализация ViewModel при создании фрагмента.
     *
     * @param savedInstanceState Состояние, сохранённое при предыдущем создании.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    /**
     * Создаёт и возвращает иерархию представлений фрагмента.
     *
     * @param inflater Инфлейтер для создания View из XML.
     * @param container Родительский ViewGroup.
     * @param savedInstanceState Состояние, сохранённое при предыдущем создании.
     * @return Корневой View для фрагмента.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_device_details, container, false);
        detailsBinder = new DeviceDetailsBinder(view, deviceViewModel);

        observeView();
        setupSaveButton();
        setupResetTableBtn();
        setupSaveTableButton();

        return view;
    }

    /**
     * Подписка на изменения данных во ViewModel для обновления UI.
     * Обновляет детали устройства, таблицу калибровки и конфигурацию сенсора.
     */
    private void observeView() {
        deviceViewModel.getDeviceDetails().observe(getViewLifecycleOwner(), deviceDetails -> {
            detailsBinder.bindInfo(deviceDetails);
            deviceViewModel.updateVirtualPoint(deviceDetails);
        });

        deviceViewModel.getCalibrationTable().observe(getViewLifecycleOwner(), detailsBinder::bindTable);
        deviceViewModel.getSensorConfigure().observe(getViewLifecycleOwner(), detailsBinder::bindConfigure);
    }

    /**
     * Настраивает кнопку сброса таблицы калибровки для повторного чтения из сенсора.
     */
    private void setupResetTableBtn() {
        detailsBinder.setupReadFromSensorButton(v -> deviceViewModel.rereadCalibrationTable());
    }

    /**
     * Настраивает кнопку сохранения таблицы калибровки.
     * При сохранении выводит сообщение об успехе или ошибке.
     */
    private void setupSaveTableButton() {
        detailsBinder.setupSaveTableButton(v -> {
            int result = deviceViewModel.saveTable();
            if (result > 0) showMessage(getString(R.string.invalid_detector, result));
            else showMessage(getString(R.string.save_configuration));
        });
    }

    /**
     * Настраивает кнопку сохранения конфигурации сенсора.
     * Обновляет данные конфигурации и сохраняет их через ViewModel.
     */
    private void setupSaveButton() {
        detailsBinder.setupSaveButton(v -> {
            SensorConfig config = deviceViewModel.getSensorConfigure().getValue();
            if (config != null) {
                detailsBinder.updateSensorConfig(config);
                deviceViewModel.saveSensorConfiguration();
                showMessage(getString(R.string.save_configuration));
            }
        });
    }

    /**
     * Очистка ресурсов при уничтожении View фрагмента.
     * Сбрасывает биндер, очищает детали и отключается от устройства.
     * Если активити реализует BaseBluetoothActivity, сбрасывает состояние навигатора устройства.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detailsBinder = null;
        deviceViewModel.clearDetails();
        deviceViewModel.disconnect();
        if (getActivity() instanceof BaseBluetoothActivity) {
            ((BaseBluetoothActivity) getActivity()).resetDeviceNavigatorState();
        }
        Log.d("MyTag", "Device details fragment is closed");
    }

    /**
     * Отображает сообщение в Snackbar.
     *
     * @param message Текст сообщения.
     */
    @Override
    public void showMessage(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
