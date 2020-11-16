package com.bjzjmy.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.runtime.permission.annotation.NeedsPermission;
import com.zjmy.sdk.oss.OssService;
import com.zjmy.sdk.oss.callback.OSSCompletedCallback;
import com.zjmy.sdk.oss.callback.OSSProgressCallback;
import com.zjmy.sdk.oss.callback.SimpleOSSCallBack;
import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.exception.ServiceException;
import com.zjmy.sdk.oss.model.OSSRequest;
import com.zjmy.sdk.oss.model.OSSResult;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AspectJ
        //.java -> .class -> .class
        findViewById(R.id.bt_download).setOnClickListener(v->{
            downloadFile();
        });

        findViewById(R.id.bt_upload).setOnClickListener(v->{
            uploadFile();
        });
    }

    @NeedsPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = REQUEST_WRITE_EXTERNAL_STORAGE)
    private void uploadFile(){
        OssService.get().upload("2", "/sdcard/01.png", new SimpleOSSCallBack() {
            @Override
            public void onSuccess(OSSRequest request, OSSResult result) {

            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {

            }
        });
    }

    @NeedsPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = REQUEST_WRITE_EXTERNAL_STORAGE)
    public void downloadFile() {
        OssService.get().download("2", "/sdcard/2.jpg");
    }

    //@OnPermissionDenied
    private void permissionDenied(int requestCode) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            Log.e("test", "摄像头，权限被拒绝");
        }
    }



}