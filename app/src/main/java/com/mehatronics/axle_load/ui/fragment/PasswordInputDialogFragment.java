package com.mehatronics.axle_load.ui.fragment;

import static android.view.KeyEvent.KEYCODE_BACK;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordListener;

public class PasswordInputDialogFragment extends DialogFragment {
    public static final String TAG = "PasswordInputDialog";
    private PasswordListener listener;

    public void setPasswordListener(PasswordListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password_input, null);
        EditText input = dialogView.findViewById(R.id.password_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Введите пароль")
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Отмена", (dialog, which) -> {
                    if (listener != null) listener.onPasswordCancelled();
                });

        AlertDialog dialog = builder.create();

        dialog.setOnKeyListener((DialogInterface d, int keyCode, KeyEvent event) -> keyCode == KEYCODE_BACK);

        dialog.setOnShowListener(d -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> {
                String password = input.getText().toString().trim();
                if (password.isEmpty()) {
                    input.setError("Пароль не может быть пустым");
                } else {
                    if (listener != null) listener.onPasswordSubmitted(password);
                    dismiss();
                }
            });
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}