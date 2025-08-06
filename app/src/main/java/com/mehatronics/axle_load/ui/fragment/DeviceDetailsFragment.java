package com.mehatronics.axle_load.ui.fragment;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.mehatronics.axle_load.R.layout.fragment_device_details;
import static com.mehatronics.axle_load.R.string.disconnect_from;
import static com.mehatronics.axle_load.R.string.invalid_detector;
import static com.mehatronics.axle_load.R.string.save_configuration;
import static com.mehatronics.axle_load.ui.fragment.PasswordInputDialogFragment.TAG;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.ui.adapter.LoadingManager;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordListener;
import com.mehatronics.axle_load.ui.binder.DeviceDetailsBinder;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.notification.SnackbarManager;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Фрагмент для отображения и управления деталями подключенного Bluetooth-устройства.
 * Отвечает за отображение информации об устройстве, таблицы калибровки и конфигурации сенсора.
 * Использует {@link DeviceViewModel} для получения и обновления данных.
 */
@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment implements MessageCallback {
    @Inject
    protected SnackbarManager snackbarManager;
    @Inject
    protected DeviceDetailsBinder detailsBinder;
    @Inject
    protected PasswordInputDialogFragment dialog;
    private DeviceViewModel viewModel;
    private LoadingManager loadingManager;

    /**
     * Инициализация ViewModel при создании фрагмента.
     *
     * @param savedInstanceState Состояние, сохранённое при предыдущем создании.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    /**
     * Создаёт и возвращает иерархию представлений фрагмента.
     *
     * @param inflater           Инфлейтер для создания View из XML.
     * @param container          Родительский ViewGroup.
     * @param savedInstanceState Состояние, сохранённое при предыдущем создании.
     * @return Корневой View для фрагмента.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        var view = inflater.inflate(fragment_device_details, container, false);
        detailsBinder.init(view, viewModel);
        detailsBinder.setupPopupMenu(view, viewModel::resetPassword, viewModel::setNewPassword);
        loadingManager = new LoadingManager(view);

        observeView();
        setupResetTableBtn();

        return view;
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onDestroyView() {
        super.onDestroyView();
        cleanUpOnClose();
    }

    @Override
    public void showMessage(String message) {
        snackbarManager.showMessage(getViewById(), message);
    }

    /**
     * Подписка на изменения данных во ViewModel для обновления UI.
     * Обновляет детали устройства, таблицу калибровки и конфигурацию сенсора.
     */
    private void observeView() {
        viewModel.getDeviceDetails().observe(getViewLifecycleOwner(), this::observeDetails);

        observeSelectionMode();

        viewModel.setPasswordListener(() -> requireActivity().runOnUiThread(viewModel::requestPasswordInput));

        viewModel.getCalibrationTable().observe(getViewLifecycleOwner(), detailsBinder::bindTable);
        viewModel.getSensorConfigure().observe(getViewLifecycleOwner(), detailsBinder::bindConfigure);

        observePasswordDialogEvent();
    }

    private void observeDetails(DeviceDetails deviceDetails) {
        detailsBinder.bindInfo(deviceDetails);
        viewModel.updateVirtualPoint(deviceDetails);
    }

    private boolean isSavingStarted = false;

    private void observeSelectionMode() {
        detailsBinder.finishButtonOnClick(v -> {
            loadingManager.showLoading(true);
            int res = viewModel.saveTable();

            if (res > 0) {
                showMessage(getString(invalid_detector, res));
                loadingManager.showLoading(false);
                return;
            }

            SensorConfig config = viewModel.getSensorConfigure().getValue();
            if (config != null) {
                detailsBinder.updateSensorConfig(config);
                viewModel.saveSensorConfiguration();
                isSavingStarted = true;
            }
        });

        viewModel.getConfigurationSavedLiveData().observe(getViewLifecycleOwner(), isSaved -> {
            if (Boolean.TRUE.equals(isSaved) && isSavingStarted) {
                isSavingStarted = false;
                viewModel.setConfigurationSavedLive(false);

                snackbarManager.showMessage(getViewById(), getString(save_configuration), () -> {
                    loadingManager.showLoading(false);

                    // Проверяем состояние selectionMode
                    if (Boolean.TRUE.equals(viewModel.getSelectionModeLiveData().getValue())) {
                        // Если режим выбора — закрываем фрагмент
                        String lastMac = viewModel.getLastFinishedMac().getValue();
                        if (lastMac != null) {
                            viewModel.addFinishedMac(lastMac);
                        }
                        closeFragment();
                    } else {
                        // В другом режиме — просто показываем сообщение, ничего не закрываем
                        Log.d("MyTag", "Snackbar shown, fragment remains open.");
                    }
                });
            }
        });


    }

    private void observePasswordDialogEvent() {
        viewModel.getShowPasswordDialogEvent().observe(getViewLifecycleOwner(), unused -> {
            if (viewModel.isPasswordSet() && !isDialogVisible()) showPasswordInputDialog();
            else Log.d("MyTag", "Диалог не открыт");
        });
    }

    private void showPasswordInputDialog() {
        if (isDialogVisible()) return;
        viewModel.setPasswordDialogVisible(true);

        dialog.setPasswordListener(new PasswordListener() {
            @Override
            public void onPasswordSubmitted(String password) {
                viewModel.submitPassword(password);
                viewModel.setPasswordDialogVisible(false);
            }

            @Override
            public void onPasswordCancelled() {
                viewModel.clearPassword();
                viewModel.setPasswordDialogVisible(false);

                viewModel.clearPasswordDialogShown();
                viewModel.requestPasswordInput();

                closeFragment();
            }
        });
        dialog.show(getParentFragmentManager(), TAG);
    }

    private boolean isDialogVisible() {
        return Boolean.TRUE.equals(viewModel.getIsPasswordDialogVisible().getValue());
    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack(
                DeviceDetailsFragment.class.getSimpleName(),
                POP_BACK_STACK_INCLUSIVE
        );
    }

    /**
     * Настраивает кнопку сброса таблицы калибровки для повторного чтения из сенсора.
     */
    private void setupResetTableBtn() {
        detailsBinder.setupReadFromSensorButton(v -> viewModel.rereadCalibrationTable());
    }

    private View getViewById() {
        return requireActivity().findViewById(android.R.id.content);
    }

    /**
     * Очистка ресурсов при уничтожении View фрагмента.
     * Сбрасывает биндер, очищает детали и отключается от устройства.
     * Если активити реализует BaseBluetoothActivity, сбрасывает состояние навигатора устройства.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void cleanUpOnClose() {
        detailsBinder = null;

        viewModel.clearDetails();
        viewModel.disconnect();

        viewModel.clearPassword();
        viewModel.setPasswordDialogVisible(false);

        showMessage(getString(disconnect_from, viewModel.getDeviceName()));

        viewModel.clearPasswordDialogShown();
        viewModel.requestPasswordInput();

        viewModel.markAsSaved();
        viewModel.setSelectionMode(false);

        closeFragment();
        Log.d("MyTag", "Device details fragment is closed");
    }
}