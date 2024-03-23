package com.example.wirwo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView createAccountTextView;
    private Switch rememberMeSwitch;

    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "login_preferences";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Finding views by their IDs
        usernameEditText = findViewById(R.id.login_username1);
        passwordEditText = findViewById(R.id.login_password1);
        loginButton = findViewById(R.id.login_button);
        createAccountTextView = findViewById(R.id.create);
        rememberMeSwitch = findViewById(R.id.rememberMeSwitch);

        // Set initial state of login button
        loginButton.setEnabled(false);

        // Load saved username and password if "Remember Me" is checked
        if (rememberMeSwitch.isChecked()) {
            loadCredentials();
        }

        // Add TextWatchers to monitor EditText fields
        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        // Setting click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Setting click listener for the create account text view
        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when the create account text view is clicked
                // Create an Intent to start the SigninActivity (replace with your Signin activity name)
                Intent intent = new Intent(LoginActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if username and password are not empty
        if (!username.isEmpty() && !password.isEmpty()) {
            // Append the domain to the username (modify if needed)
            String email = username + "@wirwo.com";

            // Attempt to sign in with email and password
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            // Login successful, navigate to Dashboard activity
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Login failed, display error message
                            Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            // Save username and password if "Remember Me" is checked
            if (rememberMeSwitch.isChecked()) {
                saveCredentials(username, password);
            }
        } else {
            // Username or password is empty, display error message
            Toast.makeText(LoginActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
        }
    }


    // TextWatcher to monitor changes in EditText fields
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Enable login button only if both username and password fields are not empty
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginButton.setEnabled(!username.isEmpty() && !password.isEmpty());

            // Save credentials immediately if "Remember Me" is checked
            if (rememberMeSwitch.isChecked()) {
                saveCredentials(username, password);
            }
        }
    };

    // Save username and password to SharedPreferences
    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    // Load username and password from SharedPreferences
    private void loadCredentials() {
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        String password = sharedPreferences.getString(KEY_PASSWORD, "");
        usernameEditText.setText(username);
        passwordEditText.setText(password);
    }
}
