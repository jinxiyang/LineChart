package com.yang.chartlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yang on 2016/10/20.
 */

public class LineChart extends View {

    private int axisTextColor;
    private int axisTextSize;
    private int axisColor;
    private int axisWidth;

    private int dashedLineColor;
    private int dashedLineWidth;

    private int shadowColor;
    private float shadowAlpha;

    private int dotColor;

    private int lineColor;
    private int lineWidth;


    private int makerTextColor;
    private int makerTextSize;
    private int makerBackgroundColor;


    private int makerLineColor;
    private int makerLineWidth;

    private long animTime;


    private int mWidth;
    private int mHeight;

    private int xUnit;
    private int yUnit;

    private int originX;
    private int originY;



    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    //不作处理,因为模板可知宽和高一定
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        xUnit = w/12;
        yUnit = h/7;

        originX = (int) (xUnit * 1.5);
        originY = (int) (yUnit * 1.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setData(){

    }
}
