package com.example.a1220847_1222350_courseproject;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        String loggedIn_user = sharedPrefManager.getLoggedInUser();
        if (loggedIn_user.isEmpty()) {  // Check if a user is logged in
            startActivity(new Intent(this,LoginActivity.class));  // If not, go to login page
        }else{
            startActivity(new Intent(this,NavigationActivity.class));  // If yes then go to navigation page
        }
        finish();
    }
}