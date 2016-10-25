package com.yang.chartlibrary;

/**
 * Created by yangjinxi on 2016/10/21.
 */

public class ChartPoint {
    //点的x坐标,会由图表自动计算
    private int x;

    //点的y坐标,会由图表自动计算
    private int y;

    //用户提供的y值
    private int yValue;

    //该点对应的x轴的文字
    private String xLabel;


    public ChartPoint(int yValue, String xLabel) {
        this.yValue = yValue;
        this.xLabel = xLabel;
    }

    public ChartPoint() {
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

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }
}
