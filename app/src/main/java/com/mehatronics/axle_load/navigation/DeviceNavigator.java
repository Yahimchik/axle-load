package com.mehatronics.axle_load.navigation;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.fragment.DeviceDetailsFragment;

import javax.inject.Inject;

public class DeviceNavigator {
    private final FragmentManager fragmentManager;
    private boolean isDeviceDetailsFragmentOpened = false;

    @Inject
    public DeviceNavigator(AppCompatActivity activity) {
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    public void showDeviceDetailsFragment() {
        if (fragmentManager == null) {
            throw new IllegalStateException("FragmentManager is not available");
        }

        if (!isDeviceDetailsFragmentOpened) {
            replaceFragment(DeviceDetailsFragment.newInstance());
            isDeviceDetailsFragmentOpened = true;
            Log.d("MyTag", "Device details fragment is opened");
        }
    }

    public void resetState() {
        isDeviceDetailsFragmentOpened = false;
    }

    public boolean isNotDeviceDetailsFragmentVisible() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment);
        return !(fragment instanceof DeviceDetailsFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(
                R.anim.fragment_slide_in,
                R.anim.fragment_slide_out,
                R.anim.fragment_pop_in,
                R.anim.fragment_pop_out
        );

        fragmentManager.popBackStack(DeviceDetailsFragment.class.getSimpleName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(DeviceDetailsFragment.class.getSimpleName());
        transaction.commit();
    }
}