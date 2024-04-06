package com.example.wirwo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.signin_username);
        emailEditText = findViewById(R.id.signin_email);
        passwordEditText = findViewById(R.id.create_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.signin_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                int minPasswordLength = 8; // Minimum password length
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // Regular expression for email format validation

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    DialogHelper.showDialogWithTitle(RegisterActivity.this, "Empty Fields", "Please fill out all the information and try again.", null);
                } else if (!email.matches(emailPattern)) {
                    DialogHelper.showDialogWithTitle(RegisterActivity.this, "Invalid Email", "Please enter a valid email address.", null);
                } else if (password.length() < minPasswordLength) {
                    DialogHelper.showDialogWithTitle(RegisterActivity.this, "Weak Password", "Password must be at least " + minPasswordLength + " characters long.", null);
                } else {
                    if (confirmPassword.equals(password)) {
                        DialogHelper.showDialogWithOkCancel(RegisterActivity.this,
                                "Confirm Sign-up",
                                "Are the information correct? Click OK to proceed.",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // OK button clicked
                                        // Register the user
                                        registerUser(username, email, password);
                                    }
                                },
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Cancel button clicked
                                        // Dismiss the dialog (nothing to do here)
                                    }
                                });
                    } else {
                        DialogHelper.showDialogWithTitle(RegisterActivity.this, "", "Passwords do not match", null);
                    }
                }
            }
        });

    }

    private void registerUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            // Save username in Firebase Realtime Database
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            usersRef.child(uid).child("username").setValue(username);

                            sendVerificationEmail(user);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DialogHelper.showDialogWithTitle(RegisterActivity.this, "SIGN UP", "Check your email to verify your account to finish the registration process.", new DialogHelper.OnOkClickListener() {
                                @Override
                                public void onOkClicked() {
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                        } else {
                            DialogHelper.showDialogWithTitle(RegisterActivity.this, "SIGN UP", "Account already exist.", null);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}
