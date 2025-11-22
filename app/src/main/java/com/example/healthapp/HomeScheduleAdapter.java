package com.example.healthapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeScheduleAdapter extends RecyclerView.Adapter<HomeScheduleAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> list;

    public HomeScheduleAdapter(Context context, List<Appointment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_home_schedule, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appt = list.get(position);
        holder.tvName.setText(appt.getDoctorName());
        holder.tvSpecialty.setText(appt.getDoctorSpecialty()); // Use Doctor Specialty
        holder.tvDate.setText(appt.getDate());
        holder.tvTime.setText(appt.getTime());
        // Hardcoded hospital for now, or add 'hospital' to Appointment model
        holder.tvHospital.setText("Cengkareng Hospital");
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty, tvDate, tvTime, tvHospital;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name_today);
            tvSpecialty = itemView.findViewById(R.id.tv_specialty_today);
            tvDate = itemView.findViewById(R.id.tv_date_today);
            tvTime = itemView.findViewById(R.id.tv_time_today);
            tvHospital = itemView.findViewById(R.id.tv_hospital_today);
            img = itemView.findViewById(R.id.img_doc_today);
        }
    }
}