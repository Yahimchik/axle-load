package com.mehatronics.axle_load.ui.fragment;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.mehatronics.axle_load.R.id.menu_reset_password;
import static com.mehatronics.axle_load.R.id.menu_set_new_password;
import static com.mehatronics.axle_load.R.layout.fragment_device_details;
import static com.mehatronics.axle_load.R.string.disconnect_from;
import static com.mehatronics.axle_load.R.string.error_reading_the_file;
import static com.mehatronics.axle_load.R.string.invalid_detector;
import static com.mehatronics.axle_load.R.string.invalid_password_for;
import static com.mehatronics.axle_load.R.string.password_reset_for;
import static com.mehatronics.axle_load.R.string.password_set_for;
import static com.mehatronics.axle_load.R.string.save_configuration;
import static com.mehatronics.axle_load.ui.fragment.PasswordInputDialogFragment.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.data.service.SaveToFileService;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ui.adapter.LoadingManager;
import com.mehatronics.axle_load.ui.adapter.listener.GattReadListener;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordListener;
import com.mehatronics.axle_load.ui.binder.DeviceDetailsBinder;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.notification.SnackbarManager;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Фрагмент для отображения и управления деталями подключенного Bluetooth-устройства.
 * Отвечает за отображение информации об устройстве, таблицы калибровки и конфигурации сенсора.
 * Использует {@link DeviceViewModel} для получения и обновления данных.
 */
@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment implements MessageCallback, GattReadListener {
    @Inject
    protected ResourceProvider provider;
    @Inject
    protected SnackbarManager snackbarManager;
    @Inject
    protected DeviceDetailsBinder detailsBinder;
    @Inject
    protected PasswordInputDialogFragment dialog;
    @Inject
    protected PasswordRepository passwordRepository;
    @Inject
    protected SaveToFileService service;
    @Inject
    protected DeviceTypeRepository typeRepository;
    private DeviceViewModel vm;
    private LoadingManager loadingManager;
    private View view;
    private boolean wrongPassword = false;
    private boolean isSavingStarted = false;
    private boolean isSaved = false;
    protected ActivityResultLauncher<Intent> pickFileLauncher;

    /**
     * Инициализация ViewModel при создании фрагмента.
     *
     * @param savedInstanceState Состояние, сохранённое при предыдущем создании.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            List<CalibrationTable> loadedList = service.loadListFromUri(requireContext(), uri, CalibrationTable.class);
                            if (loadedList != null && !loadedList.isEmpty()) {
                                vm.setCalibrationTable(loadedList);
                            } else {
                                showMessage(provider.getString(error_reading_the_file));
                            }
                        }
                    }
                }
        );

        vm = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        vm.setListener(this);
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
        view = inflater.inflate(fragment_device_details, container, false);
        detailsBinder.init(view, vm);

        loadingManager = new LoadingManager(view);
        observeView();
        setupResetTableBtn();
        return view;
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onDestroyView() {
        super.onDestroyView();
        if (!wrongPassword) showMessage(getString(disconnect_from, vm.getDeviceName()));
        cleanUpOnClose();
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onWrongPassword() {
        wrongPassword = true;
        showMessage(provider.getString(invalid_password_for, vm.getDeviceName()));
        closeFragment();
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
        vm.getDeviceDetails().observe(getViewLifecycleOwner(), this::observeDetails);

        observeSelectionMode();

        detailsBinder.setupPopupMenu(view, item -> {
            int itemId = item.getItemId();
            if (itemId == menu_reset_password) {
                loadingManager.showLoading(true);
                vm.resetPassword(true);
                passwordRepository.setPasswordStandart(true);
                snackbarManager.showMessage(getViewById(), getString(password_reset_for, vm.getDeviceName()), ()
                        -> loadingManager.showLoading(false));
                return true;
            } else if (itemId == menu_set_new_password) {
                loadingManager.showLoading(true);
                showChangePasswordDialog();
                return true;
            }
            return false;
        });

        vm.setPasswordListener(() -> requireActivity().runOnUiThread(vm::requestPasswordInput));

        vm.getCalibrationTable().observe(getViewLifecycleOwner(), detailsBinder::bindTable);
        vm.getSensorConfigure().observe(getViewLifecycleOwner(), detailsBinder::bindConfigure);

        detailsBinder.readFromFileOnClick(v -> openFilePicker());

        observePasswordDialogEvent();
    }

    private void showChangePasswordDialog() {
        var dialogFragment = new ChangePasswordDialogFragment(provider, passwordRepository);

        getParentFragmentManager().setFragmentResultListener(
                ChangePasswordDialogFragment.REQUEST_KEY,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    if (bundle.getBoolean(ChangePasswordDialogFragment.KEY_CANCELLED, false)) {
                        showMessage(getString(R.string.cancel));
                        loadingManager.showLoading(false);
                    } else {
                        String oldPassword = bundle.getString(ChangePasswordDialogFragment.KEY_OLD_PASSWORD);
                        String newPassword = bundle.getString(ChangePasswordDialogFragment.KEY_NEW_PASSWORD);

                        passwordRepository.setPassword(oldPassword);
                        passwordRepository.setNewPassword(newPassword);
                        vm.setPassword(true);
                        snackbarManager.showMessage(getViewById(), getString(password_set_for, vm.getDeviceName()), ()
                                -> loadingManager.showLoading(false));
                    }
                });

        dialogFragment.show(getParentFragmentManager(), ChangePasswordDialogFragment.TAG);
    }

    private void observeDetails(DeviceDetails deviceDetails) {
        detailsBinder.bindInfo(deviceDetails);
        vm.updateVirtualPoint(deviceDetails);
    }

    private void observeSelectionMode() {
        vm.getConfigurationSavedLiveData().observe(getViewLifecycleOwner(), config -> {
            if (typeRepository.getCurrDeviceType().equals(DeviceType.BT_COM_MINI)) {
                vm.saveToBTCOMMini();
            }
        });

        vm.getSaveToMiniLive().observe(getViewLifecycleOwner(), saved -> {
            if (saved) {
                snackbarManager.showMessage(requireActivity(), getString(save_configuration), this::setIsSaved);
                if (isSaved) {
                    closeFragment();
                }
            }
        });

        detailsBinder.finishButtonOnClick(v -> {
            loadingManager.showLoading(true);
            int res = vm.saveTable();

            if (res > 0) {
                showMessage(getString(invalid_detector, res));
                loadingManager.showLoading(false);
                return;
            }

            SensorConfig config = vm.getSensorConfigure().getValue();
            if (config != null) {
                detailsBinder.updateSensorConfig(config);
                vm.saveSensorConfiguration();
                isSavingStarted = true;
            }
        });

        vm.getConfigurationSavedLiveData().observe(getViewLifecycleOwner(), isSaved -> {
            if (Boolean.TRUE.equals(isSaved) && isSavingStarted) {
                isSavingStarted = false;
                vm.setConfigurationSavedLive(false);

                snackbarManager.showMessage(getViewById(), getString(save_configuration), () -> {
                    loadingManager.showLoading(false);
                    if (Boolean.TRUE.equals(vm.getSelectionModeLiveData().getValue())) {
                        String lastMac = vm.getLastFinishedMac().getValue();
                        if (lastMac != null) {
                            vm.addFinishedMac(lastMac);
                        }
                    }
                });
            }
        });
    }

    private void setIsSaved() {
        isSaved = true;
    }

    private void observePasswordDialogEvent() {
        vm.getShowPasswordDialogEvent().observe(getViewLifecycleOwner(), unused -> {
            if (vm.isPasswordSet() && !isDialogVisible()) showPasswordInputDialog();
        });
    }

    private void showPasswordInputDialog() {
        if (isDialogVisible()) return;
        vm.setPasswordDialogVisible(true);

        dialog.setPasswordListener(new PasswordListener() {
            @Override
            public void onPasswordSubmitted(String password) {
                vm.submitPassword(password);
                vm.setPasswordDialogVisible(false);
            }

            @Override
            public void onPasswordCancelled() {
                vm.clearPassword();
                vm.setPasswordDialogVisible(false);

                vm.clearPasswordDialogShown();
                vm.requestPasswordInput();

                closeFragment();
            }
        });
        dialog.show(getParentFragmentManager(), TAG);
    }

    private boolean isDialogVisible() {
        return Boolean.TRUE.equals(vm.getIsPasswordDialogVisible().getValue());
    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack(
                DeviceDetailsFragment.class.getSimpleName(),
                POP_BACK_STACK_INCLUSIVE
        );

        if (requireActivity() instanceof BaseBluetoothActivity) {
            ((BaseBluetoothActivity) requireActivity()).loadingManagerShowLoading(false);
        }
    }

    /**
     * Настраивает кнопку сброса таблицы калибровки для повторного чтения из сенсора.
     */
    private void setupResetTableBtn() {
        detailsBinder.setupReadFromSensorButton(v -> vm.rereadCalibrationTable());
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

        vm.clearDetails();
        vm.disconnect();

        vm.clearPassword();
        vm.setPasswordDialogVisible(false);

        vm.clearPasswordDialogShown();
        vm.requestPasswordInput();

        vm.markAsSaved();
        vm.setSelectionMode(false);

        closeFragment();
        Log.d("MyTag", "Device details fragment is closed");
    }

    public void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickFileLauncher.launch(intent);
    }
}