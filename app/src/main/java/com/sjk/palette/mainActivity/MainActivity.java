package com.sjk.palette.mainActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sjk.palette.mainActivity.dialogs.BrushDialog;
import com.sjk.palette.mainActivity.dialogs.ColorDialog;
import com.sjk.palette.mainActivity.dialogs.CustomDialog;
import com.sjk.palette.mainActivity.dialogs.MenuDialog;
import com.sjk.palette.R;
import com.sjk.palette.mainActivity.dialogs.SaveDialog;

public class MainActivity extends Activity {
    private static InkPresenter inkPresenter;
    private static MainActivity mainActivity;
    private SaveDialog saveDialog;
    private ColorDialog colorDialog;
    private BrushDialog brushDialog;
    private MenuDialog menuDialog;
    private long firstTime = 0;
    final static int REQUEST_WRITE = 1;
    private Context context;

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
     * 活动被创建时调用,并判断是否拥有储存权限
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
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
        return Color.parseColor(InkPresenter.getStrokeColor());
    }

    /**
     * 设置笔触颜色
     *
     * @param str
     */
    public void setStrokeColor(String str) {
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
        saveDialog = new SaveDialog(context);
        createCustomDialog(saveDialog);
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
                menuDialog = new MenuDialog(context);
                createCustomDialog(menuDialog);
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
                createCustomDialog(brushDialog);
            }
        });

        ImageView circle = findViewById(R.id.circle);
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorDialog = new ColorDialog(getApplicationContext());
                createCustomDialog(colorDialog);
            }
        });
    }

    /**
     * @param customDialog
     */
    public void createCustomDialog(CustomDialog customDialog) {
        if (customDialog instanceof ColorDialog) {
            colorDialog.setColorEditTextHint(InkPresenter.getStrokeColor());
        }
        customDialog.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
        params[0].alpha = 0.7f;
        getWindow().setAttributes(params[0]);
        customDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params[0] = getWindow().getAttributes();
                params[0].alpha = 1f;
                getWindow().setAttributes(params[0]);
                inkPresenter.drawLines();
            }
        });
    }

    /**
     * 隐藏颜色选取面板弹出框
     */
    public void dismissColorDialog() {
        colorDialog.delayAndDismiss(CustomDialog.delayMills);
    }

    public void dismissMenuDialog() {
        menuDialog.delayAndDismiss(CustomDialog.delayMills);
    }

    public void finishMainActivity() {
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.getMainActivity().finish();
            }
        }, CustomDialog.delayMills);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

