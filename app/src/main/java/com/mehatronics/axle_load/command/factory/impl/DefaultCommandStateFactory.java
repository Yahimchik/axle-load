package com.mehatronics.axle_load.command.factory.impl;

import com.mehatronics.axle_load.command.CommandStateHandler;
import com.mehatronics.axle_load.command.factory.CommandStateFactory;
import com.mehatronics.axle_load.command.impl.FirstCommandState;

public class DefaultCommandStateFactory implements CommandStateFactory {
    @Override
    public CommandStateHandler createInitialState() {
        return new FirstCommandState();
    }
}
