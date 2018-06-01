package com.sjk.palette;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;

public class Line {
    private Path path = new Path();
    private ArrayList<PointF> line = new ArrayList<>();
    private int width = 0;
    private int color = Color.BLACK;
    private int paintMode = 1;

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void setPaintMode(int paintMode) {
        this.paintMode = paintMode;
    }

    public int getPaintMode() {
        return paintMode;
    }

    public void setColor(int color) {//设置线条颜色
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void addPointF(PointF pointF) {//往line中添加触摸点
        line.add(pointF);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int getLineSize() {
        return line.size();
    }

    public PointF getPointF(int index) {
        return line.get(index);
    }

}
