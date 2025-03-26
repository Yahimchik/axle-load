package com.mehatronics.axle_load.navigation;

import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.fragment.ConfigureFragment;
import com.mehatronics.axle_load.fragment.DeviceDetailsFragment;

import javax.inject.Inject;

public class FragmentNavigator {
    private final FragmentManager fragmentManager;
    private boolean isDeviceDetailsFragmentOpened = false;

    @Inject
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

    public void showFragment() {
        if (fragmentManager == null) {
            throw new IllegalStateException("FragmentManager is not available");
        }

        if (!isDeviceDetailsFragmentOpened) {
            replaceFragment(new DeviceDetailsFragment());
            isDeviceDetailsFragmentOpened = true;
            Log.d("MyTag", "Device details fragment is opened");
        }
    }

    public void resetState() {
        isDeviceDetailsFragmentOpened = false;
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