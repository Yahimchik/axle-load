package com.mehatronics.axle_load.ui.navigation;

import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.fragment.ConfigureFragment;
import com.mehatronics.axle_load.ui.fragment.DeviceDetailsFragment;

public class FragmentNavigator {
    private final FragmentManager fragmentManager;
    private boolean isFragmentOpened = false;

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
            Log.d("MyTag", fragment.getClass().getSimpleName() + " is opened");
        } else {
            Log.d("MyTag", fragment.getClass().getSimpleName() + " already opened");
        }
    }


    public void resetState() {
        isFragmentOpened = false;
    }

    public boolean isFragmentNotVisible() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment);
        return !(fragment instanceof DeviceDetailsFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}