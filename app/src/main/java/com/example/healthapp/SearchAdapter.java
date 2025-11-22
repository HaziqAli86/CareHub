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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<Doctor> list;

    public SearchAdapter(Context context, List<Doctor> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<Doctor> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_search_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doc = list.get(position);
        holder.tvName.setText(doc.getName());
        holder.tvSpecialty.setText(doc.getSpecialty());

        if (doc.getImageUrl() != null && !doc.getImageUrl().isEmpty()) {
            Glide.with(context).load(doc.getImageUrl()).into(holder.img);
        }

        // Click Listener: Open Detail Page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);
            intent.putExtra("doctor_data", doc);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_search_name);
            tvSpecialty = itemView.findViewById(R.id.tv_search_specialty);
            img = itemView.findViewById(R.id.img_search_doc);
        }
    }
}