package com.bagus.purchasingapp_mtn.utils.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by admin on 2/21/2018.
 */

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera.Parameters mParams;
    Camera.Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;
    Camera mCamera;
    private boolean meteringAreaSupported;
    private int focusAreaSize;
    private Matrix matrix;
    WindowManager wm;
    Display display;
    Context mContext;
    private static final int FOCUS_AREA_SIZE = 300;


    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, SurfaceView sv) {
        super(context);
        mContext = context;

        mSurfaceView = sv;
//        addView(mSurfaceView);
        wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        Rect newRect = new Rect(0, 0, 0, 0);
        Camera.Area focusArea = new Camera.Area(newRect, 1000);
        List<Camera.Area> focusAreas = new ArrayList<>();
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPictureSizes();
            requestLayout();
            Camera.Parameters params = mCamera.getParameters();
            mParams = mCamera.getParameters();


// Check what resolutions are supported by your camera
            List<Camera.Size> sizes = params.getSupportedPictureSizes();

// Iterate through all available resolutions and choose one.
// The chosen resolution will be stored in mSize.
//            Size mSize;
//            for (Size size : sizes) {
//                Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
//            }
//
//            Log.i(TAG, "Chosen resolution: "+ mSize.width+" "+mSize.height);
//            params.setPictureSize(mSize.width, mSize.height);
//            mCamera.setParameters(params);

            // get Camera parameters

            List<String> focusModes = params.getSupportedFocusModes();
            List<String> steadyModes = params.getSupportedSceneModes();
//            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//                // set the focus au                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                // set Camera parameters
//                mCamera.setParameters(params);
//            }
            if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }

            if (steadyModes != null && steadyModes.contains(Camera.Parameters.SCENE_MODE_STEADYPHOTO)) {
                params.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
            }

            mCamera.setParameters(params);

            /*if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO) && steadyModes.contains(Camera.Parameters.SCENE_MODE_STEADYPHOTO)) {
                Log.d("Checking Camera", "true");
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
//                params.setFocusAreas(focusAreas);
//                params.setPictureSize(1024 , 768);
                mCamera.setParameters(params);
            }*/

//            else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
//            {
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//                mCamera.setParameters(params);
//            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels;
//        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {

            mPreviewSize = getBestPreviewSize(mParams, width, height);
        }
        float ratio;
        if (mPreviewSize.height >= mPreviewSize.width) {
            ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
        } else {
            ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;
        }
        float camHeight = (int) width * ratio;
        float newCamHeight, newHeightRatio;

        if (camHeight < height) {
            newHeightRatio = (float) height / (float) mPreviewSize.height;
            newCamHeight = (newHeightRatio * camHeight);
            setMeasuredDimension((int) (width * newHeightRatio), (int) newCamHeight);
        } else {
            newCamHeight = camHeight;
            setMeasuredDimension(width, (int) newCamHeight);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public Camera.Size maxSize() {

//    	heightmax =0;
//    	widthmax =0;
        Camera.Size sizeMax = mSupportedPreviewSizes.get(0);
        //long totalsize = heightmax*widthmax;
        //long maxsize=mSupportedPreviewSizes.get(0).height*mSupportedPreviewSizes.get(0).width;

        for (Camera.Size size : mSupportedPreviewSizes) {
            if (size.height * size.width > sizeMax.width * sizeMax.height) {
                sizeMax = size;

            }
        }

        return sizeMax;
//    	for(int i = 0;i<mSupportedPreviewSizes.size();i++){
//    		if(maxsize>totalsize){
//    			heightmax = mSupportedPreviewSizes.get(i).height;
//    			widthmax = mSupportedPreviewSizes.get(i).width;
//    			totalsize=maxsize;
//    		}
//    	}


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 1, 0,
                        (width + scaledChildWidth) / 1, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 1,
                        width, (height + scaledChildHeight) / 1);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                Camera.Parameters cameraParameters = mCamera.getParameters();
                mParams = mCamera.getParameters();
                List<Camera.Size> previewSizes = cameraParameters.getSupportedPreviewSizes();
                Camera.Size optimalPreviewSize = getBestPreviewSize(cameraParameters, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
//                Camera.Size optimalPreviewSize = getOptimalPreviewSize(previewSizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                cameraParameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                mParams.setPictureSize(optimalPreviewSize.width, optimalPreviewSize.height);
                mCamera.setParameters(cameraParameters);
//                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        Log.d("TEST Surface Destroy", "");
        if (mCamera != null) {
//            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

        }
    }


    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null) {

            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                Log.i(TAG, "fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                mCamera.setParameters(parameters);
                mCamera.autoFocus(mnAutoFocusCallback);
            } else {
                mCamera.autoFocus(mnAutoFocusCallback);
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    Camera.AutoFocusCallback mnAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {


        }
    };


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera != null) {
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
//            parameters.setPreviewSize(mPreviewSize.width,mPreviewSize.height);
////            setMyPreviewSize(w,h);
//            requestLayout();
//
//            mCamera.setParameters(parameters);
//            mCamera.startPreview();

            if (mHolder.getSurface() == null)//check if the surface is ready to receive camera responseData
                return;

            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                //this will happen when you are trying the camera if it's not running
            }

            //now, recreate the camera preview
            try {

                //set the focusable true
                this.setFocusable(true);
                //set the touch able true
                this.setFocusableInTouchMode(true);

                Display display = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                Camera.Parameters params = mCamera.getParameters();
                List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                Camera.Size optimalPreviewSize = getBestPreviewSize(params, w, h);
                Camera.Size optimalSize = getBestPictureSize(params, w, h);
                Log.e("Width :" + String.valueOf(optimalSize.width), "Height :" + String.valueOf(optimalSize.height));

//            params.setPreviewSize(optimalSize.width, optimalSize.height);


                if (display.getRotation() == Surface.ROTATION_0) {
//                params.setPreviewSize(optimalSize.width, optimalSize.height);

                    params.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                    Log.e("rotate", String.valueOf(display.getRotation()));

//                parameters.setPreviewSize(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                    mCamera.setDisplayOrientation(90);
                }

                if (display.getRotation() == Surface.ROTATION_90) {
                    params.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                    Log.e("rotate", String.valueOf(display.getRotation()));
//                    mCamera.setDisplayOrientation(90);


                    //
//     parameters.setPreviewSize(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                }

                if (display.getRotation() == Surface.ROTATION_180) {
                    params.setPreviewSize(optimalPreviewSize.height, optimalPreviewSize.width);
                    Log.e("rotate", String.valueOf(display.getRotation()));
//                parameters.setPreviewSize(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                }

                if (display.getRotation() == Surface.ROTATION_270) {
                    params.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
//                parameters.setPreviewSize(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                    mCamera.setDisplayOrientation(180);
                    Log.e("rotate", String.valueOf(display.getRotation()));
                }


                //set the camera display orientation lock
//            mCamera.setDisplayOrientation(90);

//                params.setRotation(or);
                Log.e("ISO STANDARD", String.valueOf(params.get("iso-values")));
                String supportedValues = params.get("iso-values");
                if (supportedValues != null) {
                    params.set("iso", "auto");
                    Log.i(TAG, "Supported White Balance Modes:" + params.get("whitebalance-values"));
                }
                params.setPictureSize(optimalSize.width, optimalSize.height);
                mCamera.setParameters(params);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception exp) {
                Log.i("xception", "FROM surfaceChanged: " + exp.toString());
            }
        }
    }


    private void setMyPreviewSize(int width, int height) {
        // Get the set dimensions
        float newProportion = (float) width / (float) height;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);


        // Get the width of the screen
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        LayoutParams lp = mSurfaceView.getLayoutParams();
        if (newProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / newProportion);
        } else {
            lp.width = (int) (newProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);
        requestLayout();

    }

    private Camera.Size getBestPreviewSize(Camera.Parameters parameters, int width, int height) {
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

        bestSize = sizeList.get(0);

        for (int i = 0; i < sizeList.size(); i++) {
            if (sizeList.get(i).width > bestSize.width)
                bestSize = sizeList.get(i);
        }

        return bestSize;
    }


    private Camera.Size getBestPictureSize(Camera.Parameters parameters, int width, int height) {
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();

        bestSize = sizeList.get(0);

        for (int i = 0; i < sizeList.size(); i++) {
            if (sizeList.get(i).width > bestSize.width)
                bestSize = sizeList.get(i);
        }

        return bestSize;
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            Toast.makeText(mContext, "Masuk UP", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else if (event.getAction()== MotionEvent.ACTION_DOWN){
//            Toast.makeText(mContext, "Masuk Down", Toast.LENGTH_SHORT).show();
////            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
////            paint.setColor(Color.WHITE);
////            paint.setStyle(Paint.Style.STROKE);
////            final Canvas canvas = mHolder.lockCanvas();
////            Log.d("touch", "touchRecieved by camera");
////            if (canvas != null) {
////                Log.d("touch", "touchRecieved CANVAS STILL Not Null");
////                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
////                canvas.drawColor(Color.TRANSPARENT);
////                canvas.drawCircle(event.getX(), event.getY(), 100, paint);
////                mHolder.unlockCanvasAndPost(canvas);
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        Canvas canvas1 = mHolder.lockCanvas();
////                        if(canvas1 !=null){
////                            canvas1.drawColor(0, PorterDuff.Mode.CLEAR);
////                            mHolder.unlockCanvasAndPost(canvas1);
////                        }
////
////                    }
////                }, 1000);
////
////            }
////            mHolder.unlockCanvasAndPost(canvas);
//
////            focusOnTouch(event);
//        return false;
//        }
//        return true;
//    }


}