package com.mehatronics.axle_load.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.fragment.DeviceDetailsFragment;

import javax.inject.Inject;

public class DeviceNavigator {
    private final FragmentManager fragmentManager;

    @Inject
    public DeviceNavigator(AppCompatActivity activity) {
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    public void showDeviceDetailsFragment() {
        if (fragmentManager == null) {
            throw new IllegalStateException("FragmentManager is not available");
        }

        replaceFragment(DeviceDetailsFragment.newInstance());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(
                R.anim.fragment_slide_in,
                R.anim.fragment_slide_out,
                R.anim.fragment_pop_in,
                R.anim.fragment_pop_out
        );

        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}