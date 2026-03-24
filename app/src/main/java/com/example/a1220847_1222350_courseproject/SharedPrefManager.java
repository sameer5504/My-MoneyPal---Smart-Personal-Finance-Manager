package com.example.a1220847_1222350_courseproject;
import android.content.Context;
import android.content.SharedPreferences;
public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "My Shared Preference";
    private static final int SHARED_PREF_PRIVATE = Context.MODE_PRIVATE;
    private static SharedPrefManager ourInstance = null;
    private static SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    static SharedPrefManager getInstance(Context context) {
        if (ourInstance != null)
        { return ourInstance;
        }
        ourInstance=new SharedPrefManager(context);
        return ourInstance;
    }
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, SHARED_PREF_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void saveEmail(String email) {
        editor.putString("REMEMBER_EMAIL", email);
        editor.commit();
    }
    public String getEmail() {
        return sharedPreferences.getString("REMEMBER_EMAIL", "");
    }
    public void setLoggedInUser(String email) {
        editor.putString("LOGGED_USER", email);
        editor.commit();
    }
    public String getLoggedInUser() {
        return sharedPreferences.getString("LOGGED_USER", "");
    }
    public void logout() {
        editor.remove("LOGGED_USER");
        editor.commit();
    }
}