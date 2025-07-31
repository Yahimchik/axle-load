package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.ui.fragment.PasswordInputDialogFragment.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordListener;
import com.mehatronics.axle_load.ui.binder.DeviceDetailsBinder;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Фрагмент для отображения и управления деталями подключенного Bluetooth-устройства.
 * Отвечает за отображение информации об устройстве, таблицы калибровки и конфигурации сенсора.
 * Использует {@link DeviceViewModel} для получения и обновления данных.
 */
@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment /*implements MessageCallback*/ {
    @Inject
    protected DeviceDetailsBinder detailsBinder;
    @Inject
    protected PasswordInputDialogFragment dialog;
    private DeviceViewModel viewModel;
    private View view;

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
        view = inflater.inflate(R.layout.fragment_device_details, container, false);
        detailsBinder.init(view, viewModel);
        detailsBinder.setupPopupMenu(view, viewModel::resetPassword, viewModel::setNewPassword);

        observeView();
        setupSaveButton();
        setupResetTableBtn();
        setupSaveTableButton();

        return view;
    }

    private boolean isPasswordDialogVisible = false;

    /**
     * Подписка на изменения данных во ViewModel для обновления UI.
     * Обновляет детали устройства, таблицу калибровки и конфигурацию сенсора.
     */
    private void observeView() {
        viewModel.getDeviceDetails().observe(getViewLifecycleOwner(), deviceDetails -> {
            detailsBinder.bindInfo(deviceDetails);
            viewModel.updateVirtualPoint(deviceDetails);
        });

        observeSelectionMode();

        viewModel.setPasswordListener(() -> requireActivity().runOnUiThread(viewModel::requestPasswordInput));

        viewModel.getCalibrationTable().observe(getViewLifecycleOwner(), detailsBinder::bindTable);
        viewModel.getSensorConfigure().observe(getViewLifecycleOwner(), detailsBinder::bindConfigure);

        observePasswordDialogEvent();
    }

    private void observeSelectionMode() {
        viewModel.getSelectionModeLiveData().observe(getViewLifecycleOwner(), detailsBinder::setVisibility);

        detailsBinder.finishButtonOnClick(v -> {
            String lastMac = viewModel.getLastFinishedMac().getValue();
            if (lastMac != null) {
                viewModel.addFinishedMac(lastMac);
            }

            closeFragment();
        });
    }

    private void observePasswordDialogEvent() {
        viewModel.getShowPasswordDialogEvent().observe(getViewLifecycleOwner(), unused -> {
            if (viewModel.isPasswordSet()
                    && !isPasswordDialogVisible
                    && isAdded()
                    && getView() != null
                    && !isRemoving()
                    && !requireActivity().isFinishing()) {
                showPasswordInputDialog();
            } else {
                Log.d("MyTag", "Диалог не открыт — пароль уже установлен или уже показывается, или фрагмент неактивен");
            }
        });
    }

    private void showPasswordInputDialog() {
        if (isPasswordDialogVisible) return;
        isPasswordDialogVisible = true;

        dialog.setPasswordListener(new PasswordListener() {
            @Override
            public void onPasswordSubmitted(String password) {
                viewModel.submitPassword(password);
                isPasswordDialogVisible = false;
            }

            @Override
            public void onPasswordCancelled() {
                viewModel.clearPassword();
                isPasswordDialogVisible = false;

                viewModel.clearPasswordDialogShown();
                viewModel.requestPasswordInput();

                closeFragment();
            }
        });
        dialog.show(getParentFragmentManager(), TAG);
    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack(
                DeviceDetailsFragment.class.getSimpleName(),
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        );
    }

    /**
     * Настраивает кнопку сброса таблицы калибровки для повторного чтения из сенсора.
     */
    private void setupResetTableBtn() {
        detailsBinder.setupReadFromSensorButton(v -> viewModel.rereadCalibrationTable());
    }

    /**
     * Настраивает кнопку сохранения таблицы калибровки.
     * При сохранении выводит сообщение об успехе или ошибке.
     */
    private void setupSaveTableButton() {
        detailsBinder.setupSaveTableButton(v -> {
            int result = viewModel.saveTable();
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
            SensorConfig config = viewModel.getSensorConfigure().getValue();
            if (config != null) {
                detailsBinder.updateSensorConfig(config);
                viewModel.saveSensorConfiguration();
                showMessage(getString(R.string.save_configuration));
            }
        });
    }

    /**
     * Очистка ресурсов при уничтожении View фрагмента.
     * Сбрасывает биндер, очищает детали и отключается от устройства.
     * Если активити реализует BaseBluetoothActivity, сбрасывает состояние навигатора устройства.
     */
    private void cleanUpOnClose() {
        detailsBinder = null;

        viewModel.clearDetails();
        viewModel.disconnect();

        viewModel.clearPassword();
        isPasswordDialogVisible = false;

        showMessage(getString(R.string.disconnect_from, viewModel.getDeviceName()));

        viewModel.clearPasswordDialogShown();
        viewModel.requestPasswordInput();

        viewModel.markAsSaved();
        viewModel.setSelectionMode(false);

        closeFragment();
        Log.d("MyTag", "Device details fragment is closed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanUpOnClose();
    }

    /**
     * Отображает сообщение в Snackbar.
     *
     * @param message Текст сообщения.
     */
    public void showMessage(String message) {
        View root = requireActivity().findViewById(android.R.id.content);
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }
}
