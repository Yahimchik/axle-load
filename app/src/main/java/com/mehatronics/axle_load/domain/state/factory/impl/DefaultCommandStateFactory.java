package com.mehatronics.axle_load.domain.state.factory.impl;

import com.mehatronics.axle_load.domain.state.CommandStateHandler;
import com.mehatronics.axle_load.domain.state.factory.CommandStateFactory;
import com.mehatronics.axle_load.domain.state.impl.FirstAuthCommandState;

public class DefaultCommandStateFactory implements CommandStateFactory {
    @Override
    public CommandStateHandler createInitialState() {
        return new FirstAuthCommandState();
    }
}