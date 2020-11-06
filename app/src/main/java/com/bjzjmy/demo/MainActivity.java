package com.bjzjmy.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.runtime.permission.annotation.NeedsPermission;
import com.runtime.permission.annotation.OnPermissionDenied;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AspectJ
        //.java -> .class -> .class
        findViewById(R.id.bt_test).setOnClickListener(v->{
            openCamera();
        });
    }

    @NeedsPermission(value = Manifest.permission.CAMERA, requestCode = REQUEST_CAMERA)
    public void openCamera() {
        Log.e("test", "我调用了摄像头");
    }

    @OnPermissionDenied
    private void permissionDenied(int requestCode) {
        if (requestCode == REQUEST_CAMERA) {
            Log.e("test", "摄像头，权限被拒绝");
        }
    }



}