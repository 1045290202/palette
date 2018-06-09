package com.sjk.palette;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends Activity {
    PackageManager packageManager;
    Context context;
    private List<Info> infoList = new ArrayList<>();
    private final String apkPackageName = "com.coolapk.market";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        context = getApplicationContext();
        packageManager = getApplicationContext().getPackageManager();
        TextView nameVersion = findViewById(R.id.name_version);
        nameVersion.setText(getAppName(context) + "（V" + getVersionName(context) + "）");

        initInfo();
        InfoAdapter infoAdapter = new InfoAdapter(AboutActivity.this, R.layout.info, infoList);
        ListView listView = findViewById(R.id.info_list);
        listView.setAdapter(infoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Info info = infoList.get(position);
                Uri uri;
                Intent intent;
                switch (position) {
                    case 0:
                        if (CheckApkExist.checkApkExist(context, apkPackageName)) {
                            uri = Uri.parse("coolmarket://u/458995");
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setPackage(apkPackageName);
                            startActivity(intent);
                        } else {
                            uri = Uri.parse("https://www.coolapk.com/u/458995");
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        if (CheckApkExist.checkApkExist(context, apkPackageName)) {
                            uri = Uri.parse("market://details?id=" + getPackageName());
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setPackage("com.coolapk.market");
                            startActivity(intent);
                        } else {
                            uri = Uri.parse("https://www.coolapk.com/apk/com.sjk.palette");
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        uri = Uri.parse(getString(R.string.open_source));
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Info info = infoList.get(position);
                Uri uri;
                Intent intent;
                switch (position) {
                    case 0:
                        String developer = getString(R.string.developer).replaceFirst("@", "");
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(getAppName(context), developer);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(context, "已复制“" + developer + "”", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        uri = Uri.parse("market://details?id=" + getPackageName());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.getPackage() != null) {
                            startActivity(intent);
                        } else {
                            Log.d("123", "2352");
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 初始化infoList
     */
    private void initInfo() {
        Info developer = new Info("开发者", getString(R.string.developer));
        infoList.add(developer);
        Info app = new Info("应用详情", "在酷安中查看");
        infoList.add(app);
        Info openSource = new Info("开源链接（GitHub）", getString(R.string.open_source));
        infoList.add(openSource);
    }

    public String getAppName(Context context) {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
