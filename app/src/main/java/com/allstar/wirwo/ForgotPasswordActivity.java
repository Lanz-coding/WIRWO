package com.allstar.wirwo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button sendButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.signin_email);
        sendButton = findViewById(R.id.send_button);
        firebaseAuth = FirebaseAuth.getInstance();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (!email.isEmpty()) {
                    sendPasswordResetEmail(email);
                } else {
                    DialogHelper.showDialogWithTitle(ForgotPasswordActivity.this, "Empty Fields", "Please fill out all the information and try again.", null);
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DialogHelper.showDialogWithTitle(ForgotPasswordActivity.this, "Email Sent", "Password Reset email successfully sent to your email. Please make sure you are registered with this email.", new DialogHelper.OnOkClickListener() {
                                @Override
                                public void onOkClicked() {
                                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                        } else {
                            // Check if the error is due to unregistered email
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                DialogHelper.showDialogWithTitle(ForgotPasswordActivity.this, "Invalid email", "Please enter a registered email and try again. If problem still persists, contact the administrator.", null);
                            } else {
                                // Handle other errors
                                DialogHelper.showDialogWithTitle(ForgotPasswordActivity.this, "Failed", "Please try again. If problem still persists, contact the administrator.", null);
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
        finish();
    }


}
