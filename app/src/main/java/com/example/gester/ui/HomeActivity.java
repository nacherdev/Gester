package com.example.gester.ui;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gester.ui.Fragments.HomeFragment;
import com.example.gester.ui.Fragments.AddFragment;
import com.example.gester.ui.Fragments.HistoryFragment;
import com.example.gester.ui.Fragments.NotificationsFragment;
import com.example.gester.ui.Fragments.CalendarFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.gester.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        String nombre = getIntent().getStringExtra("nombre");

        if (savedInstanceState == null) {
            Bundle mochila = new Bundle();
            mochila.putString("nombre", nombre);

            HomeFragment primerFragment = new HomeFragment();
            primerFragment.setArguments(mochila);

            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, primerFragment
            ).commit();
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            androidx.fragment.app.Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                HomeFragment homeFrag = new HomeFragment();
                Bundle mochilaReutilizable = new Bundle();
                mochilaReutilizable.putString("nombre", nombre);
                homeFrag.setArguments(mochilaReutilizable);
                selectedFragment = homeFrag;
            } else if (id == R.id.navigation_notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (id == R.id.navigation_add) {
                selectedFragment = new AddFragment();
            } else if (id == R.id.navigation_calendar) {
                selectedFragment = new CalendarFragment();
            } else if (id == R.id.navigation_history) {
                selectedFragment = new HistoryFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

    }
}