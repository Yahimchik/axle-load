package com.mehatronics.axle_load.command.factory;

import com.mehatronics.axle_load.command.CommandStateHandler;

public interface CommandStateFactory {
    CommandStateHandler createInitialState();
}
