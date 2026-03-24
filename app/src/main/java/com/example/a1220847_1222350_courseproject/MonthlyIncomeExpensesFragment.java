package com.example.a1220847_1222350_courseproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;

public class MonthlyIncomeExpensesFragment extends Fragment {
    private HomeFragment.HomeData data;
    public void setData(HomeFragment.HomeData data) {
        this.data = data;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        BarChart chart = view.findViewById(R.id.barChart);
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.monthlyExpenses.size(); i++) {
            entries.add(new BarEntry(i, (float) data.monthlyExpenses.get(i).total));
        }
        BarDataSet set = new BarDataSet(entries, "Monthly Expenses");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(set);
        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getLabels(data.monthlyExpenses)));
        chart.getXAxis().setGranularity(1f);
        chart.invalidate();
        return view;
    }

    private List<String> getLabels(List<DataBaseHelper.MonthlySum> list) {
        List<String> labels = new ArrayList<>();
        for (DataBaseHelper.MonthlySum m : list) labels.add(m.label);
        return labels;
    }
}