package com.zjmy.sdk.android;

import com.zjmy.sdk.oss.model.HeadObjectRequest;
import com.zjmy.sdk.oss.model.HeadObjectResult;
import com.zjmy.sdk.oss.model.ImagePersistRequest;
import com.zjmy.sdk.oss.model.ImagePersistResult;
import com.zjmy.sdk.oss.model.PutObjectRequest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by huaixu on 2018/1/30.
 */

public class ImagePersistTest extends BaseTestCase {
    public static final String JPG_OBJECT_KEY = "source-image-key";
    public static final String persist2Obj = "persis2Obj";


    private String imgPath = OSSTestConfig.FILE_DIR + "shilan.jpg";

    @Override
    void initTestData() throws Exception {
        OSSTestConfig.initDemoFile("shilan.jpg");
        PutObjectRequest putImg = new PutObjectRequest(mBucketName,
                JPG_OBJECT_KEY, imgPath);
        oss.putObject(putImg);
    }

    @Test
    public void testImagePersist() throws Exception {
        ImagePersistRequest request = new ImagePersistRequest(mBucketName, JPG_OBJECT_KEY, mBucketName, persist2Obj, "resize,w_100");
        try {
            ImagePersistResult result = oss.imagePersist(request);

            HeadObjectRequest head = new HeadObjectRequest(mBucketName, persist2Obj);

            HeadObjectResult headResult = oss.headObject(head);


            assertNotNull(headResult.getMetadata().getContentType());
            assertEquals(200, headResult.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }

    }
}
