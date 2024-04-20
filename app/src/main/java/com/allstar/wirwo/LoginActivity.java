package com.allstar.wirwo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.tasks.OnCompleteListener;



public class LoginActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView createAccountTextView;
    private TextView forgotPasswordTextView;
    private TextView faqsTextView;
    private TextView abousUsTextView;
    private CheckBox rememberMeSwitch;

    private FirebaseAuth auth;

    private SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "login_preferences";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!isNetworkAvailable()) {
            // Show dialog indicating no internet connection
            DialogHelper.showDialogWithTitle(LoginActivity.this, "NO INTENET CONNECTION", "Check your internet connection.", null );
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Finding views by their IDs
        usernameEditText = findViewById(R.id.login_username1);
        passwordEditText = findViewById(R.id.login_password1);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordTextView = findViewById(R.id.forgotPassword);
        createAccountTextView = findViewById(R.id.create);
        rememberMeSwitch = findViewById(R.id.rememberMeSwitch);
        faqsTextView = findViewById(R.id.faqs_btn_login);
        abousUsTextView = findViewById(R.id.about_us_btn_login);

        faqsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,FAQsActivity.class);
            startActivity(intent);
        });

        abousUsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,AboutUsActivity.class);
            startActivity(intent);
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Set initial state of login button
        loginButton.setEnabled(false);

        // Add TextWatchers to monitor EditText fields
        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        // Setting click listener for the login button
        loginButton.setOnClickListener(v -> login());

        // Setting click listener for the create account text view
        createAccountTextView.setOnClickListener(v -> {
            // Action to perform when the create account text view is clicked
            // Create an Intent to start the SigninActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Finish LoginActivity so the user cannot come back to it using the back button
        });

        // Pre-populate fields if "Remember Me" is checked
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
        usernameEditText.setText(savedUsername);
        passwordEditText.setText(savedPassword);

        // Check switch state for debugging purposes (remove later)
        if (rememberMeSwitch.isChecked()) {
            Toast.makeText(LoginActivity.this, "Remember Me is checked", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if username and password are not empty
        if (!username.isEmpty() && !password.isEmpty()) {
            // Append the domain to the username (modify if needed)
            String email = username;

            // Disable the login button while logging in
            loginButton.setEnabled(false);

            // Perform login asynchronously
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    // User is logged in and email is verified
                                    // Proceed to your main activity
                                    showCustomToast("Logged In Successfully", true);
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                } else {
                                    // User's email is not verified
                                    DialogHelper.showDialogWithTitle(LoginActivity.this,"Unverified Account", "Please verify your email first before logging in.", null);
                                    FirebaseAuth.getInstance().signOut(); // Sign out the user
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                DialogHelper.showDialogWithTitle(LoginActivity.this,"Log-In Failed", "Email and Password does not match, please try again.", null);
                            }
                            // Enable login button after login attempt
                            loginButton.setEnabled(true);
                        }
                    });
        } else {
            // Username or password is empty, display error message
            DialogHelper.showDialogWithTitle(LoginActivity.this, "Empty Fields", "Please fill out all the information and try again.", null);
        }
    }


    // TextWatcher to monitor changes in EditText fields
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

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

    // AsyncTask to perform login asynchronously
    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            Task<AuthResult> signInTask = auth.signInWithEmailAndPassword(email, password);
            try {
                Tasks.await(signInTask);
                return signInTask.isSuccessful(); // Login successful if task is successful
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Login failed
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Login successful, show custom toast with success icon
                showCustomToast("Login Successful", true);
                // Navigate to Dashboard activity
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // Finish LoginActivity so the user cannot come back to it using the back button
            } else {
                // Login failed, show custom toast with failure icon
                showCustomToast("Login Failed", false);
            }
        }
    }

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

    @Override
    public void onBackPressed() {
// Display confirmation dialog
        DialogHelper.showDialogWithOkCancel(LoginActivity.this,
                "Exit App",
                "Exit the WIRWO 2.0 App?",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel button clicked
                        // Dismiss the dialog (nothing to do here)
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
