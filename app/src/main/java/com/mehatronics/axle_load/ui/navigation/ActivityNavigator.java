package com.mehatronics.axle_load.ui.navigation;

import static com.mehatronics.axle_load.constants.ButtonsConstants.BT_COM_MINI;
import static com.mehatronics.axle_load.constants.ButtonsConstants.DPS_BTN;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class ActivityNavigator {
    private final Class<? extends Activity>[] activities;

    @Inject
    public ActivityNavigator(Class<? extends Activity>[] activities) {
        this.activities = activities;
    }

    public void registerActivities(Activity activity) {
        List<MaterialCardView> buttons = searchButtons(activity);
        if (buttons.size() == activities.length) {
            for (int i = 0; i < buttons.size(); i++) {
                MaterialCardView button = buttons.get(i);
                if (button != null) {
                    final Class<? extends Activity> activityClass = activities[i];
                    button.setOnClickListener(v -> startActivity(activity, activityClass));
                }
            }
        }
    }

    private void startActivity(Activity activity, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        activity.startActivity(intent);
    }

    private List<MaterialCardView> searchButtons(Activity activity) {
        return Arrays.asList(
                activity.findViewById(DPS_BTN),
                activity.findViewById(BT_COM_MINI)
        );
    }
}