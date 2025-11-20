package com.example.healthapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 0. Quick Login for Testing
        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    android.util.Log.d("Auth", "Signed in anonymously");
                } else {
                    android.util.Log.e("Auth", "Sign in failed", task.getException());
                }
            });
        }

        setContentView(R.layout.activity_main);

        // 1. Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_doctors);

        // Important: Set layout to HORIZONTAL to match Image 62
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // 2. Create Dummy Data
        doctorList = new ArrayList<>();
        doctorList.add(new Doctor("1", "Dr. Zubaidah", "Radiology", "Cengkareng Hospital", "", 4.8, 120, 5));
        doctorList.add(new Doctor("2", "Dr. Claire", "Dentist", "Cengkareng Hospital", "", 5.0, 300, 8));
        doctorList.add(new Doctor("3", "Dr. Jhon", "General", "Jakarta Hospital", "", 4.5, 100, 3));

        // 3. Set Adapter
        adapter = new DoctorAdapter(this, doctorList);
        recyclerView.setAdapter(adapter);

        // Find the bottom bar container (LinearLayout)
        android.widget.LinearLayout bottomBar = findViewById(R.id.bottom_bar);

        bottomBar.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });
    }
}