package com.example.healthapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    private Context context;
    private List<Doctor> doctorList;

    // Constructor
    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout we created in Step 1
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.tvName.setText(doctor.getName());
        holder.tvSpecialty.setText(doctor.getSpecialty());
        holder.tvRating.setText("★ " + doctor.getRating());

        // Load Image using Glide (checks if URL is valid, otherwise shows a default icon)
        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
            Glide.with(context).load(doctor.getImageUrl()).into(holder.imgDoctor);
        } else {
            holder.imgDoctor.setImageResource(R.mipmap.ic_launcher); // Default image
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);

            // This is why we implemented 'Serializable' in the Doctor class!
            // It allows us to pass the whole object at once.
            intent.putExtra("doctor_data", doctor);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    // --- NEW METHOD FOR TASK 2: SEARCH FUNCTIONALITY ---
    public void filterList(List<Doctor> filteredList) {
        this.doctorList = filteredList;
        notifyDataSetChanged();
    }
    // ---------------------------------------------------

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDoctor;
        TextView tvName, tvSpecialty, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDoctor = itemView.findViewById(R.id.img_doctor);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSpecialty = itemView.findViewById(R.id.tv_specialty);
            tvRating = itemView.findViewById(R.id.tv_rating);
        }
    }
}