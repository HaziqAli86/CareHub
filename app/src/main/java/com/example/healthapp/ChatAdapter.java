package com.example.healthapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    // Constants to identify who sent the message
    public static final int MSG_TYPE_LEFT = 0;  // Receiver
    public static final int MSG_TYPE_RIGHT = 1; // Sender

    private Context context;
    private List<Message> list;

    public ChatAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = list.get(position);
        holder.tvMessage.setText(msg.getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // This determines which layout to use (Left or Right)
    @Override
    public int getItemViewType(int position) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (list.get(position).getSenderId().equals(currentUserId)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
        }
    }
}