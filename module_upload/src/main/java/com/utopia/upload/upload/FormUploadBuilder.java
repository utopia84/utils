package com.utopia.upload.upload;

import com.utopia.upload.bean.UploadFile;
import com.utopia.upload.upload.base.BaseUploadBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FormUploadBuilder extends BaseUploadBuilder<FormUploadBuilder> {
    private List<UploadFile> files = new ArrayList<>();//本地文件集合

    //添加单个文件
    public FormUploadBuilder addFile(String name, String filename, File file) {
        files.add(new UploadFile(name, filename, file));
        return this;
    }

    //批量添加文件
    public FormUploadBuilder addFiles(List<UploadFile> files) {
        this.files.addAll(files);
        return this;
    }


    //构建表单式本地文件上传
    public FileUploadRequest fileUploadBuild() {
        return new FileUploadRequest(url, files, params, headers);
    }
}
