package com.sjk.palette;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static InkPresenter inkPresenter;
    private static MainActivity mainActivity;
    private ColorDialog colorDialog;
    private long firstTime = 0;

    /**
     * 将已在内存中的MainActivity赋值给mainActivity
     */
    public MainActivity() {
        mainActivity = this;
    }

    /**
     * 获取mainActivity
     * @return
     */
    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    /**
     * 活动被创建时调用
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        inkPresenter = findViewById(R.id.inkPresenter);
        titleOnTouchListener();
        toolOnClickListener();
    }

    /**
     * 获取笔触大小
     * @return
     */
    public int getStrokeWidthText() {
        TextView strokeWidthText = findViewById(R.id.strokeWidth);
        return Integer.parseInt(strokeWidthText.getText().toString());
    }

    /**
     * 获取笔触颜色
     * @return
     */
    public int getStrokeColorText() {
        TextView strokeColorText = findViewById(R.id.strokeColor);
        return Color.parseColor(strokeColorText.getText().toString());
    }

    /**
     * 设置笔触颜色
     * @param str
     */
    public void setStrokeColorText(String str) {
        TextView textView = findViewById(R.id.strokeColor);
        textView.setText(str);
    }

    /**
     * 返回画布
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
    }

    /**
     * 调用画笔预览的repaint方法
     */
    public void repaintSizePreview() {
        PaintPreview paintPreview = findViewById(R.id.paintPreview);
        paintPreview.repaint();
    }

    /**
     * 创建toolBar的点击事件
     */
    public void toolOnClickListener() {
        ImageView reduce = findViewById(R.id.reduce);
        reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView strokeWidth = findViewById(R.id.strokeWidth);
                int width = getStrokeWidthText();
                if (width > 1) {
                    strokeWidth.setText("" + (width - 1));
                    repaintSizePreview();
                }
            }
        });
        reduce.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView strokeWidth = findViewById(R.id.strokeWidth);
                int width = getStrokeWidthText();
                if (width > 5) {
                    strokeWidth.setText("" + (width - 5));
                    repaintSizePreview();
                } else if (width > 1 && width <= 5) {
                    strokeWidth.setText("1");
                    repaintSizePreview();
                }
                return true;
            }
        });

        ImageView enlarge = findViewById(R.id.enlarge);
        enlarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView strokeWidth = findViewById(R.id.strokeWidth);
                int width = getStrokeWidthText();
                if (width < 40) {
                    strokeWidth.setText("" + (width + 1));
                    repaintSizePreview();
                }
            }
        });
        enlarge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView strokeWidth = findViewById(R.id.strokeWidth);
                int width = getStrokeWidthText();
                if (width < 35) {
                    strokeWidth.setText("" + (width + 5));
                    repaintSizePreview();
                } else if (width >= 35 && width < 40) {
                    strokeWidth.setText("40");
                    repaintSizePreview();
                }
                return true;
            }
        });

        TextView strokeColor = findViewById(R.id.strokeColor);
        strokeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorDialog = new ColorDialog(getApplicationContext());
                TextView strokeColorText = findViewById(R.id.strokeColor);
                colorDialog.setColorEditTextHint(strokeColorText.getText().toString());
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

    /**
     * 添加点击返回键处理，实现双击退出功能
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

