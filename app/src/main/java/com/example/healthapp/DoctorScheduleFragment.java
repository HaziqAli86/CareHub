package com.example.healthapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleFragment extends Fragment {

    private RecyclerView recyclerView;
    private DoctorAppointmentAdapter adapter; // We will create this adapter next
    private List<Appointment> appointmentList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_schedule, container, false);

        recyclerView = view.findViewById(R.id.recycler_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentList = new ArrayList<>();
        adapter = new DoctorAppointmentAdapter(getContext(), appointmentList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void loadAppointments() {
        String myDoctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // QUERY CHANGE: We look for appointments where doctorId matches the current user
        db.collection("appointments")
                .whereEqualTo("doctorId", myDoctorId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appointmentList.clear();
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        Appointment appt = d.toObject(Appointment.class);
                        appointmentList.add(appt);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}