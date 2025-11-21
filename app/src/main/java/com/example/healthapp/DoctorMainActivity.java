package com.example.healthapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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