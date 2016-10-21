package com.yang.chartlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2016/10/20.
 */

public class LineChart extends View {

    private int axisTextColor =  Color.rgb(205, 137, 118);
    private int axisTextSize = 15;

    //文字和坐标轴之间的间距
    private int yAxisGap = 10;
    private int xAxisGap = 10;

    private int xAxisColor = Color.rgb(205, 137, 118);
    private int xAxisWidth = 2;

    private int xAxisChildLineHeight = 5;

    private int dashedLineColor = Color.argb(155, 19, 113, 187);
    private int dashedLineWidth = 1;
    private int dashWidth = 6;
    private int dashGap = 4;

    private int shadowColor = Color.argb(155, 19, 113, 187);

    private int pointColor = Color.argb(155, 19, 113, 187);

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

    private int xItemNum = 10;
    private int yItemNum = 5;

    private Paint mPaint;

    private List<ChartPoint> mPoints;

    private List<String> xLabels;

    private List<String> yLabels;


    //0--1
    private float mProgress;

    //闪点的透明度、半径的百分比
    private int mFlashPointPercent;

    private CountDownTimer timer;


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
        mPaint = new Paint();
        mPoints = new ArrayList<>();
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

        xLabels = new ArrayList<>();
        xLabels.add("9/16");
        xLabels.add("9/17");
        xLabels.add("9/18");
        xLabels.add("9/19");
        xLabels.add("9/20");
        xLabels.add("9/21");
        xLabels.add("9/22");
        xLabels.add("9/23");
        xLabels.add("9/24");
        xLabels.add("9/25");


        yLabels = new ArrayList<>();
        yLabels.add("(万人) 0");
        yLabels.add("50");
        yLabels.add("100");
        yLabels.add("150");
        yLabels.add("200");
        yLabels.add("250");
    }

    //不作处理,因为模板可知宽高一定
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //计算每个轴上的单位长度所对应的px距离， 加 1 是给坐标轴文字占得空间
        xUnit = (mWidth - getPaddingLeft() - getPaddingRight()) / (xItemNum + 1);
        yUnit = (mHeight - getPaddingTop() - getPaddingBottom()) / (yItemNum + 1);

        //计算原点的坐标
        originX = getPaddingLeft() + xUnit;
        originY = getBottom() - getPaddingBottom() - yUnit;

        calculatePoint();

        drawXAxis(canvas);
        drawDashedLines(canvas);
        drawXAxisLabel(canvas);
        drawYAxisLabel(canvas);
        drawShadow(canvas);
        drawLine(canvas);
        drawChartPoints(canvas);

        mProgress = 1;
        if (mProgress != 1){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    postInvalidateDelayed(50);
                }
            }, 20);
        }
    }

    //更具数据计算左边
    private void calculatePoint() {
        int size = mPoints.size();
        for (int i = 0; i < size; i++) {
            ChartPoint point = mPoints.get(i);
            int x = (int) (originX + (i + 0.5) * xUnit);
            int y = (int) (originY - point.getyValue() * 1.0f / 50 * yUnit);
            point.setX(x);
            point.setY(y);
        }
    }

    //绘制x坐标轴
    private void drawXAxis(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(xAxisColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(xAxisWidth);
        canvas.drawLine(originX, originY, originX + xUnit * 10, originY, mPaint);

        int startX = (int) (originX + 0.5 * xUnit);
        for (int i = 0; i < xItemNum; i++){
            canvas.drawLine(startX + i * xUnit, originY, startX + i * xUnit, originY + xAxisChildLineHeight, mPaint);
        }
    }

    //绘制背景虚线
    private void drawDashedLines(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(dashedLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(dashedLineWidth);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{dashWidth, dashGap}, 0);
        mPaint.setPathEffect(dashPathEffect);

        int startY = originY - yUnit;
        Path path = new Path();
        for (int i = 0; i < yItemNum; i++){
            //drawline不支持画虚线，故用drawPath
            path.reset();
            path.moveTo(originX, startY - i * yUnit);
            path.lineTo(originX + xUnit * 10, startY - i * yUnit);
            canvas.drawPath(path, mPaint);
        }
    }

    //绘制圆点
    private void drawChartPoints(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(pointColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        int size = mPoints.size();
        for (int i = 0; i < size; i++){
            ChartPoint point = mPoints.get(i);
            int x = (int) (originX + (i + 0.5) * xUnit);
            int y = (int) (originY - point.getyValue() * 1.0f/50 * yUnit);
            point.setX(x);
            point.setY(y);
            canvas.drawCircle(x, y, 5, mPaint);
        }
    }

    //绘制阴影
    private void drawShadow(Canvas canvas) {
        mPaint.reset();
        Path path = new Path();
        mPaint.setColor(Color.argb(100, 177, 234, 253));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        int size = mPoints.size();
        for (int i = 0; i < size; i++) {
            ChartPoint point = mPoints.get(i);
            int x = (int) (originX + (i + 0.5) * xUnit);
            int y = (int) (originY - point.getyValue() * 1.0f / 50 * yUnit);
            point.setX(x);
            point.setY(y);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.lineTo((float) (originX + (0.5 + xItemNum - 1) * xUnit), originY);
        path.lineTo((float) (originX + 0.5 * xUnit), originY);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    //绘制连线
    private void drawLine(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(Color.rgb(0, 220, 255));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        Path path = new Path();
        int size = mPoints.size();
        for (int i = 0; i < size; i++) {
            ChartPoint point = mPoints.get(i);
            int x = (int) (originX + (i + 0.5) * xUnit);
            int y = (int) (originY - point.getyValue() * 1.0f / 50 * yUnit);
            point.setX(x);
            point.setY(y);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, mPaint);
    }

    //绘制y轴坐标文字
    private void drawYAxisLabel(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(axisTextColor);
        mPaint.setTextSize(axisTextSize);
        mPaint.setAntiAlias(true);
        Rect textRect = new Rect();
        for (int i = 0; i <= yItemNum; i++){
            String label = yLabels.get(i);
            if (TextUtils.isEmpty(label)){
                break;
            }
            mPaint.getTextBounds(label, 0, label.length(), textRect);
            int x = originX - yAxisGap- textRect.width();
            int y = originY - i * yUnit + textRect.height()/2;
            canvas.drawText(label, x, y, mPaint);
        }
    }

    //绘制x轴坐标文字
    private void drawXAxisLabel(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(axisTextColor);
        mPaint.setTextSize(axisTextSize);
        mPaint.setAntiAlias(true);
        Rect textRect = new Rect();
        for (int i = 0; i < xItemNum; i++){
            String label = xLabels.get(i);
            if (TextUtils.isEmpty(label)){
                break;
            }
            mPaint.getTextBounds(label, 0, label.length(), textRect);
            int x = (int) (originX + (i + 0.5) * xUnit - textRect.width()/2);
            int y = originY + xAxisChildLineHeight + xAxisGap + textRect.height();
            canvas.drawText(label, x, y, mPaint);
        }
    }


    public void setData(List<ChartPoint> points){
//        mPoints.app
        //TODO 处理数据
        startChartAnim();
    }

    private void startChartAnim() {
        mProgress = 0;
    }




}
