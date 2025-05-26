package com.mehatronics.axle_load;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.navigation.ActivityNavigator;
import com.mehatronics.axle_load.permissions.observer.PermissionObserver;
import com.mehatronics.axle_load.permissions.usecase.PermissionUseCase;
import com.mehatronics.axle_load.viewModel.PermissionsViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Главная активити приложения, отвечающая за инициализацию основных компонентов,
 * таких как проверка разрешений и навигация между экранами.
 * <p>
 * Использует Hilt для внедрения зависимостей {@link PermissionUseCase} и {@link ActivityNavigator}.
 * Инициализирует {@link PermissionsViewModel} для управления статусом разрешений.
 * </p>
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    /**
     * UseCase для работы с разрешениями пользователя.
     * Внедряется через Dagger Hilt.
     */
    @Inject
    protected PermissionUseCase permissionUseCase;
    /**
     * Навигатор для управления переходами между активностями.
     * Внедряется через Dagger Hilt.
     */
    @Inject
    protected ActivityNavigator activityNavigator;

    /**
     * Вызывается при создании активити.
     * Инициализирует UI, ViewModel и наблюдатель разрешений.
     *
     * @param savedInstanceState сохраненное состояние, если оно было
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        var permissionsViewModel = new ViewModelProvider(this).get(PermissionsViewModel.class);
        var permissionObserver = new PermissionObserver(this, permissionUseCase, permissionsViewModel);

        permissionObserver.observePermissionsStatus(this);
    }

    /**
     * Вызывается при запуске активити.
     * Регистрирует активити в навигаторе.
     */
    @Override
    protected void onStart() {
        super.onStart();
        activityNavigator.registerActivities(this);
    }
}