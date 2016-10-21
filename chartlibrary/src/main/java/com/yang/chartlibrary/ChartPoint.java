package com.yang.chartlibrary;

/**
 * Created by yangjinxi on 2016/10/21.
 */

public class ChartPoint {

    public static final int MIN_RADIUS = 1;
    public static final int MAX_RADIUS = 5;
    public static final int NORMAL_RADIUS = 3;

    private int x;
    private int y;
    private int radius;

    private int yValue;

    public ChartPoint(int yValue) {
        this.yValue = yValue;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }
}
