package com.mehatronics.axle_load.domain.handler;

import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

public class BaseBluetoothHandler {
    protected final DeviceViewModel deviceViewModel;
    protected final BluetoothHandlerContract contract;
    protected final ResourceProvider resourceProvider;

    protected BaseBluetoothHandler(BaseBuilder<?> builder) {
        this.deviceViewModel = builder.deviceViewModel;
        this.contract = builder.contract;
        this.resourceProvider = builder.resourceProvider;
    }

    @SuppressWarnings("unchecked")
    public static abstract class BaseBuilder<T extends BaseBuilder<T>> {
        protected DeviceViewModel deviceViewModel;
        protected BluetoothHandlerContract contract;
        protected ResourceProvider resourceProvider;

        public T deviceViewModel(DeviceViewModel deviceViewModel) {
            this.deviceViewModel = deviceViewModel;
            return (T) this;
        }

        public T contract(BluetoothHandlerContract contract) {
            this.contract = contract;
            return (T) this;
        }

        public T resourceProvider(ResourceProvider resourceProvider) {
            this.resourceProvider = resourceProvider;
            return (T) this;
        }

        public abstract BaseBluetoothHandler build();
    }
}
