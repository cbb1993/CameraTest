package com.cbb.cameratest;

/**
 * Created by 坎坎.
 * Date: 2020/7/11
 * Time: 19:37
 * describe:
 */
import android.content.ContentResolver;
import android.content.ContentValues;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

    private static final String TAG = "ImageUtils";

    private static String GALLERY_PATH ;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static String lastPath ;

    public static Bitmap rotateBitmap(Bitmap source, int degree, boolean flipHorizontal, boolean recycle) {
        if (degree == 0) {
            return source;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        if (flipHorizontal) {
            matrix.postScale(-1, 1); // 前置摄像头存在水平镜像的问题，所以有需要的话调用这个方法进行水平镜像
        }
        Bitmap rotateBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        if (recycle) {
            source.recycle();
        }
        return rotateBitmap;
    }

    private static void initBaseFile(){
        File file = new File(Environment.getExternalStorageDirectory(),"Camera");
        GALLERY_PATH= file.getAbsolutePath();
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static void saveBitmap(Bitmap bitmap) {
        initBaseFile();
        String fileName = DATE_FORMAT.format(new Date(System.currentTimeMillis())) + ".jpg";
        File outFile = new File(GALLERY_PATH, fileName);
        Log.d(TAG, "saveImage. filepath: " + outFile.getAbsolutePath());
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outFile);
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            if (success) {
                lastPath = outFile.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveImage(byte[] jpeg) {
        initBaseFile();
        String fileName = DATE_FORMAT.format(new Date(System.currentTimeMillis())) + ".jpg";
        File outFile = new File(GALLERY_PATH, fileName);
        Log.d(TAG, "saveImage. filepath: " + outFile.getAbsolutePath());
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outFile);
            os.write(jpeg);
            os.flush();
            os.close();
            lastPath = outFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
