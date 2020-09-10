package com.utopia.upload.upload;

import com.utopia.upload.callback.FileUploadListener;

import java.io.Closeable;
import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    private RequestBody requestBody;
    private FileUploadListener callback;

    public ProgressRequestBody(RequestBody requestBody , FileUploadListener callback) {
        this.requestBody = requestBody;
        this.callback = callback;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) {
        CountingSink countingSink = null;
        BufferedSink bufferedSink = null;
        try {
            countingSink = new CountingSink(sink, callback, contentLength());
            bufferedSink = Okio.buffer(countingSink);
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            safeClose(bufferedSink);
            safeClose(countingSink);
            safeClose(sink);
        }

    }

    protected static class CountingSink extends ForwardingSink {
        private long totalLength;
        private long currentLength = 0;
        private FileUploadListener callback;

        public CountingSink(Sink delegate, FileUploadListener callback, long totalLength) {
            super(delegate);
            this.callback = callback;
            this.totalLength = totalLength;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            currentLength += byteCount;
            if (callback != null){
                callback.onProgress(currentLength,totalLength);
            }
        }
    }

    public void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
