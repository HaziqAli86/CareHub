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

public class ScheduleFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = view.findViewById(R.id.recycler_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(getContext(), appointmentList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    // We use onResume so the list refreshes every time you swipe back to this tab
    @Override
    public void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void loadAppointments() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("appointments")
                .whereEqualTo("patientId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appointmentList.clear();
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        appointmentList.add(d.toObject(Appointment.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}