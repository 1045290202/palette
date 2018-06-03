package com.sjk.palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends InkPresenter {
    private static Paint paint;
    private int lengthOfColorBlock = 50;
    private int diameterOfColorBlock = 40;
    private String[] colors = {
            "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#000000"
    };
    private ColorPickerPoint[] colorPickerPoints = new ColorPickerPoint[colors.length];
    private List<ColorPickerPoint> points = new ArrayList<>();

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(dp2px(diameterOfColorBlock));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(0xff333333);
        int width = this.getWidth();
        int column = px2dp(width) / lengthOfColorBlock;
        int row = colors.length / column;
        if (colors.length % column > 0) {
            row += 1;
        }
        int remainder = px2dp(width) % lengthOfColorBlock;
        int widthCount = 0;
        int heightCount = 0;
        for (int j = 0; j < row; j++) {
            widthCount = 0;
            if (j > 0) {
                heightCount += lengthOfColorBlock;
            } else {
                heightCount += lengthOfColorBlock / 2;
            }
            if (j < row - 1) {
                for (int i = 0; i < column; i++) {
                    paint.setColor(Color.parseColor(colors[i + j * column]));
                    if (i > 0) {
                        widthCount += lengthOfColorBlock;
                    } else {
                        widthCount += lengthOfColorBlock / 2 + remainder / 2;
                    }
                    canvas.drawPoint(dp2px(widthCount), dp2px(heightCount), paint);
                    points.add(new ColorPickerPoint(colors[i + j * column],
                            widthCount - diameterOfColorBlock / 2, widthCount + diameterOfColorBlock / 2,
                            heightCount - diameterOfColorBlock / 2, heightCount + diameterOfColorBlock / 2));
                }
            } else {
                for (int i = 0; i < colors.length % column; i++) {
                    paint.setColor(Color.parseColor(colors[i + j * column]));
                    if (i > 0) {
                        widthCount += lengthOfColorBlock;
                    } else {
                        widthCount += lengthOfColorBlock / 2 + remainder / 2;
                    }
                    canvas.drawPoint(dp2px(widthCount), dp2px(heightCount), paint);
                    points.add(new ColorPickerPoint(colors[i + j * column],
                            widthCount - diameterOfColorBlock / 2, widthCount + diameterOfColorBlock / 2,
                            heightCount - diameterOfColorBlock / 2, heightCount + diameterOfColorBlock / 2));
                }
            }
        }
        setColorPickerHeight(heightCount + lengthOfColorBlock / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            for (ColorPickerPoint colorPickerPoint : points) {
                if (colorPickerPoint.inPoint(px2dp(x), px2dp(y))) {
                    MainActivity.getMainActivity().setStrokeColorText(colorPickerPoint.getColor().replace("#", "#FF"));
                    MainActivity.getMainActivity().dismissColorDialog();
                    break;
                }
            }
        }
        return true;
    }

    public void setColorPickerHeight(int height) {
        ColorPicker colorPicker = findViewById(R.id.color_picker);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) colorPicker.getLayoutParams();
        layoutParams.height = dp2px(height);
        colorPicker.setLayoutParams(layoutParams);
    }

    class ColorPickerPoint {
        private String color;
        private int left, right, top, bottom;

        public ColorPickerPoint(String color, int left, int right, int top, int bottom) {
            this.color = color;
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        public boolean inPoint(int x, int y) {
            if (x > right || x < left || y < top || y > bottom) {
                return false;
            } else {
                return true;
            }
        }

        public String getColor() {
            return color;
        }
    }
}
