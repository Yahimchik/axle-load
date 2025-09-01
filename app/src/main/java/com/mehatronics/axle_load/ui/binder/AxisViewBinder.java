package com.mehatronics.axle_load.ui.binder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mehatronics.axle_load.R.string.axis_config;
import static com.mehatronics.axle_load.R.string.configuration;
import static com.mehatronics.axle_load.R.string.error_select_at_least_one_sensor_per_axis;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WHRITE;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;
import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.card.MaterialCardView;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.data.service.FileService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.AxisAdapter;
import com.mehatronics.axle_load.ui.fragment.AvailableSensorFragment;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class AxisViewBinder {
    private final FragmentNavigator navigator;
    private final FileService service;
    private final EditText editTextAxisCount;
    private final EditText editTextTractorPlate;
    private final EditText editTextTrailerPlate;

    private final MaterialCardView saveButton;
    private final MaterialCardView finishButton;
    private final MaterialCardView buttonConfigureLoaded;
    private final MaterialCardView truckButton;
    private final MaterialCardView truckWithTrailer;

    private final AxisAdapter adapter;
    private final MessageCallback callback;
    private final ResourceProvider provider;
    private final Runnable onConfigureLoadedClick;
    private final DeviceInfoToSave deviceInfo;
    private final DeviceTypeRepository repository;
    private final Consumer<DeviceInfoToSave> onDeviceInfoChanged;

    @SuppressLint("ClickableViewAccessibility")
    public AxisViewBinder(
            View root,
            FileService service,
            BluetoothHandler handler,
            MessageCallback callback,
            ResourceProvider resourceProvider,
            FragmentNavigator navigator,
            Runnable onConfigureLoadedClick,
            DeviceTypeRepository repository,
            boolean isFirstLaunch,
            Consumer<DeviceInfoToSave> onDeviceInfoChanged,
            DeviceInfoToSave deviceInfo
    ) {
        this.service = service;
        this.adapter = new AxisAdapter(handler::onClick, handler::onReset);
        this.deviceInfo = deviceInfo;
        this.editTextAxisCount = root.findViewById(R.id.editTextAxisCount);
        this.saveButton = root.findViewById(R.id.buttonSave);
        this.finishButton = root.findViewById(R.id.finishButtonConfigure);
        this.buttonConfigureLoaded = root.findViewById(R.id.buttonConfigureLoaded);
        this.truckButton = root.findViewById(R.id.buttonTractor);
        this.truckWithTrailer = root.findViewById(R.id.buttonTractorWithTrailer);
        this.editTextTractorPlate = root.findViewById(R.id.editTextTractorPlate);
        this.editTextTrailerPlate = root.findViewById(R.id.editTextTrailerPlate);
        this.callback = callback;
        this.provider = resourceProvider;
        this.navigator = navigator;
        this.onConfigureLoadedClick = onConfigureLoadedClick;
        this.repository = repository;
        this.onDeviceInfoChanged = onDeviceInfoChanged;

        saveButton.setOnTouchListener(MainActivityBinder::addMotion);
        finishButton.setOnTouchListener(MainActivityBinder::addMotion);
        buttonConfigureLoaded.setOnTouchListener(MainActivityBinder::addMotion);
        truckButton.setOnTouchListener(MainActivityBinder::addMotion);
        truckWithTrailer.setOnTouchListener(MainActivityBinder::addMotion);

        initRecyclerView(root, R.id.recyclerViewAxes, adapter);

        if (isFirstLaunch) {
            this.deviceInfo.setType(-1);
            hideInitialViews();
        }
        restoreVehicleTypeState();
        setupClickListeners(handler::onConfigureClick);
    }

    private void restoreVehicleTypeState() {
        if (deviceInfo.getType() == 0) {
            editTextTractorPlate.setVisibility(VISIBLE);
            editTextTrailerPlate.setVisibility(GONE);
            saveButton.setVisibility(VISIBLE);
            buttonConfigureLoaded.setVisibility(VISIBLE);
            editTextAxisCount.setVisibility(VISIBLE);
        } else if (deviceInfo.getType() == 1) {
            editTextTractorPlate.setVisibility(VISIBLE);
            editTextTrailerPlate.setVisibility(VISIBLE);
            saveButton.setVisibility(VISIBLE);
            buttonConfigureLoaded.setVisibility(VISIBLE);
            editTextAxisCount.setVisibility(VISIBLE);
        } else {
            hideInitialViews();
        }
    }

    private void hideInitialViews() {
        editTextTractorPlate.setVisibility(GONE);
        editTextTrailerPlate.setVisibility(GONE);
        finishButton.setVisibility(GONE);
        saveButton.setVisibility(GONE);
        buttonConfigureLoaded.setVisibility(GONE);
        editTextAxisCount.setVisibility(GONE);
    }

    private void setupClickListeners(Consumer<String> onConfigureClicked) {

        editTextAxisCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (!input.isEmpty()) {
                    try {
                        int count = Integer.parseInt(input);
                        if (count > 0) {
                            onConfigureClicked.accept(input);
                        } else {
                            submitList(List.of());
                        }
                    } catch (NumberFormatException e) {
                        submitList(List.of());
                    }
                } else {
                    submitList(List.of());
                }
            }
        });

        editTextTractorPlate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                deviceInfo.setCarNumberFirst(editable.toString().trim());
                onDeviceInfoChanged.accept(deviceInfo);
            }
        });

        editTextTrailerPlate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                deviceInfo.setCarNumberSecond(editable.toString().trim());
                onDeviceInfoChanged.accept(deviceInfo);
            }
        });

        saveButton.setOnClickListener(v -> {
            if (isCanSave()) {
                adapter.setSavedState(true);
            } else {
                callback.showMessage(provider.getString(error_select_at_least_one_sensor_per_axis));
            }
        });

        finishButton.setOnClickListener(v -> {
            service.saveToFile(v,
                    v.getContext(),
                    provider.getString(configuration),
                    provider.getString(axis_config),
                    adapter.getCurrentList());
            navigator.showFragment(new AvailableSensorFragment());
            repository.setDeviceType(BT_COM_MINI);
            repository.setStatus(WHRITE);
        });

        truckButton.setOnClickListener(v -> setConfigType(0, GONE));
        truckWithTrailer.setOnClickListener(v -> setConfigType(1, VISIBLE));

        buttonConfigureLoaded.setOnClickListener(v -> onConfigureLoadedClick.run());
    }

    public void setAxisCount(int count) {
        editTextAxisCount.setText(String.valueOf(count));
    }

    public void addFinishedMac(Set<String> mac) {
        adapter.setFinishedMacs(mac);
    }

    public void setSavedState(boolean saved) {
        adapter.setSavedState(saved);
    }

    public void submitList(List<AxisModel> list) {
        adapter.submitList(new ArrayList<>(list));
    }

    public void setFinishButtonVisible(boolean visible) {
        this.finishButton.setVisibility(visible ? VISIBLE : GONE);
    }

    public void updateSaveButtonState(List<AxisModel> axes) {
        submitList(axes);
        saveButton.setEnabled(true);
    }

    private void setConfigType(int type, int gone) {
        deviceInfo.setType(type);
        editTextTractorPlate.setVisibility(VISIBLE);
        saveButton.setVisibility(VISIBLE);
        buttonConfigureLoaded.setVisibility(VISIBLE);
        editTextAxisCount.setVisibility(VISIBLE);
        editTextTrailerPlate.setVisibility(gone);
        onDeviceInfoChanged.accept(deviceInfo);
    }

    private boolean isCanSave() {
        var axisList = adapter.getCurrentList();
        return !axisList.isEmpty() && axisList.stream()
                .allMatch(axis -> axis.getSideDeviceMap()
                        .values()
                        .stream()
                        .anyMatch(Objects::nonNull));
    }
}