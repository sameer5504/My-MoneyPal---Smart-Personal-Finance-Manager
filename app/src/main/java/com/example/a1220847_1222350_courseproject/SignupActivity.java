package com.example.a1220847_1222350_courseproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
EditText editTextEmail,editTextFN,editTextLN,editTextPassword,editTextConfirm;
Button buttonSignup;
DataBaseHelper dataBaseHelper;
@Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    editTextEmail=findViewById(R.id.editTextTextEmailAddress_signup);
    editTextFN=findViewById(R.id.editTextText_signup_first_name);
    editTextLN=findViewById(R.id.editTextText_signup_last_name);
    editTextPassword=findViewById(R.id.editTextTextPasswordSignUp);
    editTextConfirm=findViewById(R.id.editTextTextPassword_signup_confirm);
    buttonSignup=findViewById(R.id.button_signup_confirm);
    dataBaseHelper = new DataBaseHelper(this,"USERS",null,1);
    buttonSignup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = editTextEmail.getText().toString().trim();
            String fn = editTextFN.getText().toString().trim();
            String ln = editTextLN.getText().toString().trim();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirm.getText().toString();
            boolean correctInputs = true;
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                correctInputs=false;
                editTextEmail.setError("Wrong Email format !");
            }
            if(fn.length()<3 || fn.length()>10){
                correctInputs=false;
                editTextFN.setError("First Name length should be 3-10 !");
            }
            if(ln.length()<3 || ln.length()>10){
                correctInputs=false;
                editTextLN.setError("Last Name length should be 3-10 !");
            }
            if( password.length()<6 || fn.length()>12 || !isValidPassword(password)){
                correctInputs=false;
                editTextPassword.setError("Password should contain at least one uppercase,one lowercase and one digit and 6-12 letters long !");
            }
            if(!confirmPassword.equals(password)){
                correctInputs=false;
                editTextConfirm.setError("Passwords do not match !");
            }
            Cursor myCursor = dataBaseHelper.getUserUsingEmail(email);
            if(myCursor.moveToFirst()){
                editTextEmail.setError("Email is already in use !");
                correctInputs=false;
            }
            if (correctInputs){
                User newUser = new User(email,fn,ln,password);
                dataBaseHelper.insertUser(newUser);
                Toast.makeText(SignupActivity.this,"Account Created Successfully !",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }else{
                Toast.makeText(SignupActivity.this,"Error creating account !",Toast.LENGTH_SHORT).show();
            }
        }
    });
}
    private boolean isValidPassword(String pass) {  // Returns  if password is valid
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        for (char c : pass.toCharArray()) {  // Check every char in password
            if (Character.isUpperCase(c)) upper = true;
            if (Character.isLowerCase(c)) lower = true;
            if (Character.isDigit(c)) digit = true;
        }
        return upper && lower && digit;
    }
}