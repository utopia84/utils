package com.zjmy.sdk.oss.internal;

import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.exception.ServiceException;
import com.zjmy.sdk.oss.callback.OSSCompletedCallback;
import com.zjmy.sdk.oss.model.AbortMultipartUploadRequest;
import com.zjmy.sdk.oss.model.CompleteMultipartUploadResult;
import com.zjmy.sdk.oss.model.InitiateMultipartUploadRequest;
import com.zjmy.sdk.oss.model.InitiateMultipartUploadResult;
import com.zjmy.sdk.oss.model.MultipartUploadRequest;
import com.zjmy.sdk.oss.network.ExecutionContext;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by jingdan on 2017/10/19.
 * multipart upload support concurrent thread work
 */
public class MultipartUploadTask extends BaseMultipartUploadTask<MultipartUploadRequest,
        CompleteMultipartUploadResult> implements Callable<CompleteMultipartUploadResult> {

    public MultipartUploadTask(InternalRequestOperation operation, MultipartUploadRequest request,
                               OSSCompletedCallback<MultipartUploadRequest, CompleteMultipartUploadResult> completedCallback,
                               ExecutionContext context) {
        super(operation, request, completedCallback, context);
    }

    @Override
    protected void initMultipartUploadId() throws ClientException, ServiceException {
        InitiateMultipartUploadRequest init = new InitiateMultipartUploadRequest(
                mRequest.getBucketName(), mRequest.getObjectKey(), mRequest.getMetadata());

        InitiateMultipartUploadResult initResult = mApiOperation.initMultipartUpload(init, null).getResult();

        mUploadId = initResult.getUploadId();
        mRequest.setUploadId(mUploadId);
    }

    @Override
    protected CompleteMultipartUploadResult doMultipartUpload() throws IOException, ServiceException, ClientException, InterruptedException {
        checkCancel();
        int readByte = mPartAttr[0];
        final int partNumber = mPartAttr[1];
        int currentLength = 0;
        for (int i = 0; i < partNumber; i++) {
            checkException();
            if (mPoolExecutor != null) {
                //need read byte
                if (i == partNumber - 1) {
                    readByte = (int) (mFileLength - currentLength);
                }
                final int byteCount = readByte;
                final int readIndex = i;
                currentLength += byteCount;
                mPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        uploadPart(readIndex, byteCount, partNumber);
                    }
                });
            }
        }

        if (checkWaitCondition(partNumber)) {
            synchronized (mLock) {
                mLock.wait();
            }
        }
        if (mUploadException != null) {
            abortThisUpload();
        }
        checkException();
        //complete sort
        CompleteMultipartUploadResult completeResult = completeMultipartUploadResult();

        releasePool();
        return completeResult;
    }

    @Override
    protected void abortThisUpload() {
        if (mUploadId != null) {
            AbortMultipartUploadRequest abort = new AbortMultipartUploadRequest(
                    mRequest.getBucketName(), mRequest.getObjectKey(), mUploadId);
            mApiOperation.abortMultipartUpload(abort, null).waitUntilFinished();
        }
    }

    @Override
    protected void processException(Exception e) {
        synchronized (mLock) {
            mPartExceptionCount++;
            if (mUploadException == null) {
                mUploadException = e;
                mLock.notify();
            }
        }
    }

    @Override
    protected void preUploadPart(int readIndex, int byteCount, int partNumber) throws Exception {
        checkException();
    }
}
