package com.allstar.wirwo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeUsernameActivity extends Activity {

    private TextInputEditText passwordEditText, newUsernameEditText;
    private TextView currentUsernameText;
    private Button changeUsernameButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        passwordEditText = findViewById(R.id.passwordEditText);
        newUsernameEditText = findViewById(R.id.newUsernameEditText);
        currentUsernameText = findViewById(R.id.currentUsernameText);
        changeUsernameButton = findViewById(R.id.change_username_button);

        // Set click listener for change username button
        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });

        // Display current username
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if user is not null
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("username");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.getValue(String.class);
                        currentUsernameText.setText("Current Username: "+username);
                    } else {
                        // Handle case where username data doesn't exist
                        currentUsernameText.setText("Current Username: "); // Or set a default message
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String text = "Current Username: ";
                    currentUsernameText.setText(text);
                }
            });
        } else {
            // Set default text if user is null
            String text = "Current Username: ";
            currentUsernameText.setText(text);
        }

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changeUsername() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String newPassword = passwordEditText.getText().toString().trim();
            String newUsername = newUsernameEditText.getText().toString().trim();

            // Check if new username and password are provided
            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newUsername)) {
                DialogHelper.showDialogWithTitle(ChangeUsernameActivity.this, "Empty Fields", "Please fill out all the information and try again.", null);;
                return;
            }

            // Create credentials for reauthentication
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), newPassword);

            // Re-authenticate user to update username
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update username in Firebase Auth
                                currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                                                .setDisplayName(newUsername)
                                                .build())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Update username in Firebase Realtime Database
                                                    mDatabase.child("users").child(currentUser.getUid()).child("username").setValue(newUsername)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DialogHelper.showDialogWithTitle(ChangeUsernameActivity.this, "Success!", "Username changed successfully.", null);
                                                                        finish();
                                                                    } else {
                                                                        DialogHelper.showDialogWithTitle(ChangeUsernameActivity.this, "Failed!", "Failed to change username. Please Try Again.", null);
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    DialogHelper.showDialogWithTitle(ChangeUsernameActivity.this, "Failed!", "Failed to change username. Please Try Again.", null);
                                                }
                                            }
                                        });
                            } else {
                                DialogHelper.showDialogWithTitle(ChangeUsernameActivity.this, "Incorrect Password!", "Password is Incorrect.", null);
                            }
                        }
                    });
        }
    }

}
