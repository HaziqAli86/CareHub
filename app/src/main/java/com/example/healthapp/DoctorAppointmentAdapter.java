package com.example.healthapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> list;

    public DoctorAppointmentAdapter(Context context, List<Appointment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reuse the same layout item_appointment.xml
        View v = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appt = list.get(position);

        // CHANGE 1: Display Patient Name instead of Doctor Name
        // We use a check just in case patientName is null
        if (appt.getPatientName() != null && !appt.getPatientName().isEmpty()) {
            holder.tvName.setText(appt.getPatientName());
        } else {
            holder.tvName.setText("Unknown Patient");
        }

        // CHANGE 2: Change the sub-text (Specialty) to something relevant
        // Since this is the doctor's view, we don't need to show the doctor's own specialty.
        holder.tvSpecialty.setText("Upcoming Appointment");

        holder.tvDate.setText(appt.getDate());
        holder.tvTime.setText(appt.getTime());
        holder.tvStatus.setText(appt.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty, tvDate, tvTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // We reuse the same IDs from item_appointment.xml
            tvName = itemView.findViewById(R.id.tv_doctor_name); // Re-purposed for Patient Name
            tvSpecialty = itemView.findViewById(R.id.tv_specialty);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}