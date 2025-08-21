package com.mehatronics.axle_load.ui.binder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mehatronics.axle_load.R.string.axis_config;
import static com.mehatronics.axle_load.R.string.configuration;
import static com.mehatronics.axle_load.R.string.error_select_at_least_one_sensor_per_axis;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WHRITE;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;
import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.data.service.SaveToFileService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.domain.entities.enums.ConnectStatus;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.AxisAdapter;
import com.mehatronics.axle_load.ui.fragment.AvailableSensorFragment;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class AxisViewBinder implements BaseBinder {
    private final FragmentNavigator navigator;
    private final SaveToFileService service;
    private final EditText editTextAxisCount;
    private final EditText editTextTractorPlate;
    private final EditText editTextTrailerPlate;

    private final Button saveButton;
    private final Button finishButton;
    private final Button buttonConfigureLoaded;
    private final Button truckButton;
    private final Button truckWithTrailer;

    private final AxisAdapter adapter;
    private final MessageCallback callback;
    private final ResourceProvider provider;
    private final Runnable onConfigureLoadedClick;
    private final DeviceInfoToSave deviceInfo;
    private final DeviceTypeRepository repository;

    public AxisViewBinder(
            View root,
            SaveToFileService service,
            BluetoothHandler handler,
            MessageCallback callback,
            ResourceProvider resourceProvider,
            FragmentNavigator navigator,
            Runnable onConfigureLoadedClick,
            DeviceTypeRepository repository,
            boolean isFirstLaunch
    ) {
        this.service = service;
        this.adapter = new AxisAdapter(handler::onClick, handler::onReset);
        this.deviceInfo = new DeviceInfoToSave();
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

        initRecyclerView(root, R.id.recyclerViewAxes, adapter);

        if (isFirstLaunch) {
            hideInitialViews();
        }

        setupClickListeners(handler::onConfigureClick);
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

        truckButton.setOnClickListener(v -> {
            deviceInfo.setType(0);
            editTextTractorPlate.setVisibility(VISIBLE);
            saveButton.setVisibility(VISIBLE);
            buttonConfigureLoaded.setVisibility(VISIBLE);
            editTextAxisCount.setVisibility(VISIBLE);
            editTextTrailerPlate.setVisibility(GONE);
            Log.d("MyTag", String.valueOf(deviceInfo));

        });

        truckWithTrailer.setOnClickListener(v -> {
            deviceInfo.setType(1);
            editTextTractorPlate.setVisibility(VISIBLE);
            saveButton.setVisibility(VISIBLE);
            buttonConfigureLoaded.setVisibility(VISIBLE);
            editTextAxisCount.setVisibility(VISIBLE);
            editTextTrailerPlate.setVisibility(VISIBLE);
            Log.d("MyTag", String.valueOf(deviceInfo));
        });

        buttonConfigureLoaded.setOnClickListener(v -> {
            onConfigureLoadedClick.run();
        });
    }

    public DeviceInfoToSave getDeviceInfo() {
        return deviceInfo;
    }

    private boolean isCanSave() {
        var axisList = adapter.getCurrentList();
        return !axisList.isEmpty() && axisList.stream()
                .allMatch(axis -> axis.getSideDeviceMap()
                        .values()
                        .stream()
                        .anyMatch(Objects::nonNull));
    }

    public void addFinishedMac(Set<String> mac) {
        adapter.setFinishedMacs(mac);
    }

    public void setSavedState(boolean saved) {
        adapter.setSavedState(saved);
    }

    public void submitList(List<AxisModel> list) {
        adapter.submitList(list);
    }

    public void setFinishButtonVisible(boolean visible) {
        this.finishButton.setVisibility(visible ? VISIBLE : GONE);
    }

    public void updateSaveButtonState(List<AxisModel> axes) {
        submitList(axes);
        saveButton.setEnabled(true);
    }
}