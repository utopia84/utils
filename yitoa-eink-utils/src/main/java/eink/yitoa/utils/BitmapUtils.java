package eink.yitoa.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BitmapUtils {

    public static Bitmap rotateBmp(Bitmap bmp, int degrees) {
        Matrix var2 = new Matrix();
        var2.postRotate((float)degrees);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), var2, true);
    }

    public static boolean savePngToFile(Bitmap bitmap, String dirName, String pngFileName, boolean isNeedOverrideDirPermission) {
        if (bitmap == null) {
            return false;
        } else {
            boolean var5;
            try {
                File var4 = new File(dirName);
                if (!var4.exists()) {
                    var4.mkdirs();
                }

                File var16 = new File(dirName,pngFileName);
                if (!var16.exists()) {
                    var16.createNewFile();
                }

                if (isNeedOverrideDirPermission) {
                    String var6 = "chmod 777 " + var4.getAbsolutePath();
                    String var7 = "chmod 777 " + var16.getAbsolutePath();
                    Runtime var8 = Runtime.getRuntime();
                    var8.exec(var6);
                    var8.exec(var7);
                }

                FileOutputStream var17 = new FileOutputStream(var16);
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, var17);
                var17.flush();
                var17.close();
                boolean var18 = true;
                return var18;
            } catch (FileNotFoundException var13) {
                var13.printStackTrace();
                var5 = false;
                return var5;
            } catch (IOException var14) {
                var14.printStackTrace();
                var5 = false;
            } finally {
                bitmap.recycle();
            }

            return var5;
        }
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String dirName, String bmpFileName, boolean isNeedOverrideDirPermission) {
        if (bitmap == null) {
            return false;
        } else {
            int var4 = bitmap.getWidth();
            int var5 = bitmap.getHeight();
            int var6 = var5 * (var4 * 3 + var4 % 4);

            boolean var8;
            try {
                File var7 = new File(dirName);
                if (!var7.exists()) {
                    var7.mkdirs();
                }

                File var45 = new File(dirName,bmpFileName);
                if (!var45.exists()) {
                    var45.createNewFile();
                }

                if (isNeedOverrideDirPermission) {
                    String var9 = "chmod 777 " + var7.getAbsolutePath();
                    String var10 = "chmod 777 " + var45.getAbsolutePath();
                    Runtime var11 = Runtime.getRuntime();
                    var11.exec(var9);
                    var11.exec(var10);
                }

                FileOutputStream var46 = new FileOutputStream(var45);
                short var47 = 19778;
                long var48 = (long)(54 + var6);
                byte var13 = 0;
                byte var14 = 0;
                long var15 = 54L;
                writeWord(var46, var47);
                writeDword(var46, var48);
                writeWord(var46, var13);
                writeWord(var46, var14);
                writeDword(var46, var15);
                long var17 = 40L;
                byte var19 = 1;
                byte var20 = 24;
                long var21 = 0L;
                long var23 = 0L;
                long var25 = 0L;
                long var27 = 0L;
                long var29 = 0L;
                long var31 = 0L;
                writeDword(var46, var17);
                writeLong(var46, (long)var4);
                writeLong(var46, (long)var5);
                writeWord(var46, var19);
                writeWord(var46, var20);
                writeDword(var46, var21);
                writeDword(var46, var23);
                writeLong(var46, var25);
                writeLong(var46, var27);
                writeDword(var46, var29);
                writeDword(var46, var31);
                byte[] var33 = new byte[var6];
                int var34 = var4 * 3 + var4 % 4;
                int var35 = 0;

                for(int var36 = var5 - 1; var35 < var5; --var36) {
                    int var37 = 0;

                    for(int var38 = 0; var37 < var4; var38 += 3) {
                        int var39 = bitmap.getPixel(var37, var35);
                        var33[var36 * var34 + var38] = (byte) Color.blue(var39);
                        var33[var36 * var34 + var38 + 1] = (byte)Color.green(var39);
                        var33[var36 * var34 + var38 + 2] = (byte)Color.red(var39);
                        ++var37;
                    }

                    ++var35;
                }

                var46.write(var33);
                var46.flush();
                var46.close();
                boolean var49 = true;
                return var49;
            } catch (Exception var43) {
                var43.printStackTrace();
                var8 = false;
            } finally {
                bitmap.recycle();
            }

            return var8;
        }
    }


    public static void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] var3 = new byte[]{(byte)((int)(value & 255L)), (byte)((int)(value >> 8 & 255L)), (byte)((int)(value >> 16 & 255L)), (byte)((int)(value >> 24 & 255L))};
        stream.write(var3);
    }

    private static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] var2 = new byte[]{(byte)(value & 255), (byte)(value >> 8 & 255)};
        stream.write(var2);
    }

    private static void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] var3 = new byte[]{(byte)((int)(value & 255L)), (byte)((int)(value >> 8 & 255L)), (byte)((int)(value >> 16 & 255L)), (byte)((int)(value >> 24 & 255L))};
        stream.write(var3);
    }

    public static void copyFileByChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
}
