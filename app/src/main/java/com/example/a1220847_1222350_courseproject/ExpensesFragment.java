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
 * Use the {@link ExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpensesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    public static ExpensesFragment newInstance(String param1, String param2) {
        ExpensesFragment fragment = new ExpensesFragment();
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

    EditText editTextExpenseAmount;
    DatePicker datePickerExpense;
    Spinner spinnerExpenseCategory;
    EditText editTextExpenseDescription;
    Button btnAddExpense;
    Button btnUpdateExpense;
    Button btnCancelEdit;
    LinearLayout linearLayoutExpenseTransactions;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    private int selectedTransactionId = -1;
    String currentUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expenses, container, false);
        dataBaseHelper = new DataBaseHelper(getContext(), "USERS", null, 1);
        editTextExpenseAmount = root.findViewById(R.id.editTextExpenseAmount);
        datePickerExpense = root.findViewById(R.id.datePickerExpense);
        spinnerExpenseCategory = root.findViewById(R.id.spinnerExpenseCategory);
        editTextExpenseDescription = root.findViewById(R.id.editTextExpenseDescription);
        btnAddExpense = root.findViewById(R.id.btnAddExpense);
        btnUpdateExpense = root.findViewById(R.id.btnUpdateExpense);
        btnCancelEdit = root.findViewById(R.id.btnCancelEdit);
        linearLayoutExpenseTransactions = root.findViewById(R.id.linearLayoutExpenseTransactions);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        currentUserEmail = sharedPrefManager.getLoggedInUser();
        dataBaseHelper.ensureDefaultCategories(currentUserEmail);
        loadCategories();
        loadTransactions();
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
            }
        });
        btnUpdateExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTransaction();
            }
        });
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelection();
            }
        });
        return root;
    }
    private void loadCategories() {
        Cursor myCursor = dataBaseHelper.getExpenseCategoriesByUser(currentUserEmail);
        java.util.ArrayList<String> names = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> ids = new java.util.ArrayList<>();
        while (myCursor.moveToNext()) {
            ids.add(myCursor.getInt(0));
            names.add(myCursor.getString(1));
        }
        myCursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenseCategory.setAdapter(adapter);
        spinnerExpenseCategory.setTag(ids);
    }
    private int getSelectedCategoryId() {
        java.util.ArrayList<Integer> ids = (java.util.ArrayList<Integer>) spinnerExpenseCategory.getTag();
        int pos = spinnerExpenseCategory.getSelectedItemPosition();
        return ids.get(pos);
    }
    private void selectSpinnerByCategoryId(int categoryId) {
        java.util.ArrayList<Integer> ids = (java.util.ArrayList<Integer>) spinnerExpenseCategory.getTag();

        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == categoryId) {
                spinnerExpenseCategory.setSelection(i);
                break;
            }
        }
    }
    private void loadTransactions() {
        linearLayoutExpenseTransactions.removeAllViews();
        Cursor myCursor = dataBaseHelper.getExpenseTransactionsByUser(currentUserEmail);
        String previousDate = "";
        while (myCursor.moveToNext()) {

            final int id = myCursor.getInt(0);
            final double amount = myCursor.getDouble(1);
            final String date = myCursor.getString(2);
            final int categoryId = myCursor.getInt(3);
            final String desc = myCursor.getString(4);
            final String categoryName = myCursor.getString(5);
            if (!date.equals(previousDate)) {
                TextView header = new TextView(getContext());
                header.setText(date);
                header.setTypeface(null, android.graphics.Typeface.BOLD);
                header.setPadding(0, 16, 0, 4);
                linearLayoutExpenseTransactions.addView(header);
                previousDate = date;
            }

            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 6, 0, 6);
            TextView textViewInfo = new TextView(getContext());
            textViewInfo.setText("Category: " + categoryName + " - Amount: " + amount + "\nDescription: " + desc);
            textViewInfo.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(textViewInfo);
            Button btnDelete = new Button(getContext());
            btnDelete.setText("X");
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataBaseHelper.deleteTransaction(id);
                    if (selectedTransactionId == id) {
                        clearSelection();
                    }
                    loadTransactions();
                }
            });

            row.addView(btnDelete);
            textViewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTransactionId = id;
                    editTextExpenseAmount.setText(String.valueOf(amount));
                    String[] parts = date.split("-");
                    datePickerExpense.updateDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));

                    if (desc==null){
                        editTextExpenseDescription.setText("");
                    }else{
                        editTextExpenseDescription.setText(desc);
                    }
                    selectSpinnerByCategoryId(categoryId);
                    btnAddExpense.setVisibility(View.GONE);
                    btnUpdateExpense.setVisibility(View.VISIBLE);
                    btnCancelEdit.setVisibility(View.VISIBLE);
                }
            });
            linearLayoutExpenseTransactions.addView(row);
        }
        myCursor.close();
    }

    private void addTransaction() {

        String amountStr = editTextExpenseAmount.getText().toString().trim();
        int day = datePickerExpense.getDayOfMonth();
        int month = datePickerExpense.getMonth() + 1;
        int year = datePickerExpense.getYear();
        String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        String desc = editTextExpenseDescription.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Amount required", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int categoryId = getSelectedCategoryId();
        dataBaseHelper.insertTransaction(currentUserEmail, "EXPENSE", amount, date, categoryId, desc);
        clearSelection();
        loadTransactions();
    }

    private void updateTransaction() {
        if (selectedTransactionId == -1) return;
        String amountStr = editTextExpenseAmount.getText().toString().trim();
        int day = datePickerExpense.getDayOfMonth();
        int month = datePickerExpense.getMonth() + 1;
        int year = datePickerExpense.getYear();
        String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        String desc = editTextExpenseDescription.getText().toString().trim();
        double amount = Double.parseDouble(amountStr);
        int categoryId = getSelectedCategoryId();
        dataBaseHelper.editTransaction(selectedTransactionId, "EXPENSE", amount, date, categoryId, desc);
        clearSelection();
        loadTransactions();
    }

    private void clearSelection() {

        selectedTransactionId = -1;
        editTextExpenseAmount.setText("");
        Calendar cal = Calendar.getInstance();
        datePickerExpense.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        editTextExpenseDescription.setText("");
        spinnerExpenseCategory.setSelection(0);
        btnAddExpense.setVisibility(View.VISIBLE);
        btnUpdateExpense.setVisibility(View.GONE);
        btnCancelEdit.setVisibility(View.GONE);
    }
}
