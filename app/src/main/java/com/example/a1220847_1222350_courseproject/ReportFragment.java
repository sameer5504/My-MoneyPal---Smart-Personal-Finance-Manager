package com.example.a1220847_1222350_courseproject;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ReportFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
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

    private HomeFragment.HomeData data;

    public void setData(HomeFragment.HomeData data) {
        this.data = data;
    }

    LinearLayout linearLayoutReportTransactions;
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        dataBaseHelper = new DataBaseHelper(getActivity(), "USERS", null, 1);
        String currentUser = sharedPrefManager.getLoggedInUser();
        linearLayoutReportTransactions = root.findViewById(R.id.linearLayoutReportTransactions);
        linearLayoutReportTransactions.removeAllViews();
        Cursor myCursor = dataBaseHelper.getTransactionsByUser(currentUser, data.Start, data.End);
        String previousDate = "";
        while (myCursor.moveToNext()) {
            final double amount = myCursor.getDouble(1);
            final String date = myCursor.getString(2);
            final String desc = myCursor.getString(4);
            final String categoryName = myCursor.getString(5);
            final String type = myCursor.getString(6);
            if (!date.equals(previousDate)) {
                TextView header = new TextView(getContext());
                header.setText(date);
                header.setTypeface(null, android.graphics.Typeface.BOLD);
                header.setPadding(0, 16, 0, 8);
                header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                linearLayoutReportTransactions.addView(header);
                previousDate = date;
            }
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 6, 0, 6);
            LinearLayout column = new LinearLayout(getContext());
            column.setOrientation(LinearLayout.VERTICAL);
            column.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView textViewInfo = new TextView(getContext());
            textViewInfo.setText(type+"-"+categoryName);
            textViewInfo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            TextView textViewInfoAmount = new TextView(getContext());
            textViewInfoAmount.setText("Amount: " + amount);
            textViewInfoAmount.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            if (type.equalsIgnoreCase("INCOME")) {
                textViewInfoAmount.setTextColor(Color.GREEN);
            } else {
                textViewInfoAmount.setTextColor(Color.RED);
            }
            TextView textViewInfoDesc = new TextView(getContext());
            textViewInfoDesc.setText("Description: " + desc);
            textViewInfoDesc.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            column.addView(textViewInfo);
            column.addView(textViewInfoAmount);
            column.addView(textViewInfoDesc);
            row.addView(column);
            linearLayoutReportTransactions.addView(row);
            View divider = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4);
            params.setMargins(0, 8, 0, 8);
            divider.setLayoutParams(params);
            divider.setBackgroundColor(0xFF000000);
            linearLayoutReportTransactions.addView(divider);
        }
        myCursor.close();
        return root;
    }
}