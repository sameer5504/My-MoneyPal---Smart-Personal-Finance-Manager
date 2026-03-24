package com.example.a1220847_1222350_courseproject;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    EditText editTextCategoryName;
    RadioGroup radioGroupCategoryType;
    RadioButton radioCategoryIncome;
    RadioButton radioCategoryExpense;
    Button btnAddCategory;
    Button btnUpdateCategory;
    Button btnCancelCategory;
    LinearLayout incomeCategoryContainer;
    LinearLayout expenseCategoryContainer;
    CheckBox checkBoxDark;
    RadioGroup radioGroupPeriod;
    RadioButton radioPeriodDay;
    RadioButton radioPeriodWeek;
    RadioButton radioPeriodMonth;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    String currentUserEmail;
    int selectedCategoryId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        dataBaseHelper = new DataBaseHelper(getContext(), "USERS", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        currentUserEmail = sharedPrefManager.getLoggedInUser();
        editTextCategoryName = root.findViewById(R.id.editTextCategoryName);
        radioGroupCategoryType = root.findViewById(R.id.radioGroupCategoryType);
        radioCategoryIncome = root.findViewById(R.id.radioCategoryIncome);
        radioCategoryExpense = root.findViewById(R.id.radioCategoryExpense);
        btnAddCategory = root.findViewById(R.id.buttonAddCategory);
        btnUpdateCategory = root.findViewById(R.id.btnUpdateCategory);
        btnCancelCategory = root.findViewById(R.id.btnCancelCategory);
        incomeCategoryContainer = root.findViewById(R.id.incomeCategoryContainer);
        expenseCategoryContainer = root.findViewById(R.id.expenseCategoryContainer);
        checkBoxDark = root.findViewById(R.id.checkBoxDark);
        radioGroupPeriod = root.findViewById(R.id.radioGroupPeriod);
        radioPeriodDay = root.findViewById(R.id.radioPeriodDay);
        radioPeriodWeek = root.findViewById(R.id.radioPeriodWeek);
        radioPeriodMonth = root.findViewById(R.id.radioPeriodMonth);
        btnAddCategory.setVisibility(View.VISIBLE);
        btnUpdateCategory.setVisibility(View.GONE);
        btnCancelCategory.setVisibility(View.GONE);
        loadPreferences();
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
        btnUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory();
            }
        });
        btnCancelCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearSelection();
            }
        });
        checkBoxDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDark = checkBoxDark.isChecked();
                String mode;
                if (isDark) {
                    mode = "DARK";
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(getContext(), "Default mode updated!", Toast.LENGTH_SHORT).show();
                } else {
                    mode = "LIGHT";
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(getContext(), "Default mode updated!", Toast.LENGTH_SHORT).show();
                }
                dataBaseHelper.setDarkMode(currentUserEmail, mode);
            }
        });
        radioGroupPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String periodValue;
                if (checkedId == R.id.radioPeriodDay) {
                    periodValue = "DAY";
                    Toast.makeText(getContext(), "Default summary period updated!", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radioPeriodWeek) {
                    periodValue = "WEEK";
                    Toast.makeText(getContext(), "Default summary period updated!", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radioPeriodMonth) {
                    periodValue = "MONTH";
                    Toast.makeText(getContext(), "Default summary period updated!", Toast.LENGTH_SHORT).show();
                } else {
                    return;
                }
                dataBaseHelper.setPeriod(currentUserEmail, periodValue);
            }
        });

        loadCategories();
        return root;
    }

    private void loadPreferences() {
        Cursor c = dataBaseHelper.getPreferencesForUser(currentUserEmail);
        if (c != null) {
            if (c.moveToFirst()) {
                String mode = c.getString(1);
                String period = c.getString(2);
                if (mode != null && mode.equalsIgnoreCase("DARK")) {
                    checkBoxDark.setChecked(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    checkBoxDark.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                if (period != null) {
                    if (period.equalsIgnoreCase("DAY")) {
                        radioPeriodDay.setChecked(true);
                    } else if (period.equalsIgnoreCase("WEEK")) {
                        radioPeriodWeek.setChecked(true);
                    } else {
                        radioPeriodMonth.setChecked(true);
                    }
                }
            }
            c.close();
        }
    }

    private void addCategory() {
        String name = editTextCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Category name required", Toast.LENGTH_SHORT).show();
            return;
        }
        String type;
        if (radioGroupCategoryType.getCheckedRadioButtonId() == R.id.radioCategoryIncome) {
            type = "INCOME";
        } else {
            type = "EXPENSE";
        }
        dataBaseHelper.insertCategory(currentUserEmail, name, type);
        clearSelection();
        loadCategories();
    }

    private void updateCategory() {
        if (selectedCategoryId == -1) {
            Toast.makeText(getContext(), "No category selected", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = editTextCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Category name required", Toast.LENGTH_SHORT).show();
            return;
        }
        String type;
        if (radioGroupCategoryType.getCheckedRadioButtonId() == R.id.radioCategoryIncome) {
            type = "INCOME";
        } else {
            type = "EXPENSE";
        }
        dataBaseHelper.updateCategory(selectedCategoryId, name, type);
        clearSelection();
        loadCategories();
    }

    private void clearSelection() {
        selectedCategoryId = -1;
        editTextCategoryName.setText("");
        radioCategoryExpense.setChecked(true);
        btnAddCategory.setVisibility(View.VISIBLE);
        btnUpdateCategory.setVisibility(View.GONE);
        btnCancelCategory.setVisibility(View.GONE);
    }

    private void loadCategories() {
        incomeCategoryContainer.removeAllViews();
        expenseCategoryContainer.removeAllViews();
        loadCategoriesByType("INCOME", incomeCategoryContainer);
        loadCategoriesByType("EXPENSE", expenseCategoryContainer);
    }

    private void loadCategoriesByType(String type, LinearLayout container) {
        Cursor myCursor = null;
        if (type.equals("INCOME")) {
            myCursor = dataBaseHelper.getIncomeCategoriesByUser(currentUserEmail);
        } else if (type.equals("EXPENSE")) {
            myCursor = dataBaseHelper.getExpenseCategoriesByUser(currentUserEmail);
        }
        if (myCursor == null) {
            return;
        }
        if (myCursor.moveToFirst()) {
            do {
                int id = myCursor.getInt(0);
                String name = myCursor.getString(1);
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 8, 0, 8);
                TextView tv = new TextView(getContext());
                tv.setText(name);
                tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                Button btnDel = new Button(getContext());
                btnDel.setText("X");
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCategoryId = id;
                        editTextCategoryName.setText(name);
                        if (type.equals("INCOME")) {
                            radioCategoryIncome.setChecked(true);
                        } else {
                            radioCategoryExpense.setChecked(true);
                        }
                        btnAddCategory.setVisibility(View.GONE);
                        btnUpdateCategory.setVisibility(View.VISIBLE);
                        btnCancelCategory.setVisibility(View.VISIBLE);
                    }
                });

                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataBaseHelper.deleteCategory(id);
                        Toast.makeText(getContext(), "Category Deleted!", Toast.LENGTH_SHORT).show();
                        if (selectedCategoryId == id) {
                            clearSelection();
                        }
                        loadCategories();
                    }
                });
                row.addView(tv);
                row.addView(btnDel);
                container.addView(row);
            } while (myCursor.moveToNext());
        }
        myCursor.close();
    }
}