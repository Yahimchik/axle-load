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
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisClickListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisResetListener;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AxisViewBinder implements BaseBinder {
    private final EditText editTextAxisCount;
    private final Button buttonConfigure;
    private final Button saveButton;
    private final Button finishButton;
    private final AxisAdapter adapter;
    private final MessageCallback messageCallback;
    private final ResourceProvider provider;
    private final Runnable onFinishClicked;

    private AxisViewBinder(builder builder) {
        this.adapter = new AxisAdapter(builder.clickListener, builder.resetListener);
        this.editTextAxisCount = builder.root.findViewById(R.id.editTextAxisCount);
        this.buttonConfigure = builder.root.findViewById(R.id.buttonConfigure);
        this.saveButton = builder.root.findViewById(R.id.buttonSave);
        this.finishButton = builder.root.findViewById(R.id.finishButton);
        this.messageCallback = builder.messageCallback;
        this.provider = builder.resourceProvider;
        this.onFinishClicked = builder.onFinishClicked;
        initRecyclerView(builder.root, R.id.recyclerViewAxes, adapter);

        onClickConfig(builder.onConfigureClicked);
        onSave();
        onFinish();
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

    private void onClickConfig(Consumer<String> onConfigureClicked) {
        buttonConfigure.setOnClickListener(v -> {
            var input = editTextAxisCount.getText().toString().trim();
            onConfigureClicked.accept(input);
        });
    }

    private void onSave() {
        saveButton.setOnClickListener(v -> {
            if (isCanSave()) {
                adapter.setSavedState(true);
            } else {
                messageCallback.showMessage(provider.getString(error_select_at_least_one_sensor_per_axis));
            }
        });
    }

    private boolean isCanSave() {
        var axisList = adapter.getCurrentList();
        return !axisList.isEmpty() && axisList.stream()
                .allMatch(axis ->
                        axis.getSideDeviceMap()
                                .values()
                                .stream()
                                .anyMatch(mac -> mac != null)
                );
    }

    private void onFinish() {
        finishButton.setOnClickListener(v -> {
            if (onFinishClicked != null) {
                onFinishClicked.run();
            }
        });
    }

    public static class builder {
        private View root;
        private OnAxisClickListener clickListener;
        private OnAxisResetListener resetListener;
        private Consumer<String> onConfigureClicked;
        private MessageCallback messageCallback;
        private ResourceProvider resourceProvider;
        private Runnable onFinishClicked;

        public builder onFinishClick(Runnable listener) {
            this.onFinishClicked = listener;
            return this;
        }

        public builder withMessageCallback(MessageCallback callback) {
            this.messageCallback = callback;
            return this;
        }

        public builder withResourceProvider(ResourceProvider provider) {
            this.resourceProvider = provider;
            return this;
        }

        public builder withRoot(View root) {
            this.root = root;
            return this;
        }

        public builder onAction(BluetoothHandler handler) {
            this.clickListener = handler::onClick;
            this.resetListener = handler::onReset;
            this.onConfigureClicked = handler::onConfigureClick;
            return this;
        }

        public AxisViewBinder build() {
            if (root == null || clickListener == null || resetListener == null || /*connectListener == null ||*/ onConfigureClicked == null) {
                throw new IllegalStateException("AxisViewManager: all fields must be set before building");
            }
            return new AxisViewBinder(this);
        }
    }
}
