package com.mehatronics.axle_load.ui.navigation;

import static com.mehatronics.axle_load.constants.ButtonsConstants.DDS_BTN;
import static com.mehatronics.axle_load.constants.ButtonsConstants.DPS_BTN;
import static com.mehatronics.axle_load.constants.ButtonsConstants.DSS_BTN;
import static com.mehatronics.axle_load.constants.ButtonsConstants.SWITCH_LANGUAGE_BTN;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.Button;

import com.mehatronics.axle_load.ui.viewModel.LanguageViewModel;
import com.mehatronics.axle_load.helper.LocaleHelper;

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

    public void registerLanguageSwitcher(Activity activity, LanguageViewModel viewModel) {
        String lang = PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getString("app_lang", "en");

        LocaleHelper.setLocale(activity, lang);

        var btnChangeLang = activity.findViewById(SWITCH_LANGUAGE_BTN);
        if (btnChangeLang != null) {
            btnChangeLang.setOnClickListener(v -> {
                viewModel.toggleLanguage();
                activity.recreate();
            });
        }
    }

    private void startActivity(Activity activity, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        activity.startActivity(intent);
    }

    private List<Button> searchButtons(Activity activity) {
        return Arrays.asList(
                activity.findViewById(DPS_BTN),
                activity.findViewById(DSS_BTN),
                activity.findViewById(DDS_BTN)
        );
    }
}


