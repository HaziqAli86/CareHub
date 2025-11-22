package com.example.healthapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);

        // Set the specialized adapter for Doctors
        viewPager2.setAdapter(new DoctorViewPagerAdapter(this));

        String[] titles = {"Appointments", "Message", "Settings"};
        int[] icons = {R.drawable.ic_calendar, R.drawable.ic_chat, R.drawable.ic_settings};

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    tab.setText(titles[position]);
                    tab.setIcon(icons[position]);
                }
        ).attach();

        setupBadgeListener(tabLayout);

        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) return;

                    // Get new FCM registration token
                    String token = task.getResult();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Save to Firestore so we know which phone belongs to this user
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                            .update("fcmToken", token);
                });
    }

    private void setupBadgeListener(TabLayout tabLayout) {
        // Ensure user is logged in to avoid crash
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(myUid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null || documentSnapshot == null) return;

                    if (documentSnapshot.contains("unreadCount")) {
                        Long count = documentSnapshot.getLong("unreadCount");

                        // For DOCTORS, "Message" is the 2nd tab (Index 1)
                        TabLayout.Tab tab = tabLayout.getTabAt(1);

                        if (tab != null) {
                            if (count != null && count > 0) {
                                tab.getOrCreateBadge().setVisible(true);
                                tab.getOrCreateBadge().setNumber(count.intValue());
                            } else {
                                tab.getOrCreateBadge().setVisible(false);
                            }
                        }
                    }
                });
    }

    // INTERNAL ADAPTER CLASS
    class DoctorViewPagerAdapter extends FragmentStateAdapter {
        public DoctorViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new DoctorScheduleFragment(); // The list we just made
                case 1: return new MessageFragment();        // Reuse existing
                case 2: return new SettingsFragment();       // Reuse existing
                default: return new DoctorScheduleFragment();
            }
        }

        @Override
        public int getItemCount() { return 3; }
    }
}