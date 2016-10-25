package com.yang.linechart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yang.chartlibrary.ChartPoint;
import com.yang.chartlibrary.LineChart;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LineChart chart = (LineChart) findViewById(R.id.linechart);
        final ArrayList<ChartPoint> mPoints = new ArrayList<>();
        mPoints.add(new ChartPoint(120, "9/16"));
        mPoints.add(new ChartPoint(132, "9/17"));
        mPoints.add(new ChartPoint(101, "9/18"));
        mPoints.add(new ChartPoint(134, "9/19"));
        mPoints.add(new ChartPoint(90, "9/20"));
        mPoints.add(new ChartPoint(230, "9/21"));
        mPoints.add(new ChartPoint(210, "9/22"));
        mPoints.add(new ChartPoint(134, "9/23"));
        mPoints.add(new ChartPoint(90, "9/24"));
        mPoints.add(new ChartPoint(230, "9/27"));

//        chart.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                chart.setData(mPoints);
//            }
//        }, 5000);

        chart.setData(mPoints);
    }
}
