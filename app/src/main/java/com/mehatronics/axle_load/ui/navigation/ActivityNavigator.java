package com.mehatronics.axle_load.ui.navigation;

import static com.mehatronics.axle_load.constants.BundleKeys.APP_LANGUAGE;
import static com.mehatronics.axle_load.constants.ButtonsConstants.BT_COM_MINI;
import static com.mehatronics.axle_load.constants.ButtonsConstants.DPS_BTN;
import static com.mehatronics.axle_load.constants.ButtonsConstants.SWITCH_LANGUAGE_BTN;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.EN;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.RU;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mehatronics.axle_load.domain.manager.SharedPreferencesManager;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.ui.viewModel.LanguageViewModel;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class ActivityNavigator {
    private static final String EN_CODE = EN.getCode();
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

    public void registerLanguageSwitcher(Activity activity, LanguageViewModel viewModel, SharedPreferencesManager prefsManager) {
        String lang = prefsManager.get(APP_LANGUAGE, EN_CODE);

        LocaleHelper.setLocale(activity, lang);

        var btnChangeLang = activity.findViewById(SWITCH_LANGUAGE_BTN);
        if (btnChangeLang instanceof ImageButton) {
            btnChangeLang.setOnClickListener(v -> {
                viewModel.toggleLanguage();
                activity.recreate();

                String newLang = prefsManager.get(APP_LANGUAGE, EN_CODE);

                int color = newLang.equals(EN_CODE)
                        ? ContextCompat.getColor(activity, android.R.color.white)
                        : ContextCompat.getColor(activity, android.R.color.holo_blue_light);

                ((ImageButton) btnChangeLang).setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

                String abbrev = newLang.equals(EN_CODE) ? EN.name() : RU.name();

                showLangPopup(activity, btnChangeLang, abbrev);
            });

            String currentLang = prefsManager.get(APP_LANGUAGE, EN_CODE);

            int initialColor = currentLang.equals(EN_CODE)
                    ? ContextCompat.getColor(activity, android.R.color.white)
                    : ContextCompat.getColor(activity, android.R.color.holo_blue_light);

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