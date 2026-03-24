package com.example.a1220847_1222350_courseproject;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
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
    EditText editTextIncomeAmount;
    DatePicker datePickerIncome;
    Spinner spinnerIncomeCategory;
    EditText editTextIncomeDescription;
    Button btnAddIncome;
    Button btnUpdateIncome;
    Button btnCancelEdit;
    LinearLayout linearLayoutIncomeTransactions;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    private int selectedTransactionId = -1;  // When adding a new transaction
    String currentUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_income, container, false);
        dataBaseHelper = new DataBaseHelper(getContext(), "USERS", null, 1);
        editTextIncomeAmount = root.findViewById(R.id.editTextIncomeAmount);
        datePickerIncome = root.findViewById(R.id.datePickerIncome);
        spinnerIncomeCategory = root.findViewById(R.id.spinnerIncomeCategory);
        editTextIncomeDescription = root.findViewById(R.id.editTextIncomeDescription);
        btnAddIncome = root.findViewById(R.id.btnAddIncome);
        btnUpdateIncome = root.findViewById(R.id.btnUpdateIncome);
        btnCancelEdit = root.findViewById(R.id.btnCancelEdit);
        linearLayoutIncomeTransactions = root.findViewById(R.id.linearLayoutIncomeTransactions);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        currentUserEmail = sharedPrefManager.getLoggedInUser();
        // Load Income Categories and Transactions for the user
        dataBaseHelper.ensureDefaultCategories(currentUserEmail);
        loadCategories();
        loadTransactions();
        btnAddIncome.setOnClickListener(new View.OnClickListener() {  // Add a transaction
            @Override
            public void onClick(View v) {
                addTransaction();
            }
        });
        btnUpdateIncome.setOnClickListener(new View.OnClickListener() {  // Update a transaction
            @Override
            public void onClick(View v) {
                updateTransaction();
            }
        });
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {  // Stop editing and go back to add
            @Override
            public void onClick(View v) {
                clearSelection();
            }
        });
        return root;
    }
    private void loadCategories() {  // Load income categories
        Cursor myCursor = dataBaseHelper.getIncomeCategoriesByUser(currentUserEmail);
        java.util.ArrayList<String> names = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> ids = new java.util.ArrayList<>();
        while (myCursor.moveToNext()) {
            ids.add(myCursor.getInt(0));
            names.add(myCursor.getString(1));
        }
        myCursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIncomeCategory.setAdapter(adapter);
        spinnerIncomeCategory.setTag(ids);
    }
    private int getSelectedCategoryId() {  // Get the id of the selected category in the list
        java.util.ArrayList<Integer> ids = (java.util.ArrayList<Integer>) spinnerIncomeCategory.getTag();
        int pos = spinnerIncomeCategory.getSelectedItemPosition();
        return ids.get(pos);
    }
    private void selectSpinnerByCategoryId(int categoryId) {  // Select the category from the list with the id
        java.util.ArrayList<Integer> ids = (java.util.ArrayList<Integer>) spinnerIncomeCategory.getTag();
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == categoryId) {
                spinnerIncomeCategory.setSelection(i);
                break;
            }
        }
    }
    private void loadTransactions() {  // Load income transactions for the user
        linearLayoutIncomeTransactions.removeAllViews();
        Cursor myCursor = dataBaseHelper.getIncomeTransactionsByUser(currentUserEmail);
        String previousDate = "";
        while (myCursor.moveToNext()) {  // Keep loading until the last transaction
            final int id = myCursor.getInt(0);
            final double amount = myCursor.getDouble(1);
            final String date = myCursor.getString(2);
            final int categoryId = myCursor.getInt(3);
            final String categoryName = myCursor.getString(5);
            final String desc = myCursor.getString(4);
            if (!date.equals(previousDate)) {
                TextView header = new TextView(getContext());
                header.setText(date);
                header.setTypeface(null, android.graphics.Typeface.BOLD);
                header.setPadding(0, 16, 0, 4);
                linearLayoutIncomeTransactions.addView(header);
                previousDate = date;
            }
            LinearLayout row = new LinearLayout(getContext());  // Create a row that has the info of the transaction
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 6, 0, 6);
            TextView textViewInfo = new TextView(getContext());
            textViewInfo.setText("Category: "+categoryName + " - Amount: " + amount + "\n" +"Description: "+ desc);  // Text view with info of transaction
            textViewInfo.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            row.addView(textViewInfo);
            Button btnDelete = new Button(getContext());  // Delete button to delete the transaction
            btnDelete.setText("X");
            btnDelete.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  // When delete button is pressed
                    dataBaseHelper.deleteTransaction(id);  // Delete transaction from database
                    if (selectedTransactionId == id){  // If we are editing the deleted transaction, go back to adding a new one instead
                        clearSelection();
                    }
                    loadTransactions();
                }
            });
            row.addView(btnDelete);
            textViewInfo.setOnClickListener(new View.OnClickListener() {  // TextView is clickable to edit
                @Override
                public void onClick(View v) {  // When transaction is clicked, put its info in the edit form
                    selectedTransactionId = id;
                    editTextIncomeAmount.setText(String.valueOf(amount));
                    String[] parts = date.split("-"); // Format "YYYY-MM-DD"
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int day = Integer.parseInt(parts[2]);
                    datePickerIncome.updateDate(year, month, day);
                    if (desc==null){
                        editTextIncomeDescription.setText("");
                    }else{
                        editTextIncomeDescription.setText(desc);
                    }
                    selectSpinnerByCategoryId(categoryId);
                    btnAddIncome.setVisibility(View.GONE);  // Hide the add button
                    btnUpdateIncome.setVisibility(View.VISIBLE);  // Show the edit button
                    btnCancelEdit.setVisibility(View.VISIBLE);  // Show the cancel button
                }
            });
            linearLayoutIncomeTransactions.addView(row);  // Add the transaction row (Info + delete button)
        }
        myCursor.close();
    }
    private void addTransaction() {  // Adding a new transaction
        String amountStr = editTextIncomeAmount.getText().toString().trim();
        int day = datePickerIncome.getDayOfMonth();
        int month = datePickerIncome.getMonth() + 1; // months start at 0
        int year = datePickerIncome.getYear();
        String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        String desc = editTextIncomeDescription.getText().toString().trim();
        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(getContext(), "Amount and Date required", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        int categoryId = getSelectedCategoryId();
        dataBaseHelper.insertTransaction(currentUserEmail, "INCOME", amount, date, categoryId, desc);
        clearSelection();
        loadTransactions();
    }
    private void updateTransaction() {  // Update a transaction
        if (selectedTransactionId == -1){
            return;
        }
        String amountStr = editTextIncomeAmount.getText().toString().trim();
        int day = datePickerIncome.getDayOfMonth();
        int month = datePickerIncome.getMonth() + 1; // months start at 0
        int year = datePickerIncome.getYear();
        String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        String desc = editTextIncomeDescription.getText().toString().trim();
        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(getContext(), "Amount and Date required", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        int categoryId = getSelectedCategoryId();
        dataBaseHelper.editTransaction(selectedTransactionId, "INCOME", amount, date, categoryId, desc);
        clearSelection();
        loadTransactions();
    }
    private void clearSelection() {  // Goes back from edit to add mode
        selectedTransactionId = -1;  // Clear selected transaction and input forms
        editTextIncomeAmount.setText("");
        Calendar cal = Calendar.getInstance();
        datePickerIncome.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        editTextIncomeDescription.setText("");
        spinnerIncomeCategory.setSelection(0);
        btnAddIncome.setVisibility(View.VISIBLE);
        btnUpdateIncome.setVisibility(View.GONE);
        btnCancelEdit.setVisibility(View.GONE);
    }
}