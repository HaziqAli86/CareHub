package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // Recommendations
    private RecyclerView recyclerViewRecommendations;
    private DoctorAdapter recommendationAdapter;
    private List<Doctor> recommendationList;

    // Search Dropdown
    private RecyclerView recyclerViewSearch;
    private SearchAdapter searchAdapter;
    private List<Doctor> allDoctorsList;
    private CardView searchResultCard;

    // NEW: Schedule Today
    private RecyclerView recyclerViewToday;
    private HomeScheduleAdapter todayAdapter;
    private List<Appointment> todayList;
    private TextView tvTodayLabel;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        // 1. Setup Recommendation Recycler
        recyclerViewRecommendations = view.findViewById(R.id.recycler_view_doctors);
        recyclerViewRecommendations.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationList = new ArrayList<>();
        recommendationAdapter = new DoctorAdapter(getContext(), recommendationList);
        recyclerViewRecommendations.setAdapter(recommendationAdapter);

        // 2. Setup Search Recycler
        searchResultCard = view.findViewById(R.id.card_search_results);
        recyclerViewSearch = view.findViewById(R.id.recycler_search);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        allDoctorsList = new ArrayList<>();
        searchAdapter = new SearchAdapter(getContext(), allDoctorsList);
        recyclerViewSearch.setAdapter(searchAdapter);

        // 3. NEW: Setup Schedule Today Recycler
        tvTodayLabel = view.findViewById(R.id.tv_today_label);
        recyclerViewToday = view.findViewById(R.id.recycler_today);
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getContext()));
        todayList = new ArrayList<>();
        todayAdapter = new HomeScheduleAdapter(getContext(), todayList);
        recyclerViewToday.setAdapter(todayAdapter);

        // 4. Fetch Data
        fetchDoctors();
        fetchTodayAppointments(); // <--- NEW CALL

        // 5. Search Logic
        EditText searchBar = view.findViewById(R.id.et_search);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterSearch(s.toString());
            }
        });

        // 6. Other Listeners
        View tvSeeAll = view.findViewById(R.id.tv_see_all);
        tvSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AllDoctorsActivity.class);
            startActivity(intent);
        });

        View btnNotif = view.findViewById(R.id.btn_notifications);
        btnNotif.setOnClickListener(v -> startActivity(new Intent(getContext(), NotificationActivity.class)));

        setupCategories(view);

        return view;
    }

    // --- NEW METHOD: Fetch Appointments for Today ---
    private void fetchTodayAppointments() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get Today's Date in the format "EEE dd" (e.g., "Sat 22")
        // Note: This matches the format used in your Chips (Mon 03, Tue 04)
        //SimpleDateFormat sdf = new SimpleDateFormat("EEE dd", Locale.ENGLISH);
        //String todayDateString = sdf.format(new Date());
        String todayDateString = "Tue 04";

        // For testing, if you don't have an appointment literally today,
        // you can hardcode this string to match a date in your database, e.g.:
        // String todayDateString = "Mon 03";

        db.collection("appointments")
                .whereEqualTo("patientId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    todayList.clear();
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        Appointment appt = d.toObject(Appointment.class);

                        // Check if the appointment date matches today's date
                        if (appt != null && appt.getDate().startsWith(todayDateString)) {
                            todayList.add(appt);
                        }

                        // OPTIONAL: If strict date matching is failing because your database
                        // has "Mon 03" but today is "Sat 22", you can temporarily
                        // remove the 'if' check to see ALL upcoming appointments here for testing.
                    }

                    // Show or Hide the section based on results
                    if (todayList.isEmpty()) {
                        tvTodayLabel.setVisibility(View.GONE);
                        recyclerViewToday.setVisibility(View.GONE);
                    } else {
                        tvTodayLabel.setVisibility(View.VISIBLE);
                        recyclerViewToday.setVisibility(View.VISIBLE);
                        todayAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void fetchDoctors() {
        db.collection("doctors").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recommendationList.clear();
                    allDoctorsList.clear();
                    int count = 0;
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        Doctor doc = d.toObject(Doctor.class);
                        if (doc != null) {
                            doc.setId(d.getId());
                            allDoctorsList.add(doc);
                            if (count < 5) {
                                recommendationList.add(doc);
                                count++;
                            }
                        }
                    }
                    recommendationAdapter.notifyDataSetChanged();
                });
    }

    private void filterSearch(String text) {
        if (text.isEmpty()) {
            searchResultCard.setVisibility(View.GONE);
            return;
        }
        List<Doctor> filteredList = new ArrayList<>();
        for (Doctor item : allDoctorsList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getSpecialty().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            searchResultCard.setVisibility(View.GONE);
        } else {
            searchResultCard.setVisibility(View.VISIBLE);
            searchAdapter.updateList(filteredList);
        }
    }

    private void setupCategories(View view) {
        // ... (Keep your existing category logic) ...
        // Just putting placeholder here to keep code short
        view.findViewById(R.id.btn_cat_doctor).setOnClickListener(v -> recyclerViewRecommendations.smoothScrollToPosition(0));
    }
}