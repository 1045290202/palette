package com.sjk.palette;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InkPresenter extends View {
    private static Paint paint = null;
    private Bitmap cacheBitmap = null;
    private Canvas cacheCanvas = null;
    private Canvas canvas;
    private Path path = new Path();
    private Line line = new Line();
    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Line> restoreLines = new ArrayList<>();
    private float scale;
    private int widthPixels = 0;
    private int heightPixels = 0;
    private int linesCount = 0;
    private int restoreLinesCount = 0;
    private int needToRestore = 0;
    private int backgroundColor = 0xFFFAFAFA;
    private static int paintMode = 1;
    float x, y;

    public void clear() {
        restoreLinesCount += linesCount;
        needToRestore = linesCount;
        lines.clear();
        linesCount = 0;
        restoreLines.clear();
        restoreLinesCount = 0;
        invalidate();
    }

    public void revoke() {
        if (linesCount > 0) {
            Line line = lines.get(linesCount - 1);
            restoreLines.add(line);
            restoreLinesCount++;
            lines.remove(linesCount - 1);
            linesCount--;
            invalidate();
        }
    }

    public void restore() {
        if (restoreLinesCount > 0) {
            Line line = restoreLines.get(restoreLinesCount - 1);
            lines.add(line);
            linesCount++;
            restoreLines.remove(restoreLinesCount - 1);
            restoreLinesCount--;
            invalidate();
        }
    }

    public InkPresenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        scale = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    public InkPresenter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void getCanvasWidth() {
        widthPixels = this.getWidth();
    }

    public void getCanvasHeight() {
        heightPixels = this.getHeight();
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    public int dp2px(float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }

    public static void galleryAddPic(Context context, String path) {//在图库中显示
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public void saveBitmap(String fileName) throws IOException {
        String filePath = "/sdcard/Pictures/palette" + fileName + ".png";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        } else {

        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        galleryAddPic(getContext(), filePath);
    }

    public void drawLines(Canvas canvas) {
        int linesSize = lines.size();
        if (linesSize != 0) {
            for (int j = 0; j < linesSize; j++) {//利用二重循环画出所有的线
                Line line = lines.get(j);
                if (line.getLineSize() > 1) {
                    for (int i = 0, lineSize = line.getLineSize(); i < lineSize - 1; i++) {
                        paint.setStrokeWidth(dp2px((float) line.getWidth()));
                        paint.setColor(line.getColor());
                        if (line.getPaintMode() == 1) {
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
                            canvas.drawLine(line.getPointF(i).x, line.getPointF(i).y,
                                    line.getPointF(i + 1).x, line.getPointF(i + 1).y, paint);
                        } else if (line.getPaintMode() == 0) {
                            canvas.drawLine(line.getPointF(i).x, line.getPointF(i).y,
                                    line.getPointF(i + 1).x, line.getPointF(i + 1).y, paint);
                        }
                    }
                } else if (line.getLineSize() == 1) {
                    paint.setStrokeWidth(dp2px((float) line.getWidth()));
                    paint.setColor(line.getColor());
                    canvas.drawPoint(line.getPointF(0).x, line.getPointF(0).y, paint);
                }
            }
        }
    }

    public void save(String fileName) {
        getCanvasWidth();
        getCanvasHeight();
        cacheBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas();
        cacheCanvas.setBitmap(cacheBitmap);
        cacheCanvas.drawColor(backgroundColor);
        drawLines(cacheCanvas);
        cacheCanvas.drawBitmap(cacheBitmap, 0, 0, paint);
        try {
            saveBitmap(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//触摸事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //按下时往lines二维动态数组里添加一个新的line动态数组，然后让线条数+1
            line.setWidth(MainActivity.getMainActivity().getStrokeWidthText());
            if (paintMode == 1) {
                line.setPaintMode(1);
                line.setColor(MainActivity.getMainActivity().getStrokeColorText());
            } else if (paintMode == 0) {
                line.setPaintMode(0);
                line.setColor(backgroundColor);
            }
            lines.add(line);
            linesCount++;
            if (restoreLinesCount != 0) {
                restoreLines.clear();
                restoreLinesCount = 0;
            }
            x = event.getX();
            y = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //手指滑动时往line动态数组中添加新的触摸点信息并更新lines二维动态数组第linesCount-1个元素
            if (Math.abs(event.getX() - x) > 1 && Math.abs(event.getY() - y) > 1) {
                line.addPointF(new PointF(event.getX(), event.getY()));
                lines.set(linesCount - 1, line);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //手指离开屏幕时让line管理一个新创建的ArrayList
            line = new Line();
        }
        invalidate();//重绘
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        canvas.drawColor(backgroundColor);
        drawLines(canvas);
    }

}