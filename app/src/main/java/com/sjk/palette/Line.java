package com.sjk.palette;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Line类包含路径、笔画粗细、颜色、笔触样式（新功能）
 */
public class Line {
    private Path path;
    private int width = 3;
    private int color = Color.BLACK;
    private InkPresenter.PaintMode paintMode = InkPresenter.PaintMode.ROUND;

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void setPaintMode(InkPresenter.PaintMode paintMode) {
        this.paintMode = paintMode;
    }

    public InkPresenter.PaintMode getPaintMode() {
        return paintMode;
    }

    public void setColor(int color) {//设置线条颜色
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

}
