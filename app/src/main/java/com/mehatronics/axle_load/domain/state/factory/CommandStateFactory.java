package com.mehatronics.axle_load.domain.state.factory;

import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public interface CommandStateFactory {
    CommandStateHandler createInitialState();
}