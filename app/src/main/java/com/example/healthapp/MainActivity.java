package com.example.healthapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Build;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    // Icons for the tabs (Ensure you have these in res/drawable)
    // If you don't have them, replace with R.drawable.ic_launcher_foreground for now
    private int[] tabIcons = {
            R.drawable.ic_home,      // Create or download this icon
            R.drawable.ic_calendar,  // Create or download this icon
            R.drawable.ic_chat,      // Create or download this icon
            R.drawable.ic_settings   // Create or download this icon
    };

    private String[] tabTitles = {"Home", "Schedule", "Message", "Setting"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Login Check (Quick Fix)
        FirebaseAuth auth = FirebaseAuth.getInstance();

        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Disable swipe if you want it to act like a strict Bottom Nav
        // viewPager2.setUserInputEnabled(false);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    tab.setText(tabTitles[position]);
                    tab.setIcon(tabIcons[position]);
                }
        ).attach();

        // Ask for Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) return;

                    // Get new FCM registration token
                    String token = task.getResult();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    android.util.Log.d("FCM_TEST", "Device Token: " + token);

                    // Save to Firestore so we know which phone belongs to this user
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                            .update("fcmToken", token);
                });

        setupBadgeListener();
    }

    private void setupBadgeListener() {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // Listen to ALL chats to count unread messages addressed to ME
        // Note: In a complex app, you'd query specific chat rooms.
        // For this project, we use a Collection Group Query or simplified logic.

        // SIMPLIFIED STRATEGY FOR PROJECT:
        // We will assume all messages are in subcollections.
        // A proper count requires a Cloud Function trigger to update a counter on the User document.
        // HOWEVER, to do this purely in Android for your demo, we will check the "users" -> "unreadCount" field.
        // (See Step 4 below for how to update this count).

        FirebaseFirestore.getInstance().collection("users").document(myUid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null || documentSnapshot == null) return;

                    if (documentSnapshot.contains("unreadCount")) {
                        Long count = documentSnapshot.getLong("unreadCount");

                        // Get the Message Tab (Index 2 in your array)
                        TabLayout.Tab tab = tabLayout.getTabAt(2);

                        if (tab != null) {
                            if (count != null && count > 0) {
                                // Show Red Badge
                                tab.getOrCreateBadge().setVisible(true);
                                tab.getOrCreateBadge().setNumber(count.intValue());
                            } else {
                                // Hide Badge
                                tab.getOrCreateBadge().setVisible(false);
                            }
                        }
                    }
                });
    }
}