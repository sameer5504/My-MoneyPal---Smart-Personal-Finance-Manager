package com.example.a1220847_1222350_courseproject;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

public class BudgetFragment extends Fragment {
    Spinner spinnerBudgetCategory;
    EditText editTextBudgetLimit;
    Button btnSetBudget;
    LinearLayout linearLayoutBudgets;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    String currentUserEmail;
    // Used to know whether we are editing an existing budget
    private int selectedCategoryId = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_budget, container, false);
        spinnerBudgetCategory = root.findViewById(R.id.spinnerBudgetCategory);
        editTextBudgetLimit = root.findViewById(R.id.editTextBudgetLimit);
        btnSetBudget = root.findViewById(R.id.btnSetBudget);
        linearLayoutBudgets = root.findViewById(R.id.linearLayoutBudgets);
        dataBaseHelper = new DataBaseHelper(getContext(), "USERS", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        currentUserEmail = sharedPrefManager.getLoggedInUser();
        loadExpenseCategories();
        btnSetBudget.setOnClickListener(v -> setBudget());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBudgets();
    }

    private void loadExpenseCategories() {
        Cursor c = dataBaseHelper.getExpenseCategoriesByUser(currentUserEmail);
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        while (c.moveToNext()) {
            ids.add(c.getInt(0));
            names.add(c.getString(1));
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetCategory.setAdapter(adapter);
        spinnerBudgetCategory.setTag(ids);
    }

    private int getSelectedCategoryId() {
        ArrayList<Integer> ids = (ArrayList<Integer>) spinnerBudgetCategory.getTag();
        return ids.get(spinnerBudgetCategory.getSelectedItemPosition());
    }

    private void setBudget() {
        String limitStr = editTextBudgetLimit.getText().toString().trim();
        if (limitStr.isEmpty()) {
            Toast.makeText(getContext(), "Enter budget limit", Toast.LENGTH_SHORT).show();
            return;
        }
        double limit = Double.parseDouble(limitStr);
        int categoryId;
        if (selectedCategoryId != -1) {
            categoryId = selectedCategoryId; // update existing
        } else {
            categoryId = getSelectedCategoryId(); // new
        }
        dataBaseHelper.updateInsertBudget(currentUserEmail, categoryId, limit);
        // reset UI
        editTextBudgetLimit.setText("");
        spinnerBudgetCategory.setSelection(0);
        selectedCategoryId = -1;
        btnSetBudget.setText("Add Budget");
        loadBudgets();
    }

    private void loadBudgets() {
        linearLayoutBudgets.removeAllViews();
        Cursor c = dataBaseHelper.getBudgetsWithCategoryNames(currentUserEmail);
        String currentMonth = getCurrentYearMonth();
        while (c.moveToNext()) {
            final int categoryId = c.getInt(1);
            final String categoryName = c.getString(2);
            final double limit = c.getDouble(3);
            double spent = dataBaseHelper.getMonthlySpentForCategory(currentUserEmail, categoryId, currentMonth);
            double remaining = limit - spent;
            int percent = dataBaseHelper.getBudgetProgressPercent(currentUserEmail, categoryId, limit, currentMonth);
            String alert = dataBaseHelper.getBudgetAlertStatus(currentUserEmail, categoryId, limit, currentMonth);
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            TextView tvInfo = new TextView(getContext());
            tvInfo.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            String alertText;
            if ("EXCEEDED".equals(alert)) {
                alertText = "Budget Exceeded";
                tvInfo.setTextColor(0xFFFF0000);
            } else if ("HALF_REACHED".equals(alert)) {
                alertText = "Budget Half reached";
                tvInfo.setTextColor(0xFFFFA500);
            } else {
                alertText = "Budget OK";
                tvInfo.setTextColor(0xFF2E8B57);
            }
            tvInfo.setText("Category: " + categoryName + "\nLimit: " + limit + " | Spent: " + spent + " | Remaining: " + remaining + "\nProgress: " + percent + "%  " + alertText);
            tvInfo.setOnClickListener(v -> {
                selectedCategoryId = categoryId;
                editTextBudgetLimit.setText(String.valueOf(limit));
                ArrayList<Integer> ids = (ArrayList<Integer>) spinnerBudgetCategory.getTag();
                for (int i = 0; i < ids.size(); i++) {
                    if (ids.get(i) == categoryId) {
                        spinnerBudgetCategory.setSelection(i);
                        break;
                    }
                }
                btnSetBudget.setText("Update Budget");
            });
            row.addView(tvInfo);
            linearLayoutBudgets.addView(row);
            View divider = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4);
            params.setMargins(0, 8, 0, 8);
            divider.setLayoutParams(params);
            divider.setBackgroundColor(0xFF000000);
            linearLayoutBudgets.addView(divider);
        }

        c.close();
    }

    private String getCurrentYearMonth() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        return year + "-" + String.format("%02d", month);
    }
}