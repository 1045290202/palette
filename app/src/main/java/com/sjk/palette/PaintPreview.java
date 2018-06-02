package com.sjk.palette;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class PaintPreview extends InkPresenter {
    private static Paint paint;

    public PaintPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setStrokeWidth(dp2px(MainActivity.getMainActivity().getStrokeWidthText()));
        paint.setColor(MainActivity.getMainActivity().getStrokeColorText());
        canvas.drawPoint(canvas.getWidth() / 2, canvas.getHeight() / 2, paint);
    }

    public void repaint() {
        invalidate();
    }
}
