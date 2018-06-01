package com.sjk.palette;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SaveDialog extends PopupWindow {
    private Context context;
    private View view;
    private InkPresenter inkPresenter;
    private char[] illegalCharacters = {'\\', '/', ':', '*', '?', '#', '"', '<', '>', '|', ' '};

    public SaveDialog(Context context) {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.view = LayoutInflater.from(context).inflate(R.layout.activity_save_dialog, null);
        this.context = context;

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.save_dialog).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.BottomDialogAnimation);

        onButtonClick();
    }

    public void onButtonClick() {
        Button ok = view.findViewById(R.id.save_dialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.file_name);
                String fileName = editText.getText().toString();
                if (fileName.length() > 0 && fileName.length() < 256) {
                    boolean judgeFlag = true;
                    if (fileName.charAt(0) == '.') {
                        judgeFlag = false;
                    } else {
                        OUT:
                        for (int i = 0, length = fileName.length(); i < length; i++) {
                            for (int j = 0, charLength = illegalCharacters.length; j < charLength; j++) {
                                if (fileName.charAt(i) == illegalCharacters[j]) {
                                    judgeFlag = false;
                                    break OUT;
                                }
                            }
                        }
                    }
                    if (judgeFlag == true) {
                        inkPresenter = MainActivity.getMainActivity().getInkPresenter();
                        inkPresenter.save(fileName);
                        dismiss();
                    } else {
                        Toast.makeText(context, "文件名可能输错了呢", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "文件名可能输错了呢", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancel = view.findViewById(R.id.save_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
