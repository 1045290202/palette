package com.sjk.palette.mainActivity.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.sjk.palette.R;
import com.sjk.palette.mainActivity.MainActivity;

public class BrushDialog extends CustomDialog {
    private View view;
    private Context context;
    private SeekBar strokeWidthSeekBar;
    private PaintPreview paintPreview;
    private static int strokeWidth;

    public BrushDialog(Context context) {
        dialogInit(context);

        paintPreview = view.findViewById(R.id.paint_preview);
        strokeWidth = MainActivity.getMainActivity().getStrokeWidth();

        strokeWidthSeekBar = view.findViewById(R.id.stroke_width);
        strokeWidthSeekBar.setProgress(strokeWidth - 1);
        strokeWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                strokeWidth = progress + 1;
                paintPreview.repaint(strokeWidth);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button ok = view.findViewById(R.id.brush_dialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getMainActivity().setStrokeWidth(strokeWidth);
                delayAndDismiss(delayMills);
            }
        });

        Button cancel = view.findViewById(R.id.brush_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayAndDismiss(delayMills);
            }
        });

        paintPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getMainActivity().setStrokeWidth(3);
                paintPreview.repaint(3);
                strokeWidthSeekBar.setProgress(2);
                strokeWidth = 3;
            }
        });
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void dialogInit(Context context) {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.view = LayoutInflater.from(context).inflate(R.layout.brush_dialog, null);
        this.context = context;
        setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = v.findViewById(R.id.brush_dialog).getTop();
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
}
