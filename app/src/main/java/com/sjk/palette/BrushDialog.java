package com.sjk.palette;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class BrushDialog extends PopupWindow {
    View view;
    Context context;
    SeekBar strokeWidthSeekBar;
    PaintPreview paintPreview;
    private static int strokeWidth;

    public BrushDialog(Context context) {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.view = LayoutInflater.from(context).inflate(R.layout.brush_dialog, null);
        this.context = context;
        this.setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.brush_dialog).getTop();
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
        paintPreview = view.findViewById(R.id.paint_preview);

        strokeWidth = MainActivity.getMainActivity().getStrokeWidth();

        strokeWidthSeekBar = view.findViewById(R.id.stroke_width);
        strokeWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //MainActivity.getMainActivity().setStrokeWidth(progress);
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
                dismiss();
            }
        });

        Button cancel = view.findViewById(R.id.brush_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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

    public void init() {
        strokeWidthSeekBar.setProgress(MainActivity.getMainActivity().getStrokeWidth() - 1);
    }
}
