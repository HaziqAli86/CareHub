package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView (Note: use view.findViewById)
        recyclerView = view.findViewById(R.id.recycler_view_doctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(getContext(), doctorList);
        recyclerView.setAdapter(adapter);

        //loadDummyData();
        db = FirebaseFirestore.getInstance();

        fetchDoctors();

        return view;
    }

    private void loadDummyData() {
        doctorList.add(new Doctor("1", "Dr. Zubaidah", "Radiology", "Cengkareng Hospital", "", 4.8, 120, 5));
        doctorList.add(new Doctor("2", "drg. Claire", "Dentist", "Cengkareng Hospital", "", 5.0, 300, 8));
        doctorList.add(new Doctor("3", "Dr. Jhon", "General", "Jakarta Hospital", "", 4.5, 100, 3));
        doctorList.add(new Doctor("4", "Dr. Serenity", "Surgeon", "Central Hospital", "", 4.9, 500, 12));

        adapter.notifyDataSetChanged(); // IMPORTANT: Tell the view to update!
    }

    private void fetchDoctors() {
        db.collection("doctors").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    doctorList.clear(); // Clear list to avoid duplicates
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        Doctor doc = d.toObject(Doctor.class);
                        // Manually inject the ID so we can use it later
                        if (doc != null) {
                            doc.setId(d.getId());
                            doctorList.add(doc);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}