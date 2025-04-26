package com.mehatronics.axle_load.di;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.mehatronics.axle_load.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ble.handler.BluetoothHandler;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class BluetoothModule {
    @Provides
    @Singleton
    public BluetoothAdapter provideBluetoothAdapter(@ApplicationContext Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager != null ? bluetoothManager.getAdapter() : BluetoothAdapter.getDefaultAdapter();
    }

    @Provides
    public static BluetoothHandler provideBluetoothConnectionManager(
            BluetoothViewModel bluetoothViewModel,
            BaseBluetoothActivity activity) {
        return new BluetoothHandler(bluetoothViewModel, activity);
    }
}