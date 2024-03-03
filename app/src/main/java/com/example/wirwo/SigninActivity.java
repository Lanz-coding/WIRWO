package com.example.wirwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;

public class SigninActivity extends Activity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;

    // Firebase authentication
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Finding views by their IDs
        emailEditText = findViewById(R.id.signin_email);
        passwordEditText = findViewById(R.id.create_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.signin_button);
        loginTextView = findViewById(R.id.login);

        // Setting click listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when the register button is clicked
                // Show loading indicator
                showLoading();

                // Retrieve user input
                final String user = emailEditText.getText().toString().trim();
                final String pass = passwordEditText.getText().toString().trim();

                // Validate input
                if (user.isEmpty()){
                    emailEditText.setError("Email cannot be empty");
                    hideLoading();
                    return;
                }
                if (pass.isEmpty()){
                    passwordEditText.setError("Password cannot be empty");
                    hideLoading();
                    return;
                }

                // Perform user authentication using Firebase
                auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SigninActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SigninActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(SigninActivity.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideLoading();
                    }
                });
            }
        });

        // Setting click listener for the login text view
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when the login text view is clicked
                // Create an Intent to start the LoginActivity
                Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLoading() {
        // Show loading indicator (if needed)
        // For example, you can show a progress dialog or change the UI to indicate loading
        // In this example, let's just disable the register button
        registerButton.setEnabled(false);
    }

    private void hideLoading() {
        // Hide loading indicator (if needed)
        // For example, you can hide a progress dialog or revert UI changes made during loading
        // In this example, let's just enable the register button
        registerButton.setEnabled(true);
    }
}
