package com.mehatronics.axle_load.ui.adapter;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisClickListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisConnectListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisResetListener;

import java.util.List;
import java.util.function.Consumer;

public class AxisViewBinder {
    private final EditText editTextAxisCount;
    private final Button buttonConfigure;
    private final Button saveButton;
    private final AxisAdapter adapter;
    private final LoadingManager loadingManager;

    private AxisViewBinder(builder builder) {
        this.adapter = new AxisAdapter(builder.clickListener, builder.resetListener, builder.connectListener);
        this.editTextAxisCount = builder.root.findViewById(R.id.editTextAxisCount);
        this.buttonConfigure = builder.root.findViewById(R.id.buttonConfigure);
        this.saveButton = builder.root.findViewById(R.id.buttonSave);
        this.loadingManager = new LoadingManager(builder.root);

        initRecyclerView(builder.root, R.id.recyclerViewAxes, adapter);

        onClickConfig(builder.onConfigureClicked);
        onSave();
    }

    public void showLoading(boolean value) {
        loadingManager.showLoading(value);
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

        public builder onClick(OnAxisClickListener listener) {
            this.clickListener = listener;
            return this;
        }

        public builder onReset(OnAxisResetListener listener) {
            this.resetListener = listener;
            return this;
        }

        public builder onConnect(OnAxisConnectListener listener) {
            this.connectListener = listener;
            return this;
        }

        public builder onConfigureClicked(Consumer<String> listener) {
            this.onConfigureClicked = listener;
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
