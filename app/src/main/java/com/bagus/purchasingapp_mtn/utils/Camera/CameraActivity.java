package com.bagus.purchasingapp_mtn.utils.Camera;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bagus.purchasingapp_mtn.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2/21/2018.
 */

public class CameraActivity extends AppCompatActivity {

    ImageView captureButton, flashButton, changeSide;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    FrameLayout frame;
    Camera camera;
    private boolean isFlashOn = false;
    Activity context;
    CameraPreview previewCamera;
    Camera.Parameters params;
    int currentCameraId;
    SurfaceView surfaceView;
    int FLAG_Rotate = 0;
    String jalur = null;
    int mDegress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        context = this;

        captureButton = (ImageView) findViewById(R.id.captureButton);
        flashButton = (ImageView) findViewById(R.id.flashButton);
        changeSide = (ImageView) findViewById(R.id.changebutton);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        previewCamera = new CameraPreview(this, surfaceView);

        frame = (FrameLayout) findViewById(R.id.frameLayout);
        frame.addView(previewCamera);
        previewCamera.setKeepScreenOn(true);

        camera = Camera.open();
        params = camera.getParameters();
//        ctrlAppModul = new CtrlAppModul(this);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    askPermissionAndWriteFile();
//                    takeFocusedPicture();
                } catch (Exception e) {
//                    Crashlytics.logException(e);
                }
            }
        });

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    flashButton.setImageResource(R.drawable.f2);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    camera.startPreview();
                    isFlashOn = false;
                } else {
                    flashButton.setImageResource(R.drawable.f1);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                    isFlashOn = true;
                }
            }
        });
        changeSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera != null) {
                    camera.stopPreview();
                }
//NB: if you don't release the current camera before switching, you app will crash
                camera.release();

//swap the id of the camera to be used
                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    flashButton.setVisibility(View.GONE);
                    changeSide.setImageResource(R.drawable.ic_camera_rear_black_24dp);
                    FLAG_Rotate = 1;

                } else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    flashButton.setVisibility(View.VISIBLE);
                    changeSide.setImageResource(R.drawable.ic_camera_front_black_24dp);
                    FLAG_Rotate = 0;
                }
                camera = Camera.open(currentCameraId);

                setCameraDisplayOrientation(CameraActivity.this, currentCameraId, camera);
                try {

                    camera.setPreviewDisplay(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }

        });
    }


    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

            try {
                camera.takePicture(mShutterCallback, null, jpegCallback);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    };

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };

    public void takeFocusedPicture() {
        camera.autoFocus(mAutoFocusCallback);
    }

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @SuppressWarnings("deprecation")
        public void onPictureTaken(byte[] data, Camera camera) {
            File root = Environment.getExternalStorageDirectory();
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            String mImageName = "/PurchasingApp" + timeStamp + ".jpg";
            jalur = getIntent().getStringExtra("PhotoFile");
            String prefix = getIntent().getStringExtra("prefix");
            File Path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PurchasingApp/" + prefix + "/");
            if (jalur == null) {
                Path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PurchasingApp/");
                jalur = Path.getAbsolutePath() + mImageName;
            }
            File gambar = new File(jalur);
            if (!Path.exists()) {
                Path.mkdirs();
            }
            new CompressImage(data, gambar).execute();
        }
    };

    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
    }

    private void releaseCameraAndPreview() {
        previewCamera.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    protected void onResume() {
        super.onResume();
        // TODO Auto-generated method stub
        if (camera == null) {
            camera = Camera.open();
            camera.startPreview();
            camera.setErrorCallback(new Camera.ErrorCallback() {
                public void onError(int error, Camera mcamera) {
                    camera.release();
                    camera = Camera.open();
                }
            });
        }
        if (camera != null) {
            if (Build.VERSION.SDK_INT >= 16)
                setCameraDisplayOrientation(context, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
            previewCamera.setCamera(camera);
        }
    }

    public int getCorrectCameraOrientation(Camera.CameraInfo info, Camera camera) {

        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;

        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        params.setExposureCompensation(params.getMaxExposureCompensation());

        if (params.isAutoExposureLockSupported()) {
            params.setAutoExposureLock(false);
        }
        List<int[]> frameRates = params.getSupportedPreviewFpsRange();
        int l_first = 0;
        int minFps = (frameRates.get(l_first))[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
        int maxFps = (frameRates.get(l_first))[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
        params.setPreviewFpsRange(minFps, maxFps);
        mDegress = result;
        params.setJpegQuality(100);
        params.setRotation(degrees);
        camera.setDisplayOrientation(result);

    }


    private void askPermissionAndWriteFile() {
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION, Manifest.permission.CAMERA);
        Log.d("canWrite :", String.valueOf(canWrite));
        //
        if (canWrite) {
            takeFocusedPicture();
        }
    }

    private boolean askPermission(int requestId, String permissionName) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }


    class CompressImage extends AsyncTask<Void, Void, String> {
        byte[] dataImages;
        File gambar;
        ProgressDialog loading;

        CompressImage(byte[] bytes, File gambarFile) {
            dataImages = bytes;
            gambar = gambarFile;
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(Void... bytes) {
            //TODO : nggawe file teko responseData
            File root = Environment.getExternalStorageDirectory();
            // TODO : KOMPRESS
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            options.inDither = true;
            options.inJustDecodeBounds = true;
            options.inInputShareable = true;
            Bitmap realImage = BitmapFactory.decodeByteArray(dataImages, 0, dataImages.length/*, options*/);
//            realImage = adjustedContrast(realImage, 20);
            Matrix matrix = new Matrix();
            if (FLAG_Rotate == 0) {
                matrix.setRotate(mDegress);
            } else {
                matrix.setRotate(90);
            }
            Bitmap bitmap;
//            Bitmap bitmap = Bitmap.createBitmap(rotateImage, 0, 0, rotateImage.getWidth(), rotateImage.getHeight(), matrix, false);
            int widthImage = realImage.getWidth();
            float dif = 0;
            int heightImage = realImage.getHeight();

            int trigger = 1280;

            Log.d("TRIGGER", String.valueOf(trigger));

            if (widthImage > heightImage) {
                if (widthImage < 1000) {
                    trigger = widthImage;
                }
                dif = (float) widthImage / trigger;
                widthImage = trigger;
                heightImage = (int) (heightImage / dif);
            } else if (widthImage < heightImage) {
                if (heightImage < 1000) {
                    trigger = heightImage;
                }
                dif = (float) heightImage / trigger;
                heightImage = trigger;
                widthImage = (int) (widthImage / dif);
            }

            Log.e("Different " + dif, "Width = " + widthImage + " height = " + heightImage);
            Log.e("Different0 " + dif, "Width0 = " + realImage.getWidth() + " height0 = " + realImage.getHeight());
//            bitmap = ScalingUtilities.createScaledBitmap(realImage, ctrlAppModul.getInt("60"), ctrlAppModul.getInt("59"), ScalingUtilities.ScalingLogic.FIT);
            if (realImage.getHeight() > realImage.getWidth()) {
                bitmap = BITMAP_RESIZER(realImage, 1280, 720/*, ScalingUtilities.ScalingLogic.FIT*/);
                widthImage = 1280;
                heightImage = 720;
            } else {
                bitmap = BITMAP_RESIZER(realImage, 720, 1280/*, ScalingUtilities.ScalingLogic.FIT*/);
                widthImage = 720;
                heightImage = 1280;
            }

            /*Log.e("BITMAP RESIZED", "Width = " + bitmap.getWidth() + " height= " + bitmap.getHeight());
            Log.wtf("X+WIDTH", widthImage + "-" + bitmap.getWidth());*/

            /*if (heightImage > bitmap.getHeight())
                heightImage = bitmap.getHeight();

            if (widthImage > bitmap.getWidth())
                widthImage = bitmap.getWidth();*/

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, widthImage, heightImage, matrix, false);

//            bitmap = ScalingUtilities.createScaledBitmap(realImage, widthImage, heightImage, ScalingUtilities.ScalingLogic.FIT);
//            bitmap = Bitmap.createBitmap(bitmap);
//            bitmap = ScalingUtilities.createScaledBitmap(realImage, ctrlAppModul.getInt("59"), ctrlAppModul.getInt("60"), ScalingUtilities.ScalingLogic.FIT);
//            bitmap = ScalingUtilities.createScaledBitmap(realImage, realImage.getWidth(), realImage.getHeight(), ScalingUtilities.ScalingLogic.FIT);
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

            ByteArrayOutputStream bytesData = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesData);
            FileOutputStream outStream = null;
            try {
                // Write to SD Card
                outStream = new FileOutputStream(gambar);
                outStream.write(bytesData.toByteArray());
                outStream.close();
//                Toast.makeText(getApplicationContext(), "File is Saved in  " + gambar, Toast.LENGTH_LONG).show();
                Log.e("File is Saved in  ", "" + gambar);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//            Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
//            switch(orientation) {
//
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotateImage = rotate(realImage, 90);
//                    Log.d("orientation1 :",String.valueOf(orientation));
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotateImage = rotate(realImage, 180);
//                    Log.d("orientation2 :",String.valueOf(orientation));
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotateImage = rotate(realImage, 270);
//                    Log.d("orientation3 :",String.valueOf(orientation));
//                    break;
//                case ExifInterface.ORIENTATION_NORMAL:
//                default:
//                    rotateImage = realImage;
//                    Log.d("orientation4 :",String.valueOf(orientation));
//            }


//            Which kind of reference will be used to recover the Bitmap responseData after being clear, when it will be used in the future

            return gambar.getPath();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            loading.dismiss();
            Intent intent = new Intent();
            intent.putExtra("responseData", path);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            loading.dismiss();
            loading = new ProgressDialog(CameraActivity.this);
            loading.setMessage("Please Wait..");
            loading.setCancelable(false);
            loading.show();
            /*;
            loading = ProgressDialog.show(CameraActivity.this, "", "Please Wait..", false, false);*/
            releaseCameraAndPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseCameraAndPreview();
    }


    private Bitmap adjustedContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (currentCameraId  == Camera.CameraInfo.CAMERA_FACING_FRONT){
//        lockScreenRotation(currentCameraId);
//        }
//        else {
//            lockScreenRotation(currentCameraId);
//        }
//
//    }
//
//    private void lockScreenRotation(int orientation)
//    {
//        // Stop the screen orientation changing during an event
//        if (orientation ==1) {
//            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        else
//            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//    }
    }

    public Bitmap BITMAP_RESIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
}