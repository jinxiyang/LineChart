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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 折线图表，附带星星闪烁动画
 *
 * 注：图表中所有的字体大小、线的宽度都是dp单位，在绘制时会将dp转为px，sp转为px
 * Created by yang on 2016/10/20.
 *
 */

public class LineChart extends View {
    //默认的动画脉冲间隔
    private static final long DEFAULT_INTERVAL_TIME = 20;

    //默认x轴最大显示几项
    private static final int DEFAULT_X_MAX_ITEM_NUM = 10;

    //默认y轴最大显示几格
    private static final int DEFAULT_Y_MAX_ITEM_NUM = 5;

    //默认点的最大半径,dp
    private static final int DEFAULT_MAX_POINT_RADIUS = 6;

    //坐标轴文字的颜色
    private int axisTextColor =  Color.rgb(205, 137, 118);

    //标轴文字的大小,sp
    private int axisTextSize = 14;

    //文字和x坐标轴之间的间距,dp
    private int yAxisGap = 3;

    //文字和y坐标轴之间的间距,dp
    private int xAxisGap = 3;

    //x坐标轴的颜色
    private int xAxisColor = Color.rgb(205, 137, 118);

    //x坐标轴的宽度,dp
    private int xAxisWidth = 2;

    //x坐标轴下面小竖线的高度,dp
    private int xAxisChildLineHeight = 5;

    //虚线的颜色
    private int dashedLineColor = Color.argb(155, 19, 113, 187);

    //虚线的宽度,dp
    private int dashedLineWidth = 1;

    //虚线中每段实线的宽度,dp
    private int dashWidth = 5;

    //虚线中实线间的间隔,dp
    private int dashGap = 3;

    //阴影的颜色
    private int shadowColor = Color.argb(100, 177, 234, 253);

    //点的颜色
    private int pointColor = Color.argb(155, 19, 113, 187);

    //点之间连线的颜色
    private int lineColor = Color.rgb(0, 220, 255);

    //点之间连线的宽度,dp
    private int lineWidth = 2;

    //悬浮maker标记的字体颜色
    private int makerTextColor;

    //悬浮maker标记的字体大小,dp
    private int makerTextSize;

    //悬浮maker标记的背景颜色
    private int makerBackgroundColor;

    //悬浮maker标旁边竖线的颜色
    private int makerLineColor;

    //悬浮maker标旁边竖线的宽度,dp
    private int makerLineWidth;


    //图标的宽度,px
    private int mWidth;

    //图标的高度,px
    private int mHeight;

    //x坐标轴单位长度所代表的像素,px
    private int xUnit;

    //y坐标轴单位长度所代表的像素,px
    private int yUnit;

    //点闪烁时,最大的半径
    private int maxPointRadius = DEFAULT_MAX_POINT_RADIUS;

    //点正常时的半径
    private int pointRadius = DEFAULT_MAX_POINT_RADIUS / 2;

    //y轴每单位长度,所代表的值
    private int yUnitValue = 50;

    //y轴的单位
    private String yUnitName = "万人";

    //原点在chart中的x值,px
    private int originX;

    //原点在chart中的y值,px
    private int originY;

    private Paint mPaint;

    private List<ChartPoint> mPoints;

    private List<String> xLabels;

    private List<String> yLabels;

    //x轴默认显示几项
    private int xItemNum = DEFAULT_X_MAX_ITEM_NUM;

    //y轴默认显示几项
    private int yItemNum = DEFAULT_Y_MAX_ITEM_NUM;


    //动画脉冲间隔
    private long intervalTime = DEFAULT_INTERVAL_TIME;

    //每次移动进度
    private float intervalProgress = 0.06f;

    //0-10,动画执行时在x轴上的进度
    private float mProgress = 0;

    //连线前进时头的坐标
    private ChartPoint lineHeadPoint;

    //标识动画是否正在执行
    private boolean isAniming = true;

    //是否需要计算点
    private boolean needCalculatePoint = true;

    private DisplayMetrics mDm;


    //标志是否有点被点击选中
    private boolean pointIsSelected = false;

    //被点击选中的点在mPoints中的id
    private int selectedPointId = -1;

    //点被点击选中时，标记提示框显示的时间, millis
    private long showMakerTime = 5000;

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
        xLabels = new ArrayList<>();
        yLabels = new ArrayList<>();
        mPoints = new ArrayList<>();
        mDm = getContext().getResources().getDisplayMetrics();
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
        needCalculatePoint = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (needCalculatePoint){
            calculatePoint();
        }

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

        if (pointIsSelected){
            drawMakerLine(canvas);
            drawMaker(canvas);
        }

        if (isAniming){
            mProgress += intervalProgress;
            if (mProgress >= mPoints.size()) isAniming = false;
            postInvalidateDelayed(intervalTime);
        }
    }

    //根据数据计算坐标
    private void calculatePoint() {
        //计算每个轴上的单位长度所对应的px距离， 加 1 是给坐标轴文字占得空间
        xUnit = (mWidth - getPaddingLeft() - getPaddingRight()) / (xItemNum + 1);
        yUnit = (mHeight - getPaddingTop() - getPaddingBottom()) / (yItemNum + 1);

        //计算原点的坐标
        originX = getPaddingLeft() + xUnit;
        originY = getBottom() - getPaddingBottom() - yUnit;

        xLabels.clear();
        int size = mPoints.size();
        for (int i = 0; i < size; i++) {
            ChartPoint point = mPoints.get(i);
            //x轴的第一个点距离y轴0.5个单位长度
            int x = (int) (originX + (i + 0.5) * xUnit);
            int y = (int) (originY - point.getyValue() * 1.0f / yUnitValue * yUnit);
            point.setX(x);
            point.setY(y);
            xLabels.add(point.getxLabel());
        }

        yLabels.clear();
        yLabels.add("(" + yUnitName + ")" + 0);
        for (int i = 1; i <= yItemNum; i++){
            yLabels.add(yUnitValue * i + "");
        }

        needCalculatePoint = true;
    }

    //绘制x坐标轴
    private void drawXAxis(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(xAxisColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dpToPx(mDm, xAxisWidth));
        canvas.drawLine(originX, originY, originX + xUnit * xItemNum, originY, mPaint);

        int startX = (int) (originX + 0.5 * xUnit);
        int childLineHeight = dpToPx(mDm, xAxisChildLineHeight);
        for (int i = 0; i < xItemNum; i++){
            canvas.drawLine(startX + i * xUnit, originY, startX + i * xUnit, originY + childLineHeight, mPaint);
        }
    }

    //绘制背景虚线
    private void drawDashedLines(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(dashedLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(dpToPx(mDm, dashedLineWidth));
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{dpToPx(mDm, dashWidth), dpToPx(mDm, dashGap)}, 0);
        mPaint.setPathEffect(dashPathEffect);

        int startY = originY - yUnit;
        Path path = new Path();
        for (int i = 0; i < yItemNum; i++){
            //drawline不支持画虚线，故用drawPath
            path.reset();
            path.moveTo(originX, startY - i * yUnit);
            path.lineTo(originX + xUnit * xItemNum, startY - i * yUnit);
            canvas.drawPath(path, mPaint);
        }
    }

    //绘制圆点
    private void drawChartPoints(Canvas canvas) {
        int ceil = (int) Math.ceil(mProgress);
        if (ceil == 0 || mPoints.isEmpty()){
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
        int radius = dpToPx(mDm, pointRadius);

        for (int i = 0; i < ceil - 1; i++){//不绘制闪烁点,不绘制最后一个点
            ChartPoint p = mPoints.get(i);
            canvas.drawCircle(p.getX(), p.getY(), radius, mPaint);
        }
        drawFlashPoint(canvas, ceil);
    }

    //绘制闪烁的点
    private void drawFlashPoint(Canvas canvas, int ceil) {
        ChartPoint flashP = mPoints.get(ceil - 1);
        if (isAniming){//绘制闪烁点
            //函数y = |cos(pi * (mProgress - ceil) * 5/2)|       动画实现的关键
            double flashParam = Math.abs(Math.cos(Math.PI * (mProgress - Math.floor(mProgress)) * 5 / 2));
            canvas.drawCircle(flashP.getX(), flashP.getY(), (float) (maxPointRadius * flashParam), mPaint);
        }else {//绘制最后一个点
            canvas.drawCircle(flashP.getX(), flashP.getY(), dpToPx(mDm, pointRadius), mPaint);
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
        if (floor == 0 || mPoints.isEmpty()){
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
        mPaint.setStrokeWidth(dpToPx(mDm, lineWidth));
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
        mPaint.setTextSize(spToPx(mDm, axisTextSize));
        mPaint.setAntiAlias(true);
        Rect textRect = new Rect();
        int size = yLabels.size();
        for (int i = 0; i < size; i++){
            String label = yLabels.get(i);
            if (TextUtils.isEmpty(label)){
                break;
            }
            mPaint.getTextBounds(label, 0, label.length(), textRect);
            int x = originX - dpToPx(mDm, yAxisGap) - textRect.width();
            int y = originY - i * yUnit + textRect.height()/2;
            canvas.drawText(label, x, y, mPaint);
        }
    }

    //绘制x轴坐标文字
    private void drawXAxisLabel(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(axisTextColor);
        mPaint.setTextSize(spToPx(mDm, axisTextSize));
        mPaint.setAntiAlias(true);
        Rect textRect = new Rect();
        int childLineHeight = dpToPx(mDm, xAxisChildLineHeight);
        int gap = dpToPx(mDm, xAxisGap);
        int size = xLabels.size();
        for (int i = 0; i < size; i++){
            String label = xLabels.get(i);
            if (TextUtils.isEmpty(label)){
                break;
            }
            mPaint.getTextBounds(label, 0, label.length(), textRect);
            int x = (int) (originX + (i + 0.5) * xUnit - textRect.width()/2);
            int y = originY + childLineHeight + gap + textRect.height();
            canvas.drawText(label, x, y, mPaint);
        }
    }

    private void drawMakerLine(Canvas canvas) {

    }

    private void drawMaker(Canvas canvas) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (isAniming){
                    return true;
                }
                selectedPointId = findPointIdNearbyLocation(event.getX(), event.getY());
//                Log.i("======", "selectedPointId: " + selectedPointId);
                if (selectedPointId != -1){
                    pointIsSelected = true;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (pointIsSelected){
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pointIsSelected = false;
                            selectedPointId = -1;
                            postInvalidate();
                        }
                    }, showMakerTime);
                }
                break;
        }
        return true;
    }

    //在所给位置附近找到最近的图表中的点, 范围0-size    -1代表没找到
    private int findPointIdNearbyLocation(float x, float y) {
        if (mPoints.isEmpty() || x < originX || x > originX + xItemNum * xUnit){
            return -1;
        }
        double id = (x - originX) / xUnit - 0.5;
        int floor = (int) Math.floor(id);
        int ceil = (int) Math.ceil(id);

        if (floor >= 0 && floor < mPoints.size()){
            ChartPoint p = mPoints.get(floor);
            double interval = Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2) - 20 * 20;
            if (interval < 0){
                return floor;
            }
        }

        if (ceil >= 0 && ceil < mPoints.size()){
            ChartPoint p = mPoints.get(ceil);
            double interval = Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2) - 20 * 20;
            if (interval < 0){
                return ceil;
            }
        }
        return -1;

    }

    public void setData(List<ChartPoint> points){
        mPoints = points;
        isAniming = true;
        mProgress = 0;
        needCalculatePoint = true;
        pointIsSelected = false;
        selectedPointId = -1;
        postInvalidate();
    }

    //dp转为px
    public int dpToPx(DisplayMetrics dm, int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    //sp转为px
    public int spToPx(DisplayMetrics dm, int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, dm);
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


    public void setyUnitName(String yUnitName) {
        this.yUnitName = yUnitName;
    }

    public void setyUnitValue(int yUnitValue) {
        this.yUnitValue = yUnitValue;
    }

    public void setPointRadius(int pointRadius) {
        this.pointRadius = pointRadius;
    }

    public void setMaxPointRadius(int maxPointRadius) {
        this.maxPointRadius = maxPointRadius;
    }

    public void setxItemNum(int xItemNum) {
        this.xItemNum = xItemNum;
    }

    public void setyItemNum(int yItemNum) {
        this.yItemNum = yItemNum;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public void setIntervalProgress(float intervalProgress) {
        this.intervalProgress = intervalProgress;
    }

    public void setShowMakerTime(long showMakerTime) {
        this.showMakerTime = showMakerTime;
    }
}
