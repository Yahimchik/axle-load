package com.mehatronics.axle_load.ui.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.data.service.PermissionObserverService;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.manager.SharedPreferencesManager;
import com.mehatronics.axle_load.domain.usecase.PermissionUseCase;


import com.mehatronics.axle_load.ui.viewModel.LanguageViewModel;
import com.mehatronics.axle_load.ui.viewModel.PermissionsViewModel;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.ui.navigation.ActivityNavigator;

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
    @Inject
    protected PermissionObserverService permissionObserverService;
    @Inject
    protected ActivityNavigator activityNavigator;
    @Inject
    protected SharedPreferencesManager manager;
    private LanguageViewModel languageViewModel;


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

        languageViewModel = new ViewModelProvider(this).get(LanguageViewModel.class);
        var permissionsViewModel = new ViewModelProvider(this).get(PermissionsViewModel.class);

        permissionObserverService.setActivity(this);
        permissionObserverService.setPermissionsViewModel(permissionsViewModel);

        if (savedInstanceState == null) {
            permissionObserverService.observePermissionsStatus(this);
            permissionObserverService.startPermissionFlow();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase));
    }

    /**
     * Вызывается при запуске активити.
     * Регистрирует активити в навигаторе.
     */
    @Override
    protected void onStart() {
        super.onStart();
        activityNavigator.registerActivities(this);
        activityNavigator.registerLanguageSwitcher(this, languageViewModel, manager);
    }
}