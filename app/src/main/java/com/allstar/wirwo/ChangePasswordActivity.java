package com.allstar.wirwo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends Activity {

    private TextInputEditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.change_password_button);

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set onClickListener for changePasswordButton
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        final String currentPassword = currentPasswordEditText.getText().toString().trim();
        final String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            DialogHelper.showDialogWithTitle(ChangePasswordActivity.this, "Empty Fields", "Please fill out all the information and try again.", null);
            return; // Added return statement to exit the method if fields are empty
        }

        if (!newPassword.equals(confirmPassword)) {
            DialogHelper.showDialogWithTitle(ChangePasswordActivity.this, "", "Passwords do not match", null);
            return;
        } else{
            if (newPassword.length()<8){
                DialogHelper.showDialogWithTitle(ChangePasswordActivity.this, "Weak Password", "Password must be at least " + 8 + " characters long.", null);
                return;
            }
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Re-authenticate user before changing password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> reauthTask) {
                            if (reauthTask.isSuccessful()) {
                                // Re-authentication successful, now update the password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    DialogHelper.showSuccessChangePassDialog(ChangePasswordActivity.this, new DialogHelper.OnOkClickListener() {
                                                        @Override
                                                        public void onOkClicked() {
                                                            // Perform logout functionality
                                                            FirebaseAuth.getInstance().signOut();
                                                            // Redirect user to login screen or perform any other necessary actions
                                                            startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                                                        }
                                                    });
                                                } else {
                                                    DialogHelper.showDialogWithTitle(ChangePasswordActivity.this, "Failed", "Please try again.", null);
                                                }
                                            }
                                        });
                            } else {
                                // Re-authentication failed, show error message
                                DialogHelper.showDialogWithTitle(ChangePasswordActivity.this, "Wrong Password", "Current Password is incorrect.", null);
                            }
                        }
                    });
        }
    }

}
