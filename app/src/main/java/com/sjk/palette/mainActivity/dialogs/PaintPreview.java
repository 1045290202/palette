package com.sjk.palette.mainActivity.dialogs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.sjk.palette.mainActivity.InkPresenter;

public class PaintPreview extends InkPresenter {
    private static Paint paint;

    /**
     * 构造函数，定义画笔的属性
     *
     * @param context
     * @param attrs
     */
    public PaintPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.parseColor("#FF000000"));
        paint.setStrokeWidth(dp2px(PaintPreview.getStrokeWidth()));
    }

    /**
     * 重写onDraw方法
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPoint(canvas.getWidth() / 2, canvas.getHeight() / 2, paint);
    }

    /**
     * 重绘
     */
    public void repaint(int progress) {
        paint.setStrokeWidth(dp2px(progress));
        invalidate();
    }
}