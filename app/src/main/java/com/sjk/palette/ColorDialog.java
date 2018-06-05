package com.sjk.palette;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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

public class ColorDialog extends PopupWindow {
    private Context context;
    private View view;

    /**
     * 构造函数
     *
     * @param context
     */
    public ColorDialog(Context context) {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.view = LayoutInflater.from(context).inflate(R.layout.color_dialog, null);
        this.context = context;
        this.setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.color_dialog).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        this.setContentView(this.view);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.BottomDialogAnimation);

        onButtonClick();

        ColorPicker colorPicker = view.findViewById(R.id.color_picker);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) colorPicker.getLayoutParams();
        layoutParams.height = 1;
        colorPicker.setLayoutParams(layoutParams);
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
                if (colorValue.matches("^#([0-9A-F]{8})$")) {
                    MainActivity.getMainActivity().setStrokeColorText(colorValue);
                    dismiss();
                } else if (colorValue.matches("^([0-9A-F]{8})$")) {
                    MainActivity.getMainActivity().setStrokeColorText("#" + colorValue);
                    dismiss();
                } else if (colorValue.matches("^#([0-9A-F]{6})$")) {
                    MainActivity.getMainActivity().setStrokeColorText(colorValue.replace("#", "#FF"));
                    dismiss();
                } else if (colorValue.matches("^([0-9A-F]{6})$")) {
                    MainActivity.getMainActivity().setStrokeColorText("#FF" + colorValue);
                    dismiss();
                } else {
                    Toast.makeText(context, "哎呀色值输入错误了呢，快重新输入吧！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancel = view.findViewById(R.id.color_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
