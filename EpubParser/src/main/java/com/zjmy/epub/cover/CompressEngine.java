package com.zjmy.epub.cover;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.zjmy.epub.cover.calculation.LubanCalculation;


public class CompressEngine {
    private LubanCalculation calculation;

    private ZipEntry zipEntry;
    private ZipFile zipFile;
    private BitmapFactory.Options options;

    public CompressEngine(ZipFile zipFile, ZipEntry zipEntry) throws IOException, NullPointerException {
        this.calculation = new LubanCalculation();
        this.zipFile = zipFile;
        this.zipEntry = zipEntry;

        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;//cpu资源换内存
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
    }

    public Bitmap compress() throws NullPointerException, IOException {
        options.inSampleSize = calculation.calculateInSampleSize(options, 160, 220);
        options.inJustDecodeBounds = false;
        options.inScaled = true;
        //options.inSampleSize = calculation.calculateInSampleSize(options.outWidth, options.outHeight);
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        Bitmap tagBitmap = BitmapFactory.decodeStream(inputStream, null, options);
        if (tagBitmap!=null) {
            tagBitmap = Bitmap.createScaledBitmap(tagBitmap, 160, 220, true);
        }
        inputStream.close();

        return tagBitmap;
    }
}