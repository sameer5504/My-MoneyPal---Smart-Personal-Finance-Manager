package com.example.a1220847_1222350_courseproject;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    CheckBox checkBoxRememberMe;
    Button buttonSignIn, buttonSignUp;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        checkBoxRememberMe=findViewById(R.id.checkBox_rememberMe);
        buttonSignIn=findViewById(R.id.button_signIn);
        buttonSignUp=findViewById(R.id.button_signUp);
        dataBaseHelper=new DataBaseHelper(LoginActivity.this,"USERS",null,1);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        String saved_email = sharedPrefManager.getEmail();
        if(!(saved_email.isEmpty())){  // If email is saved then type it, and check the checked box
            editTextEmail.setText(saved_email);
            checkBoxRememberMe.setChecked(true);
        }
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // Go to sign up page
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String email = editTextEmail.getText().toString().trim();
              String password = editTextPassword.getText().toString();
              Cursor myCursor = dataBaseHelper.getUserUsingEmail(email);
              if(myCursor.moveToFirst()){
                  String storedPassword= myCursor.getString(3);
                  if (storedPassword.equals(password)){  // If correct username and password
                      if(checkBoxRememberMe.isChecked()){  // Save email for next time
                          sharedPrefManager.saveEmail(email);
                      }
                      sharedPrefManager.setLoggedInUser(email);  // Set the user as signed in
                     startActivity(new Intent(LoginActivity.this,NavigationActivity.class));
                  }else{
                      Toast.makeText(LoginActivity.this,"Incorrect Password !",Toast.LENGTH_SHORT).show();
                  }
              }else{
                  Toast.makeText(LoginActivity.this,"User not found !",Toast.LENGTH_SHORT).show();
              }
            }
        });
    }
}