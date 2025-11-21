package com.example.healthapp;

import android.content.Intent;
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

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    // We can reuse AppointmentAdapter because it displays the names perfectly
    // But we need to override the Click Listener
    private AppointmentAdapter adapter;
    private List<Appointment> list;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false); // Reuse schedule layout

        recyclerView = view.findViewById(R.id.recycler_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();

        // Create a custom adapter setup or Click Logic
        adapter = new AppointmentAdapter(getContext(), list) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);

                Appointment appt = list.get(position);

                // OVERRIDE CLICK to open CHAT instead of details
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ChatActivity.class);

                    String currentRole = "patient"; // You might want to fetch this properly
                    // But simplest logic:
                    // If I am the patient, I want to chat with the Doctor (receiver = doctorId)
                    // If I am the doctor, I want to chat with the Patient (receiver = patientId)

                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (myUid.equals(appt.getPatientId())) {
                        // I am the Patient -> Chat with Doctor
                        intent.putExtra("receiverId", appt.getDoctorId());
                        intent.putExtra("receiverName", appt.getDoctorName());
                    } else {
                        // I am the Doctor -> Chat with Patient
                        intent.putExtra("receiverId", appt.getPatientId());
                        intent.putExtra("receiverName", appt.getPatientName());
                    }

                    startActivity(intent);
                });
            }
        };

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        loadContacts();

        return view;
    }

    private void loadContacts() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // We query appointments to find people to chat with
        // This query needs to be smart.
        // Ideally, you check if the user is a doctor or patient first.

        // QUICK FIX: Query both fields. If I am listed as patient OR doctor in the appointment.

        db.collection("appointments").whereEqualTo("patientId", uid).get()
                .addOnSuccessListener(snaps -> {
                    list.clear();
                    for(DocumentSnapshot d : snaps) list.add(d.toObject(Appointment.class));

                    // Also check if I am a doctor
                    db.collection("appointments").whereEqualTo("doctorId", uid).get()
                            .addOnSuccessListener(snaps2 -> {
                                for(DocumentSnapshot d : snaps2) list.add(d.toObject(Appointment.class));
                                adapter.notifyDataSetChanged();
                            });
                });
    }
}