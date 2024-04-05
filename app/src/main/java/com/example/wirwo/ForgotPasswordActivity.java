package com.example.wirwo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.SignInMethodQueryResult;

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
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Check if the error is due to unregistered email
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(ForgotPasswordActivity.this, "This email is not registered. Please enter a registered email.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle other errors
                                Toast.makeText(ForgotPasswordActivity.this, "Failed to send password reset email. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
