package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.constants.BundleKeys.KEY_CANCELLED;
import static com.mehatronics.axle_load.constants.BundleKeys.KEY_NEW_PASSWORD;
import static com.mehatronics.axle_load.constants.BundleKeys.KEY_OLD_PASSWORD;
import static com.mehatronics.axle_load.constants.BundleKeys.REQUEST_KEY;
import static com.mehatronics.axle_load.constants.StringConstants.STANDART_PASSWORD;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.localization.ResourceProvider;

import javax.inject.Inject;

public class ChangePasswordDialogFragment extends DialogFragment {

    private final ResourceProvider resourceProvider;
    private final PasswordRepository passwordRepository;

    @Inject
    public ChangePasswordDialogFragment(ResourceProvider resourceProvider, PasswordRepository passwordRepository) {
        this.resourceProvider = resourceProvider;
        this.passwordRepository = passwordRepository;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_change_password_dialog, null);

        EditText oldPasswordInput = view.findViewById(R.id.old_password_input);
        EditText newPasswordInput = view.findViewById(R.id.new_password_input);

        if (passwordRepository.isPasswordStandart()) {
            oldPasswordInput.setVisibility(View.GONE);
            passwordRepository.setPasswordStandart(true);
        } else {
            oldPasswordInput.setVisibility(View.VISIBLE);
            passwordRepository.setPasswordStandart(false);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(resourceProvider.getString(R.string.set_new_password))
                .setView(view)
                .setPositiveButton(resourceProvider.getString(R.string.ok), null)
                .setNegativeButton(resourceProvider.getString(R.string.cancel), (d, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(KEY_CANCELLED, true);
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                })
                .create();

        dialog.setOnShowListener(d -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> {
                String newPassword = newPasswordInput.getText().toString().trim();
                String oldPassword = passwordRepository.isPasswordStandart() ? STANDART_PASSWORD : oldPasswordInput.getText().toString().trim();

                boolean hasError = false;

                if (newPassword.isEmpty()) {
                    newPasswordInput.setError(resourceProvider.getString(R.string.password_cannot_be_empty));
                    hasError = true;
                }

                if (oldPassword.isEmpty()) {
                    oldPasswordInput.setError(resourceProvider.getString(R.string.password_cannot_be_empty));
                    hasError = true;
                }

                if (!hasError) {
                    Bundle result = new Bundle();
                    result.putString(KEY_OLD_PASSWORD, oldPassword);
                    result.putString(KEY_NEW_PASSWORD, newPassword);
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                    dismiss();
                }
            });
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}