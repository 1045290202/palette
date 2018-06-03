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

    public MainActivity() {
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        inkPresenter = findViewById(R.id.inkPresenter);
        titleOnTouchListener();
        toolOnClickListener();
//        EditText colorEditText=findViewById(R.id.color);
//        colorEditText.setHint(getStrokeColorText());
    }

    public int getStrokeWidthText() {
        TextView strokeWidthText = findViewById(R.id.strokeWidth);
        return Integer.parseInt(strokeWidthText.getText().toString());
    }

    public int getStrokeColorText() {
        TextView strokeColorText = findViewById(R.id.strokeColor);
        return Color.parseColor(strokeColorText.getText().toString());
    }

    public void setStrokeColorText(String str) {
        TextView textView = findViewById(R.id.strokeColor);
        textView.setText(str);
    }

    private boolean isOuterUp(MotionEvent event, View v) {
        float touchX = event.getX();
        float touchY = event.getY();
        float maxX = v.getWidth();
        float maxY = v.getHeight();

        return touchX < 0 || touchX > maxX || touchY < 0 || touchY > maxY;
    }

    public InkPresenter getInkPresenter() {
        return inkPresenter;
    }

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

    //@SuppressLint("ClickableViewAccessibility")
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

    public void repaintSizePreview() {
        PaintPreview paintPreview = findViewById(R.id.paintPreview);
        paintPreview.repaint();
    }

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
        /**
         * 弹出颜色选取框
         */
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

    public void dismissColorDialog() {
        colorDialog.dismiss();
    }

    private long firstTime = 0;

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

