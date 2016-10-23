package com.yang.chartlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2016/10/20.
 */

public class LineChart extends View {

    //坐标轴文字的颜色
    private int axisTextColor =  Color.rgb(205, 137, 118);

    //标轴文字的大小
    private int axisTextSize = 15;

    //文字和x坐标轴之间的间距
    private int yAxisGap = 10;

    //文字和y坐标轴之间的间距
    private int xAxisGap = 10;

    //x坐标轴的颜色
    private int xAxisColor = Color.rgb(205, 137, 118);

    //x坐标轴的宽度
    private int xAxisWidth = 2;

    //x坐标轴下面小竖线的高度
    private int xAxisChildLineHeight = 5;

    //虚线的颜色
    private int dashedLineColor = Color.argb(155, 19, 113, 187);

    //虚线的宽度
    private int dashedLineWidth = 1;

    //虚线中每段实线的宽度
    private int dashWidth = 6;

    //虚线中实线间的间隔
    private int dashGap = 4;

    //阴影的颜色
    private int shadowColor = Color.argb(100, 177, 234, 253);

    //点的颜色
    private int pointColor = Color.argb(155, 19, 113, 187);

    //点之间连线的颜色
    private int lineColor = Color.rgb(0, 220, 255);

    //点之间连线的宽度
    private int lineWidth = 5;

    //悬浮maker标记的字体颜色
    private int makerTextColor;

    //悬浮maker标记的字体大小
    private int makerTextSize;

    //悬浮maker标记的背景颜色
    private int makerBackgroundColor;

    //悬浮maker标旁边竖线的颜色
    private int makerLineColor;

    //悬浮maker标旁边竖线的宽度
    private int makerLineWidth;


    //图标的宽度
    private int mWidth;

    //图标的高度
    private int mHeight;

    //x坐标轴单位长度所代表的像素
    private int xUnit;

    //y坐标轴单位长度所代表的像素
    private int yUnit;

    //原点在chart中的x值
    private int originX;

    //原点在chart中的y值
    private int originY;

    //x轴默认显示几项
    private int xItemNum = 10;

    //y轴默认显示几项
    private int yItemNum = 5;

    private Paint mPaint;

    private List<ChartPoint> mPoints;

    private List<String> xLabels;

    private List<String> yLabels;

    //默认的动画脉冲间隔
    private static final long DEFAULT_INTERVAL_TIME = 20;

    //动画脉冲间隔
    private long intervalTime = DEFAULT_INTERVAL_TIME;

    //每次移动进度
    private float intervalProgress = 0.02f;

    //0-10,动画执行时在x轴上的进度
    private float mProgress = 0;

    //连线前进时头的坐标
    private ChartPoint lineHeadPoint;

    //标识动画是否正在执行
    private boolean isAniming = true;



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
        lineHeadPoint = new ChartPoint();
        mPoints = new ArrayList<>();
        calculatePoint();
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

        if (mPoints.isEmpty()){
            isAniming = false;
            mProgress = 0;
            return;
        }
        drawLine(canvas);
        drawShadow(canvas);
        drawChartPoints(canvas);

        if (isAniming){
            mProgress += intervalProgress;
            if (mProgress >= mPoints.size()) isAniming = false;
            postInvalidateDelayed(intervalTime);
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
        int ceil = (int) Math.ceil(mProgress);
        if (ceil == 0){
            return;
        }
        int size = mPoints.size();
        if (ceil >= size){
            ceil = size;
        }
        mPaint.reset();
        mPaint.setColor(pointColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        for (int i = 0; i < ceil - 1; i++){
            ChartPoint p = mPoints.get(i);
            canvas.drawCircle(p.getX(), p.getY(), 5, mPaint);
        }
        if (size > 0){
            drawFlashPoint(canvas, ceil);
        }
    }

    //绘制闪烁的点
    private void drawFlashPoint(Canvas canvas, int ceil) {
        ChartPoint flashP = mPoints.get(ceil - 1);
        double flashParam = Math.abs(Math.cos(Math.PI * (mProgress - Math.floor(mProgress)) * 5 / 2));
        if (isAniming){
            canvas.drawCircle(flashP.getX(), flashP.getY(), (float) (ChartPoint.MAX_RADIUS * flashParam), mPaint);
        }else {
            canvas.drawCircle(flashP.getX(), flashP.getY(), 5, mPaint);
        }
    }

    //绘制阴影
    private void drawShadow(Canvas canvas) {
        int floor = (int) Math.floor(mProgress);
        if (floor == 0 || mPoints.size() < 2){
            return;
        }
        mPaint.reset();
        mPaint.setColor(shadowColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        Path path = new Path();
        ChartPoint firstP = mPoints.get(0);
        path.moveTo(firstP.getX(), firstP.getY());
        for (int i = 1; i < floor; i++) {
            ChartPoint p = mPoints.get(i);
            path.lineTo(p.getX(), p.getY());
        }
        path.lineTo(lineHeadPoint.getX(), lineHeadPoint.getY());
        path.lineTo(lineHeadPoint.getX(), originY);
        path.lineTo((float) (originX + 0.5 * xUnit), originY);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    //绘制连线
    private void drawLine(Canvas canvas) {
        int floor = (int) Math.floor(mProgress);
        int size = mPoints.size();
        if (floor == 0){
            return;
        }else if (floor == size){
            lineHeadPoint = mPoints.get(size - 1);
        }else {
            int x = (int) ((mProgress - 0.5) * xUnit + originX);
            ChartPoint floorP = mPoints.get(floor - 1);
            ChartPoint ceilP = mPoints.get(floor);
            int y = (int) ((ceilP.getY() - floorP.getY()) * 1.0 /(ceilP.getX() - floorP.getX()) * (x -  floorP.getX()) + floorP.getY());
            lineHeadPoint.setX(x);
            lineHeadPoint.setY(y);
        }

        mPaint.reset();
        mPaint.setColor(lineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(lineWidth);
        Path path = new Path();
        ChartPoint firstP = mPoints.get(0);
        path.moveTo(firstP.getX(), firstP.getY());
        for (int i = 1; i < floor; i++) {
            ChartPoint p = mPoints.get(i);
            path.lineTo(p.getX(), p.getY());
        }
        path.lineTo(lineHeadPoint.getX(), lineHeadPoint.getY());
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
        mPoints = points;
        calculatePoint();
        isAniming = true;
        mProgress = 0;
        postInvalidate();
    }


    public void setAxisTextColor(int axisTextColor) {
        this.axisTextColor = axisTextColor;
    }

    public void setAxisTextSize(int axisTextSize) {
        this.axisTextSize = axisTextSize;
    }

    public void setyAxisGap(int yAxisGap) {
        this.yAxisGap = yAxisGap;
    }

    public void setxAxisGap(int xAxisGap) {
        this.xAxisGap = xAxisGap;
    }

    public void setxAxisColor(int xAxisColor) {
        this.xAxisColor = xAxisColor;
    }

    public void setxAxisWidth(int xAxisWidth) {
        this.xAxisWidth = xAxisWidth;
    }

    public void setxAxisChildLineHeight(int xAxisChildLineHeight) {
        this.xAxisChildLineHeight = xAxisChildLineHeight;
    }

    public void setDashedLineColor(int dashedLineColor) {
        this.dashedLineColor = dashedLineColor;
    }

    public void setDashedLineWidth(int dashedLineWidth) {
        this.dashedLineWidth = dashedLineWidth;
    }

    public void setDashWidth(int dashWidth) {
        this.dashWidth = dashWidth;
    }

    public void setDashGap(int dashGap) {
        this.dashGap = dashGap;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setMakerTextColor(int makerTextColor) {
        this.makerTextColor = makerTextColor;
    }

    public void setMakerTextSize(int makerTextSize) {
        this.makerTextSize = makerTextSize;
    }

    public void setMakerBackgroundColor(int makerBackgroundColor) {
        this.makerBackgroundColor = makerBackgroundColor;
    }

    public void setMakerLineColor(int makerLineColor) {
        this.makerLineColor = makerLineColor;
    }

    public void setMakerLineWidth(int makerLineWidth) {
        this.makerLineWidth = makerLineWidth;
    }
}
