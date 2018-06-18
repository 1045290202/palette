package com.sjk.palette.mainActivity.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.sjk.palette.mainActivity.MenuAdapter;
import com.sjk.palette.aboutActivity.Option;
import com.sjk.palette.R;
import com.sjk.palette.aboutActivity.AboutActivity;
import com.sjk.palette.mainActivity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class MenuDialog extends CustomDialog {
    private Context context;
    private View view;
    private List<Option> optionList = new ArrayList<>();

    public MenuDialog(Context context) {
        dialogInit(context);

        initOptions();

        MenuAdapter menuAdapter = new MenuAdapter(optionList);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(menuAdapter);
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void dialogInit(Context context) {
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.view = LayoutInflater.from(context).inflate(R.layout.menu_dialog, null);
        this.context = context;
        this.setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = v.findViewById(R.id.menu_dialog).getTop();
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
    }

    public void initOptions() {
        Option about = new Option("关于", R.drawable.about);
        optionList.add(about);
        Option exit = new Option("退出", R.drawable.exit);
        optionList.add(exit);
    }

    @Override
    public void delayAndDismiss(int delayMills) {
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.getMainActivity(), AboutActivity.class);
                MainActivity.getMainActivity().startActivity(intent);
                dismiss();
            }
        }, delayMills);
    }

    public void delayAndFinish(int delayMills) {
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.getMainActivity().finish();
            }
        }, delayMills);
    }
}
