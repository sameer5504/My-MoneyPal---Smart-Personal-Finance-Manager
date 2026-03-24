package com.example.a1220847_1222350_courseproject;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;


public class NavigationActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    SharedPrefManager sharedPrefManager;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        dataBaseHelper = new DataBaseHelper(NavigationActivity.this, "USERS", null, 1);
        final ProfileFragment profileFragment = new ProfileFragment();
        final HomeFragment homeFragment = new HomeFragment();
        final IncomeFragment incomeFragment = new IncomeFragment();
        final ExpensesFragment expensesFragment = new ExpensesFragment();
        final BudgetFragment budgetFragment = new BudgetFragment();
        final SettingsFragment settingsFragment = new SettingsFragment();
        String cUser= sharedPrefManager.getLoggedInUser();
        dataBaseHelper.ensureDefaultCategories(cUser);
        replaceFragment(homeFragment);  // When app starts, start at home
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {  // When item is selected, handle based on the id
                if (item.getItemId() == R.id.nav_profile) {
                    Toast.makeText(NavigationActivity.this, "Profile Details", Toast.LENGTH_SHORT).show();
                    replaceFragment(profileFragment);
                }
                if (item.getItemId() == R.id.nav_home) {
                    Toast.makeText(NavigationActivity.this, "Home Opened", Toast.LENGTH_SHORT).show();
                    replaceFragment(homeFragment);
                }
                if (item.getItemId() == R.id.nav_income) {
                    Toast.makeText(NavigationActivity.this, "Income Details", Toast.LENGTH_SHORT).show();
                    replaceFragment(incomeFragment);
                }
                if (item.getItemId() == R.id.nav_expenses) {
                    Toast.makeText(NavigationActivity.this, "Expenses Details", Toast.LENGTH_SHORT).show();
                    replaceFragment(expensesFragment);
                }
                if (item.getItemId() == R.id.nav_budget) {
                    Toast.makeText(NavigationActivity.this, "Budget Goals", Toast.LENGTH_SHORT).show();
                    replaceFragment(budgetFragment);
                }
                if (item.getItemId() == R.id.nav_settings) {
                    Toast.makeText(NavigationActivity.this, "Settings Opened", Toast.LENGTH_SHORT).show();
                    replaceFragment(settingsFragment);
                }
                if (item.getItemId() == R.id.nav_logout) {
                    Toast.makeText(NavigationActivity.this, "Logged Out !", Toast.LENGTH_SHORT).show();
                    sharedPrefManager.logout();
                    startActivity(new Intent(NavigationActivity.this,MainActivity.class));
                }
                drawerLayout.closeDrawers();  // Close drawer when done handling the selection
                return true;
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {  // Close the drawer when back is pressed
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {  // If the drawer is open then close it
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {  // If the drawer is close then finish the activity
                    finish();
                }
            }
        });
    }
    private void replaceFragment(Fragment f){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,f);
        fragmentTransaction.commit();
    }
}