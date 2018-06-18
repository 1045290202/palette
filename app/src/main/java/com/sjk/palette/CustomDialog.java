package com.sjk.palette;

import android.content.Context;
import android.os.Handler;
import android.widget.PopupWindow;

public abstract class CustomDialog extends PopupWindow {
    public final static int delayMills = 200;

    public abstract void dialogInit(Context context);

    /**
     * 关闭弹出框，由取消按钮调用
     *
     * @param delayMills 延迟时间
     */
    public void delayAndDismiss(int delayMills) {
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delayMills);
        MainActivity.getMainActivity().getInkPresenter().drawLines();
    }
}
