package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.R.string.error_select_at_least_one_sensor_per_axis;
import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.AxisAdapter;
import com.mehatronics.axle_load.ui.fragment.AxleOverviewFragment;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class AxisViewBinder implements BaseBinder {
    private final FragmentNavigator navigator;
    private final EditText editTextAxisCount;

    private final Button buttonConfigure;
    private final Button saveButton;
    private final Button finishButton;

    private final AxisAdapter adapter;
    private final MessageCallback callback;
    private final ResourceProvider provider;

    public AxisViewBinder(
            View root,
            BluetoothHandler handler,
            MessageCallback callback,
            ResourceProvider resourceProvider,
            FragmentNavigator navigator
    ) {

        this.adapter = new AxisAdapter(handler::onClick, handler::onReset);
        this.editTextAxisCount = root.findViewById(R.id.editTextAxisCount);
        this.buttonConfigure = root.findViewById(R.id.buttonConfigure);
        this.saveButton = root.findViewById(R.id.buttonSave);
        this.finishButton = root.findViewById(R.id.finishButton);
        this.callback = callback;
        this.provider = resourceProvider;
        this.navigator = navigator;

        initRecyclerView(root, R.id.recyclerViewAxes, adapter);

        setupClickListeners(handler::onConfigureClick);
    }

    private void setupClickListeners(Consumer<String> onConfigureClicked) {
        buttonConfigure.setOnClickListener(v -> {
            var input = editTextAxisCount.getText().toString().trim();
            onConfigureClicked.accept(input);
        });

        saveButton.setOnClickListener(v -> {
            if (isCanSave()) {
                adapter.setSavedState(true);
            } else {
                callback.showMessage(provider.getString(error_select_at_least_one_sensor_per_axis));
            }
        });

        finishButton.setOnClickListener(v -> navigator.showFragment(new AxleOverviewFragment()));
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
        finishButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void updateSaveButtonState(List<AxisModel> axes) {
        submitList(axes);
        saveButton.setEnabled(true);
    }
}