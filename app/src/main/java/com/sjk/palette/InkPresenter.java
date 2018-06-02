package com.sjk.palette;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
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
    private float scale;
    private int widthPixels = 0;
    private int heightPixels = 0;
    private int linesCount = 0;
    private int restoreLinesCount = 0;
    private int needToRestore = 0;
    private int backgroundColor = 0xFFFAFAFA;
    private PaintMode paintMode = PaintMode.ROUND;
    private float preX, preY;

    enum PaintMode {
        ROUND, SQUARE;
    }

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
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
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

    public void createAlertDialog(final File file, final String filePath, final String fileFormat) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("提示");
        alertDialog.setMessage("此文件已存在，要覆盖它吗？");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "让我换个文件名", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.getMainActivity().createSaveDialog();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "算了把它替换吧", new DialogInterface.OnClickListener() {
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

    public void drawLines(Canvas canvas) {
        int linesSize = lines.size();
        if (linesSize != 0) {
            for (int j = 0; j < linesSize; j++) {//利用二重循环画出所有的线
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {//触摸事件
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                line.setWidth(MainActivity.getMainActivity().getStrokeWidthText());
                line.setColor(MainActivity.getMainActivity().getStrokeColorText());
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
//                line.addPointF(new PointF(event.getX(), event.getY()));
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
        invalidate();//重绘
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backgroundColor);
        drawLines(canvas);
    }

}