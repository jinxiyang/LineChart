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
        mPoints.add(new ChartPoint(120));
        mPoints.add(new ChartPoint(132));
        mPoints.add(new ChartPoint(101));
        mPoints.add(new ChartPoint(134));
        mPoints.add(new ChartPoint(90));
        mPoints.add(new ChartPoint(230));
        mPoints.add(new ChartPoint(210));
        mPoints.add(new ChartPoint(134));
        mPoints.add(new ChartPoint(90));
        mPoints.add(new ChartPoint(230));

//        chart.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                chart.setData(mPoints);
//            }
//        }, 2000);

        chart.setData(mPoints);
    }
}
