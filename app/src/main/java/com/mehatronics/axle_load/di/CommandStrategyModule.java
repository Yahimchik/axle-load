package com.mehatronics.axle_load.di;

import com.mehatronics.axle_load.security.password_strategy.CommandStrategy;
import com.mehatronics.axle_load.security.password_strategy.impl.FirstAuthStrategy;
import com.mehatronics.axle_load.security.password_strategy.impl.NineAuthStrategy;
import com.mehatronics.axle_load.security.password_strategy.impl.SecondAuthStrategy;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
@InstallIn(SingletonComponent.class)
public class CommandStrategyModule {

    @Provides
    @IntoMap
    @StringKey("80-1")
    public CommandStrategy provideFirstFiftyCommandStrategy(FirstAuthStrategy strategy) {
        return strategy;
    }

    @Provides
    @IntoMap
    @StringKey("80-2")
    public CommandStrategy provideSecondFiftyCommandStrategy(SecondAuthStrategy strategy) {
        return strategy;
    }

    @Provides
    @IntoMap
    @StringKey("80-9")
    public CommandStrategy provideNineFiftyCommandStrategy(NineAuthStrategy strategy) {
        return strategy;
    }
}
