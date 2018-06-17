package com.sjk.palette;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ColorDialog extends CustomDialog {
    private Context context;
    private View view;

    /**
     * 构造函数
     *
     * @param context
     */
    public ColorDialog(Context context) {
        dialogInit(context);

        onButtonClick();

        ColorPicker colorPicker = view.findViewById(R.id.color_picker);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) colorPicker.getLayoutParams();
        layoutParams.height = 1;
        colorPicker.setLayoutParams(layoutParams);
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void dialogInit(Context context) {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.view = LayoutInflater.from(context).inflate(R.layout.color_dialog, null);
        this.context = context;
        setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = v.findViewById(R.id.color_dialog).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        setContentView(this.view);
        setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        setBackgroundDrawable(dw);
        setAnimationStyle(R.style.BottomDialogAnimation);
    }

    /**
     * 添加按键的点击事件
     */
    public void onButtonClick() {
        Button ok = view.findViewById(R.id.color_dialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.color);
                String colorValue = editText.getText().toString();
                colorValue = colorValue.toUpperCase();
                boolean isMatches = false;
                if (colorValue.matches("^#([0-9A-F]{8})$")) {
                    isMatches = true;
                } else if (colorValue.matches("^([0-9A-F]{8})$")) {
                    colorValue = "#" + colorValue;
                    isMatches = true;
                } else if (colorValue.matches("^#([0-9A-F]{6})$")) {
                    colorValue = colorValue.replace("#", "#FF");
                    isMatches = true;
                } else if (colorValue.matches("^([0-9A-F]{6})$")) {
                    colorValue = "#FF" + colorValue;
                    isMatches = true;
                } else {
                    Toast.makeText(context, "哎呀色值输入错误了呢，快重新输入吧！", Toast.LENGTH_SHORT).show();
                }
                if (isMatches) {
                    MainActivity.getMainActivity().setStrokeColor(colorValue);
                    MainActivity.getMainActivity().imageHint(R.drawable.circle, colorValue);
                    delayAndDismiss(delayMills);
                }
            }
        });
        ok.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditText editText = view.findViewById(R.id.color);
                String colorValue = editText.getText().toString();
                colorValue = colorValue.toUpperCase();
                boolean isMatches = false;
                if (colorValue.matches("^#([0-9A-F]{6})$")) {
                    isMatches = true;
                } else if (colorValue.matches("^([0-9A-F]{6})$")) {
                    isMatches = true;
                    colorValue = "#" + colorValue;
                } else {
                    Toast.makeText(context, "哎呀色值输入错误了呢，快重新输入吧！", Toast.LENGTH_SHORT).show();
                }
                if (isMatches) {
                    InkPresenter.setBgColor(Color.parseColor(colorValue));
                    Toast.makeText(context, "画板背景色设置成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                return true;
            }
        });

        Button cancel = view.findViewById(R.id.color_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayAndDismiss(delayMills);
            }
        });
    }

    /**
     * 将文本框的hint改成activity_main中的色值，由MainActivity调用
     *
     * @param str 传入的字符串
     */
    public void setColorEditTextHint(String str) {
        EditText colorEditText = view.findViewById(R.id.color);
        colorEditText.setHint(str);
    }
}
