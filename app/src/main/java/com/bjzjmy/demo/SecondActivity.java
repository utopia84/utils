package com.bjzjmy.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.runtime.permission.utils.PermissionUtils;

public class SecondActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.bt_test).setOnClickListener(v->{
            openCamera();
        });
    }

    private void openCamera() {
        if (PermissionUtils.hasSelfPermissions(this, Manifest.permission.CAMERA)) {
            Log.e("test", "我调用了摄像头");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }else{
                Log.e("test", "我调用了摄像头");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CAMERA == requestCode ) {
            //授权
            if (PermissionUtils.verifyPermissions(grantResults)) {
                Log.e("test", "我调用了摄像头");
                return;
            }

            //拒绝
            if (PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
                //当用户之前已经请求过该权限并且拒绝了授权
            } else {
                //用户勾选了不再提醒
            }

        }
    }
}