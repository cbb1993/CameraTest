package com.cbb.cameratest;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.nio.ByteBuffer;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermission();
    }



    private Camera2SurfaceView mCameraView;
    private Camera2Proxy mCameraProxy;
    private ImageView iv_image;

    private void initCamera() {
        setContentView(R.layout.activity_main);
        mCameraView = findViewById(R.id.camera_view);
        mCameraProxy = mCameraView.getCameraProxy();
        iv_image=findViewById(R.id.iv_image);
    }


    public void takePhoto(View view) {
        mCameraProxy.setImageAvailableListener(mOnImageAvailableListener);
        mCameraProxy.captureStillPicture(); // 拍照
    }
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    new ImageSaveTask().execute(reader.acquireNextImage()); // 保存图片
                }
            };
    private class ImageSaveTask extends AsyncTask<Image, Void, Void> {

        @Override
        protected Void doInBackground(Image ... images) {
            ByteBuffer buffer = images[0].getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            long time = System.currentTimeMillis();
            if (mCameraProxy.isFrontCamera()) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Log.d(TAG, "BitmapFactory.decodeByteArray time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                // 前置摄像头需要左右镜像
                Bitmap rotateBitmap = ImageUtils.rotateBitmap(bitmap, -90, true, true);
                Log.d(TAG, "rotateBitmap time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                ImageUtils.saveBitmap(rotateBitmap);
                Log.d(TAG, "saveBitmap time: " + (System.currentTimeMillis() - time));
                rotateBitmap.recycle();
            } else {
                Log.e("---------","-----22222-----");
                ImageUtils.saveImage(bytes);
                Log.d(TAG, "saveBitmap time: " + (System.currentTimeMillis() - time));
            }
            images[0].close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Glide.with(MainActivity.this).load(ImageUtils.lastPath).into(iv_image);
        }
    }

    private void requestPermission() {
        String[] ps = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        AndPermission.with(this)
                .runtime()
                .permission(ps)
                // 用户给权限了
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        initCamera();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        finish();
                    }
                }).start();
    }
}
