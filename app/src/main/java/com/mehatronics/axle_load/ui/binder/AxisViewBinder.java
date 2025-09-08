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
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.card.MaterialCardView;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.data.service.FileService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.AxisAdapter;
import com.mehatronics.axle_load.ui.adapter.sensor.SensorConfigAdapter;
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
    private final MaterialCardView tractorPlateCard;
    private final MaterialCardView trailerPlateCard;
    private final MaterialCardView axisCountCard;

    private final ImageButton axisCountMinus;
    private final ImageButton axisCountPlus;
    private final View divider;
    private final View divider2;

    private final AxisAdapter adapter;
    private final MessageCallback callback;
    private final ResourceProvider provider;
    private final Runnable onConfigureLoadedClick;
    private final DeviceInfoToSave deviceInfo;
    private final DeviceTypeRepository repository;
    private final Consumer<DeviceInfoToSave> onDeviceInfoChanged;
    private final BluetoothHandler handler;

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
        this.editTextAxisCount = root.findViewById(R.id.axisCountEditText);
        this.saveButton = root.findViewById(R.id.buttonSave);
        this.finishButton = root.findViewById(R.id.finishButtonConfigure);
        this.buttonConfigureLoaded = root.findViewById(R.id.buttonConfigureLoaded);
        this.truckButton = root.findViewById(R.id.buttonTractor);
        this.truckWithTrailer = root.findViewById(R.id.buttonTractorWithTrailer);
        this.editTextTractorPlate = root.findViewById(R.id.editTextTractorPlate);
        this.editTextTrailerPlate = root.findViewById(R.id.editTextTrailerPlate);
        this.handler = handler;

        this.tractorPlateCard = root.findViewById(R.id.tractorPlateCard);
        this.trailerPlateCard = root.findViewById(R.id.trailerPlateCard);
        this.axisCountCard = root.findViewById(R.id.axisCountCard);

        this.axisCountMinus = root.findViewById(R.id.axisCountMinus);
        this.axisCountPlus = root.findViewById(R.id.axisCountPlus);

        this.divider = root.findViewById(R.id.divider1);
        this.divider2 = root.findViewById(R.id.divider2);

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
        setupIncrementDecrement();
    }

    private void restoreVehicleTypeState() {
        if (deviceInfo.getType() == 0) {
            tractorPlateCard.setVisibility(VISIBLE);
            trailerPlateCard.setVisibility(GONE);
            saveButton.setVisibility(VISIBLE);
            buttonConfigureLoaded.setVisibility(VISIBLE);
            axisCountCard.setVisibility(VISIBLE);
            divider.setVisibility(VISIBLE);
            divider2.setVisibility(VISIBLE);
        } else if (deviceInfo.getType() == 1) {
            tractorPlateCard.setVisibility(VISIBLE);
            trailerPlateCard.setVisibility(VISIBLE);
            saveButton.setVisibility(VISIBLE);
            buttonConfigureLoaded.setVisibility(VISIBLE);
            axisCountCard.setVisibility(VISIBLE);
            divider.setVisibility(VISIBLE);
            divider2.setVisibility(VISIBLE);
        } else {
            hideInitialViews();
        }
    }

    private void hideInitialViews() {
        tractorPlateCard.setVisibility(GONE);
        trailerPlateCard.setVisibility(GONE);
        finishButton.setVisibility(GONE);
        saveButton.setVisibility(GONE);
        buttonConfigureLoaded.setVisibility(GONE);
        axisCountCard.setVisibility(GONE);
        divider.setVisibility(GONE);
        divider2.setVisibility(GONE);
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
        tractorPlateCard.setVisibility(VISIBLE);
        saveButton.setVisibility(VISIBLE);
        buttonConfigureLoaded.setVisibility(VISIBLE);
        axisCountCard.setVisibility(VISIBLE);
        trailerPlateCard.setVisibility(gone);
        onDeviceInfoChanged.accept(deviceInfo);
        divider.setVisibility(VISIBLE);
        divider2.setVisibility(VISIBLE);
        if (gone == GONE) {
            editTextTrailerPlate.setText("");
            deviceInfo.setCarNumberSecond("");
        }
    }

    private boolean isCanSave() {
        var axisList = adapter.getCurrentList();
        return !axisList.isEmpty() && axisList.stream()
                .allMatch(axis -> axis.getSideDeviceMap()
                        .values()
                        .stream()
                        .anyMatch(Objects::nonNull));
    }

    private void setupIncrementDecrement() {
        setupButton(axisCountMinus, axisCountPlus, editTextAxisCount, 1, 7);
    }

    private void setupButton(ImageButton minus, ImageButton plus, EditText editText, int min, int max) {
        minus.setOnClickListener(v -> {
            int val = Integer.parseInt(editText.getText().toString());
            if (val > min) {
                handler.onReset(val, () -> {
                    int newVal = val - 1;
                    editText.setText(String.valueOf(newVal));
                });
            }
        });

        plus.setOnClickListener(v -> {
            int val = Integer.parseInt(editText.getText().toString());
            if (val < max) {
                editText.setText(String.valueOf(val + 1));
            }
        });

        editText.setFilters(new InputFilter[]{new SensorConfigAdapter.InputFilterMinMax(min, max)});
    }
}