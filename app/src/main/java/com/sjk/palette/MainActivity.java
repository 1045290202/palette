package com.sjk.palette;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static InkPresenter inkPresenter;
    private static MainActivity mainActivity;

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

    @SuppressLint("ClickableViewAccessibility")
    private void titleOnTouchListener() {
        final TextView save = findViewById(R.id.save);
        save.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        save.setBackgroundColor(Color.parseColor("#cccccc"));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isOuterUp(event, v)) {//移出
                            save.setBackgroundColor(Color.parseColor("#eeeeee"));
                        } else {//未移出
                            save.setBackgroundColor(Color.parseColor("#eeeeee"));
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
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final TextView clear = findViewById(R.id.clear);
        clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clear.setBackgroundColor(Color.parseColor("#cccccc"));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isOuterUp(event, v)) {//移出
                            clear.setBackgroundColor(Color.parseColor("#eeeeee"));
                        } else {//未移出
                            inkPresenter.clear();
                            clear.setBackgroundColor(Color.parseColor("#eeeeee"));
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final TextView revoke = findViewById(R.id.revoke);
        revoke.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        revoke.setBackgroundColor(Color.parseColor("#cccccc"));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isOuterUp(event, v)) {//移出
                            revoke.setBackgroundColor(Color.parseColor("#eeeeee"));
                        } else {//未移出
                            inkPresenter.revoke();
                            revoke.setBackgroundColor(Color.parseColor("#eeeeee"));
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final TextView restore = findViewById(R.id.restore);
        restore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        restore.setBackgroundColor(Color.parseColor("#cccccc"));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isOuterUp(event, v)) {//移出
                            restore.setBackgroundColor(Color.parseColor("#eeeeee"));
                        } else {//未移出
                            inkPresenter.restore();
                            restore.setBackgroundColor(Color.parseColor("#eeeeee"));
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void repaintSizePreview() {
        SizePreview sizePreview = findViewById(R.id.sizePreview);
        sizePreview.repaint();
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
//                Intent intent = new Intent(MainActivity.this, ColorDialog.class);
//                startActivity(intent);
                ColorDialog colorDialog = new ColorDialog(getApplicationContext());
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

}

