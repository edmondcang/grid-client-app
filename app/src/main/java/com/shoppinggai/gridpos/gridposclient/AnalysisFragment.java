package com.shoppinggai.gridpos.gridposclient;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by admin0 on 4/25/16.
 */
public class AnalysisFragment extends Fragment {

    public static final String KEY_ITEM = "unique_key";
    public static final String KEY_INDEX = "index_key";
    private String mTime;

    Context context;
    ActionBar actionBar;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("time_key", mTime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        actionBar = ((AppCompatActivity) context).getSupportActionBar();

        actionBar.setTitle(getString(R.string.data_analysis));

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.analysis_fragment, container, false);

//        layout.setBackgroundColor(Color.argb(100, 200, 200, 255));

//        YAxis leftAxis = chart.getAxisLeft();

//        LimitLine ll = new LimitLine(140f, "Highest Point");
//        ll.setLineColor(Color.RED);
//        ll.setLineWidth(4f);
//        ll.setTextColor(Color.GRAY);
//        ll.setTextSize(12f);

//        leftAxis.addLimitLine(ll);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(4f, 0));
        entries.add(new BarEntry(8f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(12f, 3));
        entries.add(new BarEntry(18f, 4));
        entries.add(new BarEntry(9f, 5));

        BarDataSet dataSet = new BarDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

//        BarChart chart = new BarChart(layout.getContext());
        BarChart chart = (BarChart) layout.findViewById(R.id.chart);

        BarData data = new BarData(labels, dataSet);
        chart.setData(data);
        chart.invalidate();

//        layout.removeAllViews();
        chart.setVisibility(View.VISIBLE);

        layout.findViewById(R.id.imageView2).setVisibility(View.GONE);

//        layout.addView(chart);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

//        ((AppCompatActivity) context).findViewById(R.id.pager).setVisibility(View.GONE);
//        ((AppCompatActivity) context).findViewById(R.id.tabs).setVisibility(View.GONE);
//        ((AppCompatActivity) context).findViewById(R.id.pager2).setVisibility(View.GONE);
//        ((AppCompatActivity) context).findViewById(R.id.tabs2).setVisibility(View.GONE);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

//                Log.d("keyEvent", String.valueOf(keyEvent));

                if (keyCode == keyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                    getFragmentManager().popBackStack();

                    return true;
                }
                return false;
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        actionBar.setTitle(getString(R.string.data_analysis));
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        actionBar.setTitle(getString(R.string.my_grids));
//    }
}
