package com.sjk.palette;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InkPresenter extends View {
    private static Paint paint;
    private Bitmap cacheBitmap;
    private Canvas cacheCanvas;
    private Path path = new Path();
    private Line line = new Line();
    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Line> restoreLines = new ArrayList<>();
    private static float scale;
    private int widthPixels = 0;
    private int heightPixels = 0;
    private int linesCount = 0;
    private int restoreLinesCount = 0;
    private int needToRestore = 0;
    private int backgroundColor = 0xFFFAFAFA;
    private PaintMode paintMode = PaintMode.ROUND;
    private float preX, preY;
    private static String strokeColor = "#FF000000";
    private static int strokeWidth = 5;

    /**
     * 新功能
     * 枚举不同样式的画笔风格
     */
    enum PaintMode {
        ROUND, SQUARE;
    }

    /**
     * 构造函数，定义画笔属性
     *
     * @param context
     * @param attrs
     */
    public InkPresenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        scale = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
    }

    public static void setStrokeColor(String strokeColor) {
        InkPresenter.strokeColor = strokeColor;
    }

    public static String getStrokeColor() {
        return strokeColor;
    }

    public static void setStrokeWidth(int strokeWidth) {
        InkPresenter.strokeWidth = strokeWidth;
    }

    public static int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 清空画板
     */
    public void clear() {
        restoreLinesCount += linesCount;
        needToRestore = linesCount;
        lines.clear();
        linesCount = 0;
        restoreLines.clear();
        restoreLinesCount = 0;
        invalidate();
    }

    /**
     * 撤销
     */
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

    /**
     * 还原
     */
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

    /**
     * 获取画布宽度
     */
    public void getCanvasWidth() {
        widthPixels = this.getWidth();
    }

    /**
     * 获取画布高度
     */
    public void getCanvasHeight() {
        heightPixels = this.getHeight();
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return
     */
    public static int px2dp(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return
     */
    public static int dp2px(float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 使保存的图片在立即被媒体扫描，在图库中显示
     *
     * @param context
     * @param path
     */
    public static void galleryAddPic(Context context, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 利用文件流将文件保存到sdcard中
     *
     * @param fileName   文件名
     * @param fileFormat 文件格式
     * @throws IOException
     */
    public void saveBitmap(String fileName, String fileFormat) throws IOException {
        String folderPath = "/sdcard/Pictures/palette/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                String filePath = folderPath + fileName + fileFormat;
                File file = new File(filePath);
                if (!file.exists()) {
                    //文件未存在
                    if (file.createNewFile()) {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        if (fileFormat.equals(".png")) {
                            cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        } else {
                            cacheBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        galleryAddPic(getContext(), filePath);
                        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //文件已存在
                    createAlertDialog(file, filePath, fileFormat);
                }
            }
        } else {
            String filePath = folderPath + fileName + fileFormat;
            File file = new File(filePath);
            if (!file.exists()) {
                //文件未存在
                if (file.createNewFile()) {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    if (fileFormat.equals(".png")) {
                        cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    } else {
                        cacheBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    galleryAddPic(getContext(), filePath);
                    Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                //文件已存在
                createAlertDialog(file, filePath, fileFormat);
            }
        }
    }

    /**
     * 将绘制的信息复制到新的画布中，并调用saveBitmap方法
     *
     * @param fileName
     * @param fileFormat
     */
    public void save(String fileName, String fileFormat) {
        getCanvasWidth();
        getCanvasHeight();
        cacheBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas();
        cacheCanvas.setBitmap(cacheBitmap);
        cacheCanvas.drawColor(backgroundColor);
        drawLines(cacheCanvas);
        cacheCanvas.drawBitmap(cacheBitmap, 0, 0, paint);
        try {
            saveBitmap(fileName, fileFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出提示框
     *
     * @param file       文件
     * @param filePath   文件路径
     * @param fileFormat 文件后缀名
     */
    public void createAlertDialog(final File file, final String filePath, final String fileFormat) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("提示");
        alertDialog.setMessage("此文件已存在，要覆盖它吗？");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "让我再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.getMainActivity().createSaveDialog();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "快点替换它", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    if (fileFormat.equals(".png")) {
                        cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    } else {
                        cacheBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    galleryAddPic(getContext(), filePath);
                    Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    /**
     * 画出所有的线
     *
     * @param canvas
     */
    public void drawLines(Canvas canvas) {
        int linesSize = lines.size();
        if (linesSize != 0) {
            for (int j = 0; j < linesSize; j++) {
                Line line = lines.get(j);
                paint.setStrokeWidth(dp2px((float) line.getWidth()));
                paint.setColor(line.getColor());
                if (line.getPaintMode() == PaintMode.ROUND) {
                    paint.setStrokeCap(Paint.Cap.ROUND);
                } else if (line.getPaintMode() == PaintMode.SQUARE) {
                    paint.setStrokeCap(Paint.Cap.SQUARE);
                }
                canvas.drawPath(line.getPath(), paint);
            }
        }
    }

    /**
     * 画布的触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                line.setWidth(strokeWidth);
                line.setColor(Color.parseColor(strokeColor));
                if (paintMode == PaintMode.ROUND) {
                    line.setPaintMode(PaintMode.ROUND);
                } else if (paintMode == PaintMode.SQUARE) {
                    line.setPaintMode(PaintMode.SQUARE);
                }
                line.setPath(path);
                lines.add(line);
                linesCount++;
                if (restoreLinesCount != 0) {
                    restoreLines.clear();
                    restoreLinesCount = 0;
                }
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - preX) > 0 && Math.abs(y - preY) > 0) {
                    line.getPath().quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
                    lines.set(linesCount - 1, line);
                    preX = x;
                    preY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                path = new Path();
                line = new Line();
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(backgroundColor);
        drawLines(canvas);
    }

}