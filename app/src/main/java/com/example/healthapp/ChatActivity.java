package com.example.healthapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messageList;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvName;

    private String receiverId, receiverName, senderId;
    private FirebaseFirestore db;
    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. Get Details from Intent
        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 2. Generate Chat Room ID (Unique for this pair of users)
        // Logic: Alphabetically sort IDs so "A_B" is the same as "B_A"
        if (senderId.compareTo(receiverId) < 0) {
            chatRoomId = senderId + "_" + receiverId;
        } else {
            chatRoomId = receiverId + "_" + senderId;
        }

        // 3. Init UI
        tvName = findViewById(R.id.tv_chat_name);
        tvName.setText(receiverName);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.recycler_chat);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // 4. Send Button
        btnSend.setOnClickListener(v -> sendMessage());

        // Reset unread count when I open the chat
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("unreadCount", 0); // Reset badge to 0

        // 5. Listen for Messages
        listenForMessages();
    }

    private void sendMessage() {
        String msgText = etMessage.getText().toString();
        if (msgText.isEmpty()) return;


        Message message = new Message(senderId, receiverId, msgText, System.currentTimeMillis(), false);

        // 1. Add the message (Existing cod
        db.collection("chats").document(chatRoomId).collection("messages").add(message);
        // 2. NEW: Increment unreadCount for the Receiver
        db.collection("users").document(receiverId)
                .update("unreadCount", com.google.firebase.firestore.FieldValue.increment(1));

        etMessage.setText(""); // Clear input
    }

    private void listenForMessages() {
        db.collection("chats").document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Message msg = dc.getDocument().toObject(Message.class);
                                messageList.add(msg);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        // Scroll to bottom
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }
}