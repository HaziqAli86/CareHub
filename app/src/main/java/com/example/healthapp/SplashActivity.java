package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Give it a small delay (e.g., 1.5 seconds) so the user sees the logo
        // while we load the data in the background
        new Handler().postDelayed(this::checkAuth, 1000);
    }

    private void checkAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // No user logged in? Go to Login Screen
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // User exists? Check if Doctor or Patient
            checkRole(currentUser.getUid());
        }
    }

    private void checkRole(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String role = document.getString("role");

                        Intent intent;
                        if ("doctor".equals(role)) {
                            intent = new Intent(this, DoctorMainActivity.class);
                        } else {
                            intent = new Intent(this, MainActivity.class);
                        }

                        startActivity(intent);
                        finish();
                    } else {
                        // Error edge case: User in Auth but not in Database
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // If internet fails, maybe stay on splash or go to login
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
    }
}