package com.mehatronics.axle_load.utils;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

public class TextWatcherFactory {
    public static TextWatcher createWatcher(Consumer<String> onChanged) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onChanged.accept(s.toString());
            }
        };
    }
}
