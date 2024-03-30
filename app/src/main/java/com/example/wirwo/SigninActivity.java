package com.example.wirwo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmEditText;
    private Button registerButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find views by their IDs
        usernameEditText = findViewById(R.id.signin_username1);
        passwordEditText = findViewById(R.id.create_password1);
        confirmEditText = findViewById(R.id.confirm_password1);
        registerButton = findViewById(R.id.signin_button);

        // Set initial state of register button
        registerButton.setEnabled(false);

        // Add TextWatchers to monitor EditText fields
        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        confirmEditText.addTextChangedListener(textWatcher);

        // Setting click listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when the register button is clicked
                // Show loading indicator
                showLoading();

                // Retrieve user input
                final String username = usernameEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();
                final String confirmPassword = confirmEditText.getText().toString().trim();

                // Perform user authentication using Firebase
                auth.createUserWithEmailAndPassword(username + "@wirwo.com", password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // SignUp successful, show custom toast with success icon
                                    showCustomToast("SignUp Successful", true);
                                    // Handle successful registration, for example, navigate to another activity
                                    Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    // SignUp failed, show custom toast with failure icon
                                    showCustomToast("SignUp Failed: " + task.getException().getMessage(), false);
                                    // Handle failed registration, for example, show an error message
                                }
                                hideLoading();
                            }
                        });
            }
        });
    }

    // Method to show loading indicator
    private void showLoading() {
        registerButton.setEnabled(false);
    }

    // Method to hide loading indicator
    private void hideLoading() {
        registerButton.setEnabled(true);
    }

    // TextWatcher to monitor changes in EditText fields
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmEditText.getText().toString().trim();

            boolean isValidUsername = !username.isEmpty();
            boolean isValidPassword = !password.isEmpty();
            boolean isValidConfirmPassword = !confirmPassword.isEmpty() && password.equals(confirmPassword);

            registerButton.setEnabled(isValidUsername && isValidPassword && isValidConfirmPassword);
        }
    };

    // Method to show custom toast with app icon
    private void showCustomToast(String message, boolean isSuccess) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_icon));

        ImageView iconImageView = layout.findViewById(R.id.toast_icon);
        TextView messageTextView = layout.findViewById(R.id.toast_text);

        // Set the app icon based on success or failure
        if (isSuccess) {
            // Set your success icon
            iconImageView.setImageResource(R.drawable.white_wirwo); // Replace with your success icon
        } else {
            // Set your failure icon
            iconImageView.setImageResource(R.drawable.white_wirwo); // Replace with your failure icon
        }

        // Set the message
        messageTextView.setText(message);

        // Create and show the toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void onBackPressed() {
        // Handle back button click
        super.onBackPressed();
        Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional, depends on your navigation flow
    }
}
