package com.bjzjmy.demo;

import androidx.appcompat.app.AppCompatActivity;
import eink.yitoa.utils.EinkRefreshMode;
import eink.yitoa.utils.SystemUtil;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private int i = 0 ;
    private boolean isFullRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_content);

        findViewById(R.id.bt_test).setOnClickListener(v->{
            int no = SystemUtil.getInstance().getSystemSleepImageNo();
            textView.setText("随机数字："+no);
        });

        findViewById(R.id.bt_change).setOnClickListener(v->{
            if (!isFullRefresh) {
                isFullRefresh = true;
                EinkRefreshMode.updateToFullRefreshMode();
            }else{
                isFullRefresh = false;
                EinkRefreshMode.updateToLocalRefreshMode();
            }
        });
    }
}