package com.bjzjmy.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.utopia.logan.LogWriter;

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
            LogWriter.writeBehLog("我点击了按钮:"+i++);
        });

        findViewById(R.id.bt_change).setOnClickListener(v->{
            LogWriter.s(true);
        });
    }
}