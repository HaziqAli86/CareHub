package com.example.healthapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AllDoctorsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctors);

        // Back Button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_all_doctors);

        // Use GridLayout (2 columns) so it looks like a catalogue
        // Or use LinearLayoutManager for a simple vertical list.
        // Let's use Grid for a better look:
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(this, doctorList);
        recyclerView.setAdapter(adapter);

        fetchAllDoctors();
    }

    private void fetchAllDoctors() {
        db.collection("doctors").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    doctorList.clear();
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        Doctor doc = d.toObject(Doctor.class);
                        if (doc != null) {
                            doc.setId(d.getId());
                            doctorList.add(doc);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}