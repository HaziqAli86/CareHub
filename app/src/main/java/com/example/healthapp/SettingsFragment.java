package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView tvEmail = view.findViewById(R.id.tv_user_email);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        // Display current user email
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
        }

        // LOGOUT LOGIC
        btnLogout.setOnClickListener(v -> {
            // 1. Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // 2. Go back to Login Activity and CLEAR the back stack
            // This prevents the user from pressing "Back" and seeing the app again
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}