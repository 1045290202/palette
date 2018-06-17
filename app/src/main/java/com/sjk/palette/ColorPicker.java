package com.sjk.palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends InkPresenter {
    private static Paint paint;
    private int lengthOfColorBlock = 50;
    private int diameterOfColorBlock = 40;
    private String[] colors = {
            "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#000000",
            "#FAFAFA"
    };
    private List<ColorPickerPoint> points = new ArrayList<>();
    private int startX, startY;
    private static boolean hasShadow = false;
    private static int shadowX, shadowY;
    private static Handler handle = new Handler();
    private static long startTime;
    private static int pressedType;
    private static int x, y;

    /**
     * 构造函数，设置画笔属性
     *
     * @param context
     * @param attrs
     */
    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 重写Canvas类的onDraw方法，画出不同色点的位置
     *
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int width = this.getWidth();
        int column = px2dp(width) / lengthOfColorBlock;
        int widthCount = 0;
        int heightCount = 0;
        int remainder = px2dp(width) % lengthOfColorBlock;
        for (int i = 0; i < colors.length; i++) {
            if (i % column == 0) {
                widthCount = lengthOfColorBlock / 2 + remainder / 2;
                if (i == 0) {
                    heightCount += lengthOfColorBlock / 2;
                } else {
                    heightCount += lengthOfColorBlock;
                }
            } else {
                widthCount += lengthOfColorBlock;
            }
            paint.setColor(Color.parseColor("#FF000000"));
            paint.setStrokeWidth(dp2px(diameterOfColorBlock + 5));
            canvas.drawPoint(dp2px(widthCount), dp2px(heightCount), paint);
            paint.setColor(Color.parseColor(colors[i]));
            paint.setStrokeWidth(dp2px(diameterOfColorBlock));
            canvas.drawPoint(dp2px(widthCount), dp2px(heightCount), paint);
            points.add(new ColorPickerPoint(colors[i],
                    widthCount - diameterOfColorBlock / 2, widthCount + diameterOfColorBlock / 2,
                    heightCount - diameterOfColorBlock / 2, heightCount + diameterOfColorBlock / 2, widthCount, heightCount));
        }
        if (hasShadow) {
            paint.setColor(Color.parseColor("#33000000"));
            paint.setStrokeWidth(dp2px(diameterOfColorBlock));
            canvas.drawPoint(dp2px(shadowX), dp2px(shadowY), paint);
        }
        setColorPickerHeight(heightCount + lengthOfColorBlock / 2);
    }

    /**
     * 为此类添加触摸事件,调用MainActivity的方法
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        pressedType = pressedType(startX, startY, (int) event.getX(), (int) event.getY(), startTime, System.currentTimeMillis(), 500);
        if (pressedType == 4) {
            handle.removeCallbacksAndMessages(null);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pressedType == 1) {
                for (ColorPickerPoint colorPickerPoint : points) {
                    if (colorPickerPoint.inPoint(px2dp(x), px2dp(y))) {
                        MainActivity.getMainActivity().setStrokeColor(colorPickerPoint.getColor().replace("#", "#FF"));
                        MainActivity.getMainActivity().imageHint(R.drawable.circle, colorPickerPoint.getColor().replace("#", "#FF"));
                        MainActivity.getMainActivity().dismissColorDialog();
                        break;
                    }
                }
            }
            hasShadow = false;
            handle.removeCallbacksAndMessages(null);
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (ColorPickerPoint colorPickerPoint : points) {
                        if (colorPickerPoint.inPoint(px2dp(x), px2dp(y))) {
                            InkPresenter.setBgColor(Color.parseColor(colorPickerPoint.getColor()));
                            hasShadow = false;
                            Toast.makeText(getContext(), "画板背景色设置成功", Toast.LENGTH_SHORT).show();
                            MainActivity.getMainActivity().dismissColorDialog();
                            break;
                        }
                    }
                }
            }, 500);
            startTime = System.currentTimeMillis();
            startX = x;
            startY = y;
            for (int i = 0; i < 21; i++) {
                if (points.get(i).inPoint(px2dp(x), px2dp(y))) {
                    shadowX = points.get(i).centerX;
                    shadowY = points.get(i).centerY;
                    hasShadow = true;
                    invalidate();
                    break;
                }
            }
        }
        return true;
    }

    /**
     * 触摸的类型
     *
     * @param startX
     * @param startY
     * @param thisX
     * @param thisY
     * @param lastDownTime
     * @param thisEventTime
     * @param longPressTime
     * @return
     */
    public static int pressedType(int startX, int startY, int thisX,
                                  int thisY, long lastDownTime, long thisEventTime,
                                  long longPressTime) {
        int offsetX = Math.abs(thisX - startX);
        int offsetY = Math.abs(thisY - startY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return 0;
        } else {
            if (offsetX <= 10 && offsetY <= 10) {
                if (!(intervalTime >= longPressTime)) {
                    return 1;
                }
                return 2;
            } else if (intervalTime >= longPressTime) {
                return 3;
            } else {
                return 4;
            }
        }
    }

    /**
     * 设置此Canvas的高度
     *
     * @param height 高度
     */
    public void setColorPickerHeight(int height) {
        ColorPicker colorPicker = findViewById(R.id.color_picker);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) colorPicker.getLayoutParams();
        layoutParams.height = dp2px(height);
        colorPicker.setLayoutParams(layoutParams);
    }

    class ColorPickerPoint {
        private String color;
        private int left, right, top, bottom, centerX, centerY;

        /**
         * 构造函数
         *
         * @param color  颜色字符串，以"#"开头的包含透明度的16进制字符串
         * @param left   单个色点左边位置
         * @param right  单个色点右边位置
         * @param top    单个色顶部位置
         * @param bottom 单个色点左边位置
         */
        public ColorPickerPoint(String color, int left, int right, int bottom, int top, int centerX, int centerY) {
            this.color = color;
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        /**
         * 判断触摸点是否在色点内，false表示不在点内，true表示在点内
         *
         * @param x 触摸点x坐标
         * @param y 触摸点y坐标
         * @return
         */
        public boolean inPoint(int x, int y) {
            if (x > right || x < left || y < bottom || y > top) {
                return false;
            } else {
                return true;
            }
        }

        /**
         * 返回色值字符串
         *
         * @return
         */
        public String getColor() {
            return color;
        }

        @Override
        public String toString() {
            return color;
        }
    }
}
