package com.mehatronics.axle_load.state.factory.impl;

import com.mehatronics.axle_load.state.CommandStateHandler;
import com.mehatronics.axle_load.state.factory.CommandStateFactory;
import com.mehatronics.axle_load.state.impl.FirstAuthCommandState;

public class DefaultCommandStateFactory implements CommandStateFactory {
    @Override
    public CommandStateHandler createInitialState() {
        return new FirstAuthCommandState();
    }
}
