package com.example.a1220847_1222350_courseproject;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;
public class IncomeVsExpensesFragment extends Fragment {
    private HomeFragment.HomeData data;
    public void setData(HomeFragment.HomeData data) {
        this.data = data;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        PieChart chart = view.findViewById(R.id.pieChart);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) data.totalIncome, "Income"));
        entries.add(new PieEntry((float) data.totalExpenses, "Expenses"));
        PieDataSet set = new PieDataSet(entries, "Income vs Expenses");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        chart.setData(new PieData(set));
        chart.invalidate();
        return view;
    }
}