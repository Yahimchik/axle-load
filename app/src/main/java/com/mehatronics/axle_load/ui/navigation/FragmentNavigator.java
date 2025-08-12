package com.mehatronics.axle_load.ui.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mehatronics.axle_load.R;

public class FragmentNavigator {
    private final FragmentManager fragmentManager;

    public FragmentNavigator(AppCompatActivity activity) {
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    public void showFragment(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName();
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
        if (existingFragment == null) {
            replaceFragment(fragment);
        } else if (!existingFragment.isVisible()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, existingFragment, tag)
                    .commit();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }
}