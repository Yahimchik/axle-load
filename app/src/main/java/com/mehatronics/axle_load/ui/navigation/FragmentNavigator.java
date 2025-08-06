package com.mehatronics.axle_load.ui.navigation;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mehatronics.axle_load.R;

public class FragmentNavigator {
    private final FragmentManager fragmentManager;
    private static final int REQUEST_CODE_PICK_FILE = 1001;

    private Fragment currentFragment;

    public void setCurrentFragment(Fragment fragment) {
        this.currentFragment = fragment;
    }

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

    public void openDocumentPicker() {
        if (currentFragment == null) return;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        currentFragment.startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    public int getRequestCodePickFile() {
        return REQUEST_CODE_PICK_FILE;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }
}