package com.utopia.upload.upload;

import android.util.Base64;
import com.utopia.upload.bean.UploadFile;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadRequest extends FormUploadRequest {

    private List<UploadFile> files;

    public FileUploadRequest(String url, List<UploadFile> files, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.files = files;
        this.params = params;
        this.headers = headers;
    }

    @Override
    protected void buildRequestBody(MultipartBody.Builder builder) {
        if (files != null && !files.isEmpty()) {
            for (UploadFile file : files) {
                RequestBody fileBody = RequestBody.create(MediaType.parse(getMimeType(file.getName())), file.getFile());
                String fileName = file.getFilename();
                if (!fileName.startsWith("BEH-") && !fileName.startsWith("FUN-") && !fileName.startsWith("M1701")) {
                    String suffix = fileName.substring(fileName.lastIndexOf("."));
                    String encryptFileName = Base64.encodeToString(fileName.getBytes(), Base64.DEFAULT);
                    fileName = encryptFileName + suffix;
                }
                builder.addFormDataPart(file.getName(), fileName, fileBody);
            }
        }
    }

    /**
     * 根据文件名解析contentType
     */
    private String getMimeType(String name) {
        String contentTypeFor = null;
        try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
}
