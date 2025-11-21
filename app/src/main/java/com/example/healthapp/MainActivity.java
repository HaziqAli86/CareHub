package com.example.healthapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

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
    }
}