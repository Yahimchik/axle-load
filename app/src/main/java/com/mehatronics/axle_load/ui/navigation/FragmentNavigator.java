package com.mehatronics.axle_load.ui.navigation;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.fragment.ConfigureFragment;

public class FragmentNavigator {
    private final FragmentManager fragmentManager;

    public FragmentNavigator(AppCompatActivity activity) {
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    public void initConfigureButton(Button configureButton) {
        if (configureButton != null) {
            configureButton.setOnClickListener(v -> {
                if (fragmentManager.findFragmentByTag(ConfigureFragment.class.getSimpleName()) == null) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment, new ConfigureFragment(), ConfigureFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    public void showFragment(Fragment fragment) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment);
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            replaceFragment(fragment);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}