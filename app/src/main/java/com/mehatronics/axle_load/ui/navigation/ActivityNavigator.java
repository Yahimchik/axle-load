package com.mehatronics.axle_load.ui.navigation;

import static com.mehatronics.axle_load.constants.ButtonsConstants.BT_COM_MINI;
import static com.mehatronics.axle_load.constants.ButtonsConstants.DPS_BTN;
import static com.mehatronics.axle_load.constants.ButtonsConstants.SWITCH_LANGUAGE_BTN;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.ui.viewModel.LanguageViewModel;

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
        if (btnChangeLang instanceof ImageButton) {
            btnChangeLang.setOnClickListener(v -> {
                viewModel.toggleLanguage();
                activity.recreate();

                String newLang = PreferenceManager
                        .getDefaultSharedPreferences(activity)
                        .getString("app_lang", "en");

                int color = newLang.equals("en")
                        ? activity.getResources().getColor(android.R.color.white)
                        : activity.getResources().getColor(android.R.color.holo_blue_light);

                ((ImageButton) btnChangeLang).setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

                String abbrev = newLang.equals("en") ? "EN" : "RU";

                showLangPopup(activity, btnChangeLang, abbrev);
            });

            String currentLang = PreferenceManager
                    .getDefaultSharedPreferences(activity)
                    .getString("app_lang", "en");

            int initialColor = currentLang.equals("en")
                    ? activity.getResources().getColor(android.R.color.white)
                    : activity.getResources().getColor(android.R.color.holo_blue_light);

            ((ImageButton) btnChangeLang).setColorFilter(initialColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void startActivity(Activity activity, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        activity.startActivity(intent);
    }

    private List<Button> searchButtons(Activity activity) {
        return Arrays.asList(
                activity.findViewById(DPS_BTN),
                activity.findViewById(BT_COM_MINI)
        );
    }

    private void showLangPopup(Activity activity, View anchor, String abbrev) {
        TextView textView = new TextView(activity);
        textView.setText(abbrev);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(14f);
        textView.setPadding(24, 12, 24, 12);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#88000000"));
        bg.setCornerRadius(16f);
        textView.setBackground(bg);

        PopupWindow popup = new PopupWindow(textView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(false);

        textView.setAlpha(0f);
        popup.showAsDropDown(anchor);
        textView.animate()
                .alpha(1f)
                .setDuration(200)
                .withEndAction(() -> textView.animate()
                        .alpha(0f)
                        .setDuration(1500)
                        .withEndAction(popup::dismiss));
    }
}