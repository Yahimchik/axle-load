package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.LifecycleOwner;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.ui.adapter.AxisAdapter;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisClickListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisConnectListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisResetListener;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AxisViewBinder implements BaseBinder {
    private final EditText editTextAxisCount;
    private final Button buttonConfigure;
    private final Button saveButton;
    private final AxisAdapter adapter;

    public AxisViewBinder(View root, BluetoothHandler handler) {
        this.editTextAxisCount = root.findViewById(R.id.editTextAxisCount);
        this.buttonConfigure = root.findViewById(R.id.buttonConfigure);
        this.saveButton = root.findViewById(R.id.buttonSave);
        this.adapter = new AxisAdapter(handler::onClick, handler::onReset, handler::onConnect);

        initRecyclerView(root, R.id.recyclerViewAxes, adapter);

        onClickConfig(handler::onConfigureClick);
        onSave();
    }

    private AxisViewBinder(builder builder) {
        this.adapter = new AxisAdapter(builder.clickListener, builder.resetListener, builder.connectListener);
        this.editTextAxisCount = builder.root.findViewById(R.id.editTextAxisCount);
        this.buttonConfigure = builder.root.findViewById(R.id.buttonConfigure);
        this.saveButton = builder.root.findViewById(R.id.buttonSave);

        initRecyclerView(builder.root, R.id.recyclerViewAxes, adapter);

        onClickConfig(builder.onConfigureClicked);
        onSave();
    }

    public void addFinishedMac(Set<String> mac){
        adapter.setFinishedMacs(mac);
    }

    public void setSavedState(boolean saved) {
        adapter.setSavedState(saved);
    }

    public void submitList(List<AxisModel> list) {
        adapter.submitList(list);
    }

    private void onClickConfig(Consumer<String> onConfigureClicked) {
        buttonConfigure.setOnClickListener(v -> {
            var input = editTextAxisCount.getText().toString().trim();
            onConfigureClicked.accept(input);
        });
    }

    private void onSave() {
        saveButton.setOnClickListener(v -> adapter.setSavedState(true));
    }

    public static class builder {
        private View root;
        private OnAxisClickListener clickListener;
        private OnAxisResetListener resetListener;
        private OnAxisConnectListener connectListener;
        private Consumer<String> onConfigureClicked;

        public builder withRoot(View root) {
            this.root = root;
            return this;
        }

        public builder onAction(BluetoothHandler handler) {
            this.clickListener = handler::onClick;
            this.connectListener = handler::onConnect;
            this.resetListener = handler::onReset;
            this.onConfigureClicked = handler::onConfigureClick;
            return this;
        }

        public AxisViewBinder build() {
            if (root == null || clickListener == null || resetListener == null || connectListener == null || onConfigureClicked == null) {
                throw new IllegalStateException("AxisViewManager: all fields must be set before building");
            }
            return new AxisViewBinder(this);
        }
    }
}
