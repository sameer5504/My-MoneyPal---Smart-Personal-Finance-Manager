package com.example.a1220847_1222350_courseproject;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    RadioGroup radioGroupPeriod;
    RadioButton radioDay, radioWeek, radioMonth, radioCustom;

    Button btnIncomeVsExpenses;
    Button btnIncomeByCategory;
    Button btnExpensesByCategory;
    Button btnMonthlyIncomeExpenses;

    TextView tvTotalIncome;
    TextView tvTotalExpenses;
    TextView tvBalance;
    Button btnViewReport;

    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    String currentUserEmail;

    HomeData currentData;

    public static class HomeData {
        double totalIncome;
        double totalExpenses;
        List<DataBaseHelper.CategorySum> incomeByCategory;
        List<DataBaseHelper.CategorySum> expenseByCategory;
        List<DataBaseHelper.MonthlySum> monthlyExpenses;
        String Start;
        String End;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        dataBaseHelper = new DataBaseHelper(getActivity(), "USERS", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(getActivity());
        currentUserEmail = sharedPrefManager.getLoggedInUser();
        radioGroupPeriod = (RadioGroup) view.findViewById(R.id.radioGroupPeriod);
        radioDay = (RadioButton) view.findViewById(R.id.radioDay);
        radioWeek = (RadioButton) view.findViewById(R.id.radioWeek);
        radioMonth = (RadioButton) view.findViewById(R.id.radioMonth);
        radioCustom = (RadioButton) view.findViewById(R.id.radioCustom);
        btnIncomeVsExpenses = (Button) view.findViewById(R.id.buttonIncomeVsExpenses);
        btnIncomeByCategory = (Button) view.findViewById(R.id.buttonIncomeByCategory);
        btnExpensesByCategory = (Button) view.findViewById(R.id.buttonExpensesByCategory);
        btnMonthlyIncomeExpenses = (Button) view.findViewById(R.id.buttonMonthlyIncomeExpenses);
        tvTotalIncome = (TextView) view.findViewById(R.id.textViewTotalIncome);
        tvTotalExpenses = (TextView) view.findViewById(R.id.textViewTotalExpenses);
        tvBalance = (TextView) view.findViewById(R.id.textViewBalance);
        btnViewReport = (Button) view.findViewById(R.id.buttonViewReports);
        dataBaseHelper.ensureDefaultCategories(currentUserEmail);
        Cursor cursor = dataBaseHelper.getPreferencesForUser(currentUserEmail);
        cursor.moveToFirst();
        String defaultPeriod = cursor.getString(2);
        cursor.close();
        if (defaultPeriod.equalsIgnoreCase("DAY")) {
            radioDay.setChecked(true);
        } else if (defaultPeriod.equalsIgnoreCase("WEEK")) {
            radioWeek.setChecked(true);
        } else if (defaultPeriod.equalsIgnoreCase("MONTH")) {
            radioMonth.setChecked(true);
        } else if (defaultPeriod.equalsIgnoreCase("CUSTOM")) {
            radioCustom.setChecked(true);
            openCustomRange();
        }
        radioGroupPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String period = "DAY";
                if (checkedId == R.id.radioDay) {
                    period = "DAY";
                } else if (checkedId == R.id.radioWeek) {
                    period = "WEEK";
                } else if (checkedId == R.id.radioMonth) {
                    period = "MONTH";
                } else if (checkedId == R.id.radioCustom) {
                    openCustomRange();
                    return;
                }
                String[] range = computeRange(period);
                loadData(range[0], range[1]);
            }
        });

        btnIncomeVsExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(new IncomeVsExpensesFragment());
            }
        });

        btnIncomeByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(new IncomeByCategoryFragment());
            }
        });

        btnExpensesByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(new ExpensesByCategoryFragment());
            }
        });

        btnMonthlyIncomeExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(new MonthlyIncomeExpensesFragment());
            }
        });
        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new ReportFragment());
            }
        });
        String[] initialRange = computeRange(defaultPeriod);
        loadData(initialRange[0], initialRange[1]);
        return view;
    }

    private void openFragment(Fragment fragment) {
        if (currentData == null) {
            Toast.makeText(getActivity(), "Data not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fragment instanceof ExpensesByCategoryFragment) {
            ((ExpensesByCategoryFragment) fragment).setData(currentData);
        } else if (fragment instanceof IncomeByCategoryFragment) {
            ((IncomeByCategoryFragment) fragment).setData(currentData);
        } else if (fragment instanceof IncomeVsExpensesFragment) {
            ((IncomeVsExpensesFragment) fragment).setData(currentData);
        } else if (fragment instanceof MonthlyIncomeExpensesFragment) {
            ((MonthlyIncomeExpensesFragment) fragment).setData(currentData);
        }else if (fragment instanceof ReportFragment){
            ((ReportFragment) fragment).setData(currentData);
        }
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.graphContainer, fragment);
        ft.commit();
    }

    private void openCustomRange() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog startPicker = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
                        final Calendar start = Calendar.getInstance();
                        start.set(year, month, day, 23, 59, 59);
                        start.add(Calendar.DAY_OF_MONTH, -1);
                        DatePickerDialog endPicker = new DatePickerDialog(
                                getActivity(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(android.widget.DatePicker view, int y, int m, int d) {
                                        Calendar end = Calendar.getInstance();
                                        end.set(y, m, d, 0, 0, 1);
                                        end.add(Calendar.DAY_OF_MONTH, 1);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                        loadData(
                                                sdf.format(start.getTime()),
                                                sdf.format(end.getTime())
                                        );
                                    }
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                        endPicker.show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        startPicker.show();
    }
    private String[] computeRange(String option) {
        Calendar start = Calendar.getInstance();
        Calendar end   = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -1);
        start.set(Calendar.HOUR_OF_DAY, 23);
        start.set(Calendar.MINUTE, 59);
        start.set(Calendar.SECOND, 59);
        start.set(Calendar.MILLISECOND, 999);
        end.add(Calendar.DAY_OF_MONTH,1);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 1);
        if (option.equalsIgnoreCase("WEEK")) {
            start.add(Calendar.DAY_OF_MONTH, -7);
            start.set(Calendar.HOUR_OF_DAY, 23);
            start.set(Calendar.MINUTE, 59);
            start.set(Calendar.SECOND, 59);
            start.set(Calendar.MILLISECOND, 999);
        } else if (option.equalsIgnoreCase("MONTH")) {
            start.set(Calendar.DAY_OF_MONTH, 1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return new String[]{sdf.format(start.getTime()), sdf.format(end.getTime())};
    }

    private void loadData(String startDate, String endDate) {
        new AsyncTask<String, Void, HomeData>() {
            @Override
            protected HomeData doInBackground(String... params) {
                HomeData data = new HomeData();
                data.totalIncome = dataBaseHelper.getTotalAmount(currentUserEmail, "INCOME", params[0], params[1]);
                data.totalExpenses = dataBaseHelper.getTotalAmount(currentUserEmail, "EXPENSE", params[0], params[1]);
                data.incomeByCategory = dataBaseHelper.getCategorySums(currentUserEmail, "INCOME", params[0], params[1]);
                data.expenseByCategory = dataBaseHelper.getCategorySums(currentUserEmail, "EXPENSE", params[0], params[1]);
                data.monthlyExpenses = dataBaseHelper.getMonthlyExpenses(currentUserEmail, 6);
                data.Start=params[0];
                data.End=params[1];
                return data;
            }
            @Override
            protected void onPostExecute(HomeData data) {
                currentData = data;
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                tvTotalIncome.setText("Summary Income: " + nf.format(data.totalIncome));
                tvTotalExpenses.setText("Summary Expenses: " + nf.format(data.totalExpenses));
                tvBalance.setText("Total Balance: " + nf.format((dataBaseHelper.getAllTimeTotal(currentUserEmail,"INCOME"))-(dataBaseHelper.getAllTimeTotal(currentUserEmail,"EXPENSE"))));
            }
        }.execute(startDate, endDate);
    }
}