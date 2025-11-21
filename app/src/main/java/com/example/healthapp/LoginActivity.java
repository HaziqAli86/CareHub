package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private RadioButton rbPatient, rbDoctor;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        rbPatient = findViewById(R.id.rb_patient);
        rbDoctor = findViewById(R.id.rb_doctor);

        // Buttons
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> registerUser());
    }

    // Check if user is already logged in when app starts
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // If user is logged in, check their role and redirect
            checkRoleAndRedirect(currentUser.getUid());
        }
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine Role
        String role = rbDoctor.isChecked() ? "doctor" : "patient";

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        saveUserRole(uid, email, role);
                    } else {
                        Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkRoleAndRedirect(auth.getCurrentUser().getUid());
                    } else {
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserRole(String uid, String email, String role) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("role", role);

        db.collection("users").document(uid).set(userMap)
                .addOnSuccessListener(aVoid -> {
                    if (role.equals("doctor")) {
                        createDoctorProfile(uid, email); // Create extra profile for doctors
                    } else {
                        checkRoleAndRedirect(uid);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving role", Toast.LENGTH_SHORT).show());
    }

    private void createDoctorProfile(String uid, String email) {
        // Create a default profile for the doctor in the 'doctors' collection
        // This allows them to show up in the patient's search list immediately
        // Note: We use the UID as the document ID so we can easily link them
        Doctor newDoctor = new Doctor(uid, "Dr. " + email.split("@")[0], "General", "HealthApp Hospital", "", 0.0, 0, 0);

        db.collection("doctors").document(uid).set(newDoctor)
                .addOnSuccessListener(aVoid -> checkRoleAndRedirect(uid));
    }

    private void checkRoleAndRedirect(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Intent intent;
                        if ("doctor".equals(role)) {
                            intent = new Intent(LoginActivity.this, DoctorMainActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        // Clear back stack so user can't go back to login screen
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "User role not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}