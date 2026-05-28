package com.example.gester.ui;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.example.gester.R;
import com.example.gester.ui.Fragments.AddFragment;
import com.example.gester.ui.Fragments.CalendarFragment;
import com.example.gester.ui.Fragments.HistoryFragment;
import com.example.gester.ui.Fragments.HomeFragment;
import com.example.gester.ui.Fragments.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private long lastClickTime = 0;
    private static final long CLICK_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);

        hideSystemUI();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        String nombre = getIntent().getStringExtra("nombre");

        if (savedInstanceState == null) {
            Bundle mochila = new Bundle();
            mochila.putString("nombre", nombre);
            HomeFragment primerFragment = new HomeFragment();
            primerFragment.setArguments(mochila);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, primerFragment)
                    .commit();
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < CLICK_DELAY) {
                return false;
            }
            lastClickTime = currentTime;

            if (getSupportFragmentManager().isStateSaved()) {
                return false;
            }

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                HomeFragment homeFrag = new HomeFragment();
                Bundle mochila = new Bundle();
                mochila.putString("nombre", nombre);
                homeFrag.setArguments(mochila);
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
                        .commitAllowingStateLoss();
                return true;
            }
            return false;
        });
    }

    private void hideSystemUI() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }
}