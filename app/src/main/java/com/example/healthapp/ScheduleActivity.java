package com.example.healthapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.recycler_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadAppointments();
    }

    private void loadAppointments() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query: Give me appointments where patientId == current user
        db.collection("appointments")
                .whereEqualTo("patientId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            Appointment appt = d.toObject(Appointment.class);
                            appointmentList.add(appt);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No appointments found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                });
    }
}