package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.R.id.langRecycler;
import static com.mehatronics.axle_load.constants.ButtonsConstants.SWITCH_LANGUAGE_BTN;
import static com.mehatronics.axle_load.constants.FormatConstants.LANGUAGE_FLAGS;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;
import com.mehatronics.axle_load.domain.usecase.LanguageUseCase;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.ui.adapter.LanguageAdapter;
import com.mehatronics.axle_load.ui.navigation.ActivityNavigator;

import java.util.Arrays;

import javax.inject.Inject;

public class MainActivityBinder {

    private ImageButton btnChangeLang;
    private RecyclerView langRecyclerView;

    private final LanguageUseCase languageUseCase;
    private final ActivityNavigator navigator;

    @Inject
    public MainActivityBinder(LanguageUseCase languageUseCase, ActivityNavigator navigator) {
        this.languageUseCase = languageUseCase;
        this.navigator = navigator;
    }

    public void bind(Activity activity) {
        navigator.registerActivities(activity);

        btnChangeLang = activity.findViewById(SWITCH_LANGUAGE_BTN);
        langRecyclerView = activity.findViewById(langRecycler);

        AppLanguage currentLang = languageUseCase.getCurrentLanguage();
        setButtonFlag(currentLang);

        langRecyclerView.setLayoutManager(
                new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        );
        langRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LanguageAdapter adapter = new LanguageAdapter(Arrays.asList(AppLanguage.values()),
                lang -> {
                    languageUseCase.saveLanguage(lang);
                    LocaleHelper.setLocale(activity, lang);
                    setButtonFlag(lang);
                    animateLanguageRecycler(langRecyclerView, false, activity::recreate);
                });
        langRecyclerView.setAdapter(adapter);

        langRecyclerView.setVisibility(View.GONE);

        btnChangeLang.setOnClickListener(v -> {
            if (langRecyclerView.getVisibility() == View.GONE) {
                langRecyclerView.setVisibility(View.VISIBLE);
                animateLanguageRecycler(langRecyclerView, true, () -> {
                });
            } else {
                animateLanguageRecycler(langRecyclerView, false, () -> {
                });
            }
        });
    }

    private void setButtonFlag(AppLanguage lang) {
        Integer flag = LANGUAGE_FLAGS.get(lang);
        if (flag == null) {
            flag = R.drawable.ic_flag_united_kingdom;
        }
        btnChangeLang.setImageResource(flag);
    }

    private void animateLanguageRecycler(RecyclerView recyclerView, boolean show, Runnable onEnd) {
        recyclerView.post(() -> {
            int childCount = recyclerView.getChildCount();
            if (childCount == 0) {
                if (onEnd != null) onEnd.run();
                return;
            }

            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (show) {
                    child.setAlpha(0f);
                    child.setTranslationX(recyclerView.getWidth() - child.getLeft());
                }
            }

            recyclerView.setVisibility(show ? View.VISIBLE : recyclerView.getVisibility());

            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                int finalI = i;
                if (show) {
                    child.animate()
                            .alpha(1f)
                            .translationX(0)
                            .setStartDelay(i * 50L)
                            .setDuration(300)
                            .withEndAction(() -> {
                                if (finalI == childCount - 1 && onEnd != null) {
                                    recyclerView.post(onEnd);
                                }
                            })
                            .start();
                } else {
                    float targetTranslation = recyclerView.getWidth() - child.getLeft();
                    child.animate()
                            .alpha(0f)
                            .translationX(targetTranslation)
                            .setStartDelay(i * 50L)
                            .setDuration(300)
                            .withEndAction(() -> {
                                if (finalI == childCount - 1) {
                                    recyclerView.setVisibility(View.GONE);
                                    if (onEnd != null) recyclerView.post(onEnd);
                                }
                            })
                            .start();
                }
            }
        });
    }
}