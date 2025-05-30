package com.mehatronics.axle_load.di;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.mehatronics.axle_load.domain.usecase.ChangeLanguageUseCase;
import com.mehatronics.axle_load.domain.usecase.ChangeLanguageUseCaseImpl;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ui.activity.impl.DDSActivity;
import com.mehatronics.axle_load.ui.activity.impl.DPSActivity;
import com.mehatronics.axle_load.ui.activity.impl.DSSActivity;
import com.mehatronics.axle_load.handler.BluetoothHandler;
import com.mehatronics.axle_load.handler.DefaultPermissionHandler;
import com.mehatronics.axle_load.handler.PermissionHandler;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.localization.impl.AndroidResourceProvider;
import com.mehatronics.axle_load.mapper.DeviceMapper;
import com.mehatronics.axle_load.mapper.impl.DeviceMapperImpl;
import com.mehatronics.axle_load.navigation.ActivityNavigator;
import com.mehatronics.axle_load.navigation.FragmentNavigator;
import com.mehatronics.axle_load.service.impl.PermissionServiceImpl;
import com.mehatronics.axle_load.state.CommandStateHandler;
import com.mehatronics.axle_load.state.factory.impl.DefaultCommandStateFactory;
import com.mehatronics.axle_load.strategy.CommandStrategy;
import com.mehatronics.axle_load.strategy.impl.FirstAuthStrategy;
import com.mehatronics.axle_load.strategy.impl.NineAuthStrategy;
import com.mehatronics.axle_load.strategy.impl.SecondAuthStrategy;
import com.mehatronics.axle_load.domain.usecase.PermissionUseCase;
import com.mehatronics.axle_load.domain.viewModel.DeviceViewModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

/**
 * Модуль для предоставления зависимостей, связанных с активностями.
 */
@Module
@InstallIn(ActivityComponent.class)
public class ApplicationModule {

    /**
     * Предоставляет массив активностей, доступных для навигации.
     */
    @Provides
    public Class<? extends Activity>[] provideActivities() {
        return new Class[]{DPSActivity.class, DSSActivity.class, DDSActivity.class};
    }

    /**
     * Предоставляет объект {@link ActivityNavigator}, содержащий доступные активности.
     */
    @Provides
    public ActivityNavigator provideActivityNavigator(Class<? extends Activity>[] activities) {
        return new ActivityNavigator(activities);
    }

    /**
     * Модуль для биндинга глобальных зависимостей приложения.
     */
    @Module
    @InstallIn(SingletonComponent.class)
    public abstract static class AppModule {

        /**
         * Биндинг {@link AndroidResourceProvider} к интерфейсу {@link ResourceProvider}.
         */
        @Binds
        @Singleton
        public abstract ResourceProvider bindResourceProvider(AndroidResourceProvider impl);
    }

    /**
     * Модуль, предоставляющий зависимости Bluetooth и состояния команд.
     */
    @Module
    @InstallIn(SingletonComponent.class)
    public static class BluetoothModule {

        /**
         * Предоставляет {@link BluetoothAdapter}, полученный из {@link BluetoothManager}.
         *
         * @param context Контекст приложения.
         */
        @Provides
        @Singleton
        public static BluetoothAdapter provideBluetoothAdapter(@ApplicationContext Context context) {
            BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            return bluetoothManager != null
                    ? bluetoothManager.getAdapter()
                    : BluetoothAdapter.getDefaultAdapter();
        }

        /**
         * Предоставляет начальное состояние команды через {@link DefaultCommandStateFactory}.
         */
        @Provides
        public static CommandStateHandler provideCommandStateHandler() {
            return new DefaultCommandStateFactory().createInitialState();
        }

        /**
         * Предоставляет BluetoothHandler для связи между устройством и активностью.
         *
         * @param deviceViewModel ViewModel, содержащая состояние устройства.
         * @param activity        Активность, реализующая Bluetooth-логику.
         */
        @Provides
        public static BluetoothHandler provideBluetoothConnectionManager(
                DeviceViewModel deviceViewModel,
                BaseBluetoothActivity activity) {
            return new BluetoothHandler(deviceViewModel, activity);
        }
    }

    /**
     * Модуль, регистрирующий стратегии команд с помощью multibindings.
     */
    @Module
    @InstallIn(SingletonComponent.class)
    public static class CommandStrategyModule {

        /**
         * Предоставляет стратегию обработки команды "80-1".
         */
        @Provides
        @IntoMap
        @StringKey("80-1")
        public CommandStrategy provideFirstFiftyCommandStrategy(FirstAuthStrategy strategy) {
            return strategy;
        }

        /**
         * Предоставляет стратегию обработки команды "80-2".
         */
        @Provides
        @IntoMap
        @StringKey("80-2")
        public CommandStrategy provideSecondFiftyCommandStrategy(SecondAuthStrategy strategy) {
            return strategy;
        }

        /**
         * Предоставляет стратегию обработки команды "80-9".
         */
        @Provides
        @IntoMap
        @StringKey("80-9")
        public CommandStrategy provideNineFiftyCommandStrategy(NineAuthStrategy strategy) {
            return strategy;
        }
    }

    /**
     * Модуль, предоставляющий глобальный контекст приложения.
     */
    @Module
    @InstallIn(SingletonComponent.class)
    public static class ContextModule {

        /**
         * Предоставляет {@link Context} приложения.
         */
        @Provides
        public static Context provideContext(@ApplicationContext Context context) {
            return context;
        }

        @Provides
        @Singleton
        public static SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
            return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        }
    }

    @Module
    @InstallIn(SingletonComponent.class)
    public abstract static class MapperModule {

        @Binds
        @Singleton
        public abstract DeviceMapper bindDeviceMapper(DeviceMapperImpl impl);
    }

    /**
     * Модуль, предоставляющий зависимости для управления разрешениями.
     */
    @Module
    @InstallIn(SingletonComponent.class)
    public static abstract class PermissionModule {

        /**
         * Предоставляет реализацию сервиса управления разрешениями.
         *
         * @param context Контекст приложения.
         */
        @Provides
        @Singleton
        public static PermissionServiceImpl providePermissionManager(@ApplicationContext Context context) {
            return new PermissionServiceImpl(context);
        }

        /**
         * Предоставляет use case для проверки и запроса разрешений.
         */
        @Provides
        @Singleton
        public static PermissionUseCase providePermissionUseCase(PermissionServiceImpl permissionServiceImpl) {
            return new PermissionUseCase(permissionServiceImpl);
        }

        @Binds
        @Singleton
        public abstract PermissionHandler bindPermissionHandler(DefaultPermissionHandler impl);
    }

    @Module
    @InstallIn(ActivityComponent.class)
    public static class NavigatorModule {

        @Provides
        @ActivityScoped
        public FragmentNavigator provideFragmentNavigator(@ActivityContext Context context) {
            return new FragmentNavigator((AppCompatActivity) context);
        }
    }

    @Module
    @InstallIn(SingletonComponent.class)
    public abstract static class LanguageModule {

        @Binds
        @Singleton
        public abstract ChangeLanguageUseCase bindChangeLanguageUseCase(ChangeLanguageUseCaseImpl impl);
    }
}


