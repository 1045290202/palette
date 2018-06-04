package com.sjk.palette;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class PaintPreview extends InkPresenter {
    private static Paint paint;

    /**
     * 构造函数，定义画笔的属性
     * @param context
     * @param attrs
     */
    public PaintPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 重写onDraw方法
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        paint.setStrokeWidth(dp2px(MainActivity.getMainActivity().getStrokeWidthText()));
        paint.setColor(MainActivity.getMainActivity().getStrokeColorText());
        canvas.drawPoint(canvas.getWidth() / 2, canvas.getHeight() / 2, paint);
    }

    /**
     * 重绘
     */
    public void repaint() {
        invalidate();
    }
}
