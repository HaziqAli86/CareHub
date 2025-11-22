package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignup;
    private RadioButton rbDoctor;
    private TextView tvLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_email); // Make sure IDs match XML
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup); // Rename ID in XML to btn_signup if you want
        rbDoctor = findViewById(R.id.rb_doctor);
        tvLogin = findViewById(R.id.tv_login_link); // Add this TextView in XML

        btnSignup.setOnClickListener(v -> registerUser());

        // Link back to Login
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = rbDoctor.isChecked() ? "doctor" : "patient";

        if (email.isEmpty() || password.isEmpty()) return;

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                saveUserRole(uid, email, role);
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserRole(String uid, String email, String role) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("role", role);

        db.collection("users").document(uid).set(userMap).addOnSuccessListener(aVoid -> {
            if (role.equals("doctor")) {
                // Create Doctor Profile logic here (same as before)
                createDoctorProfile(uid, email);
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            }
        });
    }

    private void createDoctorProfile(String uid, String email) {
        Doctor newDoctor = new Doctor(uid, "Dr. " + email.split("@")[0], "General", "Hospital", "", 0.0, 0, 0);
        db.collection("doctors").document(uid).set(newDoctor)
                .addOnSuccessListener(aVoid -> {
                    startActivity(new Intent(this, DoctorMainActivity.class));
                    finishAffinity();
                });
    }
}