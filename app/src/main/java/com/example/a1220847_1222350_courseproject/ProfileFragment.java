package com.example.a1220847_1222350_courseproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    EditText editTextEmail, editTextFN, editTextLN, editTextPassword, editTextConfirm;
    Button buttonUpdate;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        editTextEmail = view.findViewById(R.id.editTextTextEmailAddress_signup);
        editTextEmail.setEnabled(false);
        editTextFN = view.findViewById(R.id.editTextText_signup_first_name);
        editTextLN = view.findViewById(R.id.editTextText_signup_last_name);
        editTextPassword = view.findViewById(R.id.editTextTextPasswordSignUp);
        editTextConfirm = view.findViewById(R.id.editTextTextPassword_signup_confirm);
        buttonUpdate = view.findViewById(R.id.button_update_user);
        dataBaseHelper = new DataBaseHelper(getContext(), "USERS", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        String user_email = sharedPrefManager.getLoggedInUser();
        editTextEmail.setText(user_email);
        Cursor MyCursor = dataBaseHelper.getUserUsingEmail(user_email);
        if (MyCursor.moveToFirst()) {
            editTextFN.setText(MyCursor.getString(1));
            editTextLN.setText(MyCursor.getString(2));
        }
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fn = editTextFN.getText().toString().trim();
                String ln = editTextLN.getText().toString().trim();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirm.getText().toString();
                boolean correctInputs = true;
                if (fn.length() < 3 || fn.length() > 10) {
                    correctInputs = false;
                    editTextFN.setError("First Name length should be 3-10");
                }
                if (ln.length() < 3 || ln.length() > 10) {
                    correctInputs = false;
                    editTextLN.setError("Last Name length should be 3-10");
                }
                if (password.isEmpty() && confirmPassword.isEmpty()) {  // If password did not change
                    password = MyCursor.getString(3);
                } else {  // If password changed
                    if (password.length() < 6 || fn.length() > 12 || !isValidPassword(password)) {
                        correctInputs = false;
                        editTextPassword.setError("Password should contain at least one uppercase,one lowercase and one digit and 6-12 letters long");
                    }
                    if (!confirmPassword.equals(password)) {
                        correctInputs = false;
                        editTextConfirm.setError("Passwords do not match !");
                    }
                }
                if (correctInputs) {
                    User updatedUser = new User(user_email, fn, ln, password);
                    dataBaseHelper.editUser(updatedUser);
                    Toast.makeText(getContext(), "User Updated !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error updating user !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean isValidPassword(String pass) { // Returns  if password is valid
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