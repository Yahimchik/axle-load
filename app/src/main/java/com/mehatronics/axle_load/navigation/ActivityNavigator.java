package com.mehatronics.axle_load.navigation;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.mehatronics.axle_load.R;

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
        List<Button> buttons = searchButtons(activity);
        if (buttons.size() == activities.length) {
            for (int i = 0; i < buttons.size(); i++) {
                Button button = buttons.get(i);
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

    private List<Button> searchButtons(Activity activity) {
        return Arrays.asList(
                activity.findViewById(R.id.buttonDPS),
                activity.findViewById(R.id.buttonDSS),
                activity.findViewById(R.id.buttonDDS)
        );
    }
}


