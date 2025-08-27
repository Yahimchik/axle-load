package com.mehatronics.axle_load.ui.navigation;

import static com.mehatronics.axle_load.constants.BundleKeys.APP_LANGUAGE;
import static com.mehatronics.axle_load.constants.ButtonsConstants.BT_COM_MINI;
import static com.mehatronics.axle_load.constants.ButtonsConstants.DPS_BTN;
import static com.mehatronics.axle_load.constants.ButtonsConstants.SWITCH_LANGUAGE_BTN;
import static com.mehatronics.axle_load.constants.FormatConstants.LANGUAGE_FLAGS;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.EN;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;
import com.mehatronics.axle_load.domain.manager.SharedPreferencesManager;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.ui.adapter.LanguageAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

    public void registerLanguageSwitcher(Activity activity, SharedPreferencesManager prefsManager) {
        String langCode = prefsManager.get(APP_LANGUAGE, EN_CODE);
        AppLanguage currentLang = AppLanguage.fromCode(langCode);

        LocaleHelper.setLocale(activity, langCode);

        var btnChangeLang = activity.findViewById(SWITCH_LANGUAGE_BTN);

        setLanguage(currentLang, (ImageButton) btnChangeLang);

        btnChangeLang.setOnClickListener(v ->
                showLanguagePopup(activity, btnChangeLang, selectedLang -> {
                    prefsManager.put(APP_LANGUAGE, selectedLang.getCode());
                    LocaleHelper.setLocale(activity, selectedLang);
                    activity.recreate();
                    setLanguage(selectedLang, (ImageButton) btnChangeLang);
                })
        );

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

    private void showLanguagePopup(Activity activity, View anchor, Consumer<AppLanguage> onSelected) {
        RecyclerView recyclerView = new RecyclerView(activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        final PopupWindow popup = new PopupWindow(recyclerView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        LanguageAdapter adapter = new LanguageAdapter(
                Arrays.asList(AppLanguage.values()),
                LANGUAGE_FLAGS,
                lang -> {
                    onSelected.accept(lang);
                    popup.dismiss();
                }
        );

        recyclerView.setAdapter(adapter);

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchor);
    }

    private void setLanguage(AppLanguage selected, ImageButton btnChangeLang) {
        Integer flag = LANGUAGE_FLAGS.get(selected);
        if (flag == null) {
            flag = R.drawable.ic_flag_united_kingdom;
        }
        btnChangeLang.setImageResource(flag);
    }
}