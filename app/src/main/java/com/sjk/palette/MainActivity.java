package com.sjk.palette;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {
    private static InkPresenter inkPresenter;
    private static MainActivity mainActivity;
    private ColorDialog colorDialog;
    private BrushDialog brushDialog;
    private MenuDialog menuDialog;
    private long firstTime = 0;
    final static int REQUEST_WRITE = 1;

    /**
     * 将已在内存中的MainActivity赋值给mainActivity
     */
    public MainActivity() {
        mainActivity = this;
    }

    /**
     * 获取mainActivity
     *
     * @return
     */
    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    /**
     * 重写权限申请的回调函数
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(getApplicationContext(), "啊，没有得到存储权限，我死了", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 活动被创建时调用
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.getMainActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE);
            }
        }
        inkPresenter = findViewById(R.id.inkPresenter);
        titleOnTouchListener();
        toolOnClickListener();
        imageHint(R.drawable.circle, InkPresenter.getStrokeColor());
    }

    /**
     * 图片着色并应用
     *
     * @param drawableID 图片的ID
     * @param color      色值字符串
     */
    public void imageHint(int drawableID, String color) {
        Drawable originBitmapDrawable = ContextCompat.getDrawable(this, drawableID);
        Drawable wrappedDrawable = DrawableCompat.wrap(originBitmapDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(color));
        ImageView imageView = findViewById(R.id.circle);
        imageView.setImageDrawable(wrappedDrawable);
    }

    /**
     * 获取笔触大小
     *
     * @return
     */
    public int getStrokeWidth() {
//        TextView strokeWidthText = findViewById(R.id.strokeWidth);
//        return Integer.parseInt(strokeWidthText.getText().toString());
        return InkPresenter.getStrokeWidth();
    }

    /**
     * 设置笔触大小
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(int strokeWidth) {
        InkPresenter.setStrokeWidth(strokeWidth);
    }

    /**
     * 获取笔触颜色
     *
     * @return
     */
    public int getStrokeColor() {
//        TextView strokeColorText = findViewById(R.id.strokeColor);
//        return Color.parseColor(strokeColorText.getText().toString());
        return Color.parseColor(InkPresenter.getStrokeColor());
    }

    /**
     * 设置笔触颜色
     *
     * @param str
     */
    public void setStrokeColor(String str) {
//        TextView textView = findViewById(R.id.strokeColor);
//        textView.setText(str);
        InkPresenter.setStrokeColor(str);
    }

    /**
     * 返回画布
     *
     * @return
     */
    public InkPresenter getInkPresenter() {
        return inkPresenter;
    }

    /**
     * 弹出保存提示框
     */
    public void createSaveDialog() {
        SaveDialog saveDialog = new SaveDialog(getApplicationContext());
        saveDialog.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
        params[0].alpha = 0.7f;
        getWindow().setAttributes(params[0]);
        saveDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params[0] = getWindow().getAttributes();
                params[0].alpha = 1f;
                getWindow().setAttributes(params[0]);
            }
        });
    }

    public void createMenuDialog() {
        menuDialog = new MenuDialog(getApplicationContext());
        menuDialog.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
        params[0].alpha = 0.7f;
        getWindow().setAttributes(params[0]);
        menuDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params[0] = getWindow().getAttributes();
                params[0].alpha = 1f;
                getWindow().setAttributes(params[0]);
            }
        });
    }

    /**
     * title的点击事件
     */
    private void titleOnTouchListener() {
        ImageView save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSaveDialog();
            }
        });

        ImageView clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inkPresenter.clear();
            }
        });

        ImageView revoke = findViewById(R.id.revoke);
        revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inkPresenter.revoke();
            }
        });

        ImageView restore = findViewById(R.id.restore);
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inkPresenter.restore();
            }
        });

        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenuDialog();
            }
        });
    }

    /**
     * 创建toolBar的点击事件
     */
    public void toolOnClickListener() {
        ImageView brush = findViewById(R.id.brush);
        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brushDialog = new BrushDialog(getApplicationContext());
                brushDialog.init();
                brushDialog.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
                params[0].alpha = 0.7f;
                getWindow().setAttributes(params[0]);
                brushDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        params[0] = getWindow().getAttributes();
                        params[0].alpha = 1f;
                        getWindow().setAttributes(params[0]);
                    }
                });
            }
        });

        ImageView circle = findViewById(R.id.circle);
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorDialog = new ColorDialog(getApplicationContext());
                colorDialog.setColorEditTextHint(InkPresenter.getStrokeColor());
                colorDialog.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
                params[0].alpha = 0.7f;
                getWindow().setAttributes(params[0]);
                colorDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        params[0] = getWindow().getAttributes();
                        params[0].alpha = 1f;
                        getWindow().setAttributes(params[0]);
                    }
                });
            }
        });
    }

    /**
     * 隐藏颜色选取面板弹出框
     */
    public void dismissColorDialog() {
        colorDialog.dismiss();
    }

    public void dismissMenuDialog() {
        menuDialog.dismiss();
    }

    /**
     * 添加点击返回键处理，实现双击退出功能
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出哦", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    finish();
                }
                break;
        }
        return true;
    }
}

