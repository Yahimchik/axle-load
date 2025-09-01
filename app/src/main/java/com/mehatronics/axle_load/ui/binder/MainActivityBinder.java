package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.R.id.langRecycler;
import static com.mehatronics.axle_load.constants.ButtonsConstants.BT_COM_MINI;
import static com.mehatronics.axle_load.constants.ButtonsConstants.DPS_BTN;
import static com.mehatronics.axle_load.constants.ButtonsConstants.SWITCH_LANGUAGE_BTN;
import static com.mehatronics.axle_load.constants.FormatConstants.LANGUAGE_FLAGS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
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
    private MaterialCardView buttonDPS;
    private MaterialCardView buttonBTComMini;

    private final LanguageUseCase languageUseCase;
    private final ActivityNavigator navigator;

    @Inject
    public MainActivityBinder(LanguageUseCase languageUseCase, ActivityNavigator navigator) {
        this.languageUseCase = languageUseCase;
        this.navigator = navigator;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bind(Activity activity) {
        navigator.registerActivities(activity);

        this.btnChangeLang = activity.findViewById(SWITCH_LANGUAGE_BTN);
        this.langRecyclerView = activity.findViewById(langRecycler);
        this.buttonDPS = activity.findViewById(DPS_BTN);
        this.buttonBTComMini = activity.findViewById(BT_COM_MINI);

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

        buttonDPS.setOnTouchListener(MainActivityBinder::addMotion);
        buttonBTComMini.setOnTouchListener(MainActivityBinder::addMotion);
    }

    public static boolean addMotion(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                break;
        }
        return false;
    }

    private void setButtonFlag(AppLanguage lang) {
        Integer flag = LANGUAGE_FLAGS.get(lang);
        if (flag == null) {
            flag = R.drawable.ic_flag_united_kingdom;
        }
        btnChangeLang.setImageResource(flag);
    }

    private void animateLanguageRecycler(RecyclerView recyclerView, boolean show, Runnable onEnd) {
        int childCount = recyclerView.getChildCount();
        if (childCount == 0) {
            recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
            if (onEnd != null) onEnd.run();
            return;
        }

        if (show) recyclerView.setVisibility(View.VISIBLE);

        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            child.animate().cancel();

            float startAlpha = show ? 0f : 1f;
            float endAlpha = show ? 1f : 0f;

            float startTranslation = show ? recyclerView.getWidth() : 0f;
            float endTranslation = show ? 0f : recyclerView.getWidth();

            child.setAlpha(startAlpha);
            if (show) child.setTranslationX(startTranslation);

            int finalI = i;
            child.animate()
                    .alpha(endAlpha)
                    .translationX(endTranslation)
                    .setStartDelay(i * 30L)
                    .setDuration(250)
                    .withEndAction(() -> {
                        if (finalI == childCount - 1) {
                            if (!show) recyclerView.setVisibility(View.GONE);
                            if (onEnd != null) onEnd.run();
                        }
                    })
                    .start();
        }
    }
}