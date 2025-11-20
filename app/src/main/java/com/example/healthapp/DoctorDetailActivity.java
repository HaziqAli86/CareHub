package com.example.healthapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID; // To generate unique IDs

public class DoctorDetailActivity extends AppCompatActivity {

    private TextView tvName, tvSpecialty, tvPatients, tvRating;
    private CircleImageView imgDoctor;
    private ImageView btnBack;
    private Button btnBook;
    private ChipGroup chipGroupDate, chipGroupTime;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        // 1. Initialize Views
        tvName = findViewById(R.id.detail_name);
        tvSpecialty = findViewById(R.id.detail_specialty);
        tvPatients = findViewById(R.id.detail_patients);
        tvRating = findViewById(R.id.detail_rating);
        imgDoctor = findViewById(R.id.detail_img);
        btnBack = findViewById(R.id.btn_back);
        btnBook = findViewById(R.id.btn_book_appointment);
        chipGroupDate = findViewById(R.id.chip_group_date);
        chipGroupTime = findViewById(R.id.chip_group_time);

        // 2. Get Data from Intent
        doctor = (Doctor) getIntent().getSerializableExtra("doctor_data");

        // 3. Populate UI
        if (doctor != null) {
            tvName.setText(doctor.getName());
            tvSpecialty.setText(doctor.getSpecialty());
            tvPatients.setText(String.valueOf(doctor.getPatients()));
            tvRating.setText(String.valueOf(doctor.getRating()));

            if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
                Glide.with(this).load(doctor.getImageUrl()).into(imgDoctor);
            }
        }

        // 4. Back Button Logic
        btnBack.setOnClickListener(v -> finish());

        btnBook.setOnClickListener(v -> {
            // 1. Check if user picked Date and Time
            int selectedDateId = chipGroupDate.getCheckedChipId();
            int selectedTimeId = chipGroupTime.getCheckedChipId();

            if (selectedDateId == -1 || selectedTimeId == -1) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
                return; // Stop here
            }

            // 2. Get the text from the selected chips
            Chip dateChip = findViewById(selectedDateId);
            Chip timeChip = findViewById(selectedTimeId);
            String dateStr = dateChip.getText().toString();
            String timeStr = timeChip.getText().toString();

            // 3. Check if user is logged in
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "User not logged in. Restart app.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Create the Appointment Object
            String uid = auth.getCurrentUser().getUid();
            String appointmentId = UUID.randomUUID().toString(); // Generate a random ID

            Appointment appointment = new Appointment(
                    appointmentId,
                    uid,
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSpecialty(),
                    dateStr,
                    timeStr,
                    "upcoming" // Default status
            );

            // 5. Save to Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Disable button so they can't double click
            btnBook.setEnabled(false);
            btnBook.setText("Booking...");

            db.collection("appointments").document(appointmentId)
                    .set(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Appointment Booked Successfully!", Toast.LENGTH_LONG).show();
                        finish(); // Close this screen and go back to Home
                    })
                    .addOnFailureListener(e -> {
                        btnBook.setEnabled(true);
                        btnBook.setText("Book Appointment");
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}