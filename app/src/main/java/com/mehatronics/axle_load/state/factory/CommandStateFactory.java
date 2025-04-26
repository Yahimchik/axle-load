package com.mehatronics.axle_load.state.factory;

import com.mehatronics.axle_load.state.CommandStateHandler;

public interface CommandStateFactory {
    CommandStateHandler createInitialState();
}
