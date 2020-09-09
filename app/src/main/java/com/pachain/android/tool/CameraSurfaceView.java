package com.pachain.android.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.pachain.android.util.FileUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {
    private static final String TAG = "CameraSurfaceView";

    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;
    private CameraTopRectView topView;

    private OnPathChangedListener onPathChangedListener;

    public String path;
    public String fileName;

    public OnPathChangedListener getOnPathChangedListener() {
        return onPathChangedListener;
    }

    public void setOnPathChangedListener(OnPathChangedListener onPathChangedListener) {
        this.onPathChangedListener = onPathChangedListener;
    }

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
        topView = new CameraTopRectView(context, attrs);

        initView();
    }

    //Get the phone screen size
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

    }

    private void initView() {
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            mCamera = Camera.open();
            try {
                //Set the camera screen to display on the surface
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Stop preview
        mCamera.stopPreview();
        //Release camera resources
        mCamera.release();
        mCamera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            System.out.println(success);
        }
    }


    private void setCameraParams(Camera camera, int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        //Get a list of Picture Sizes supported by the camera
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        //Select the appropriate resolution from the list
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }

        //Reset the SurfaceView size according to the selected Picture Size
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        //Get the Preview Sizes supported by the camera
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        //Set photo quality
        parameters.setJpegQuality(100);
        if (parameters.getSupportedFocusModes().contains(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            //Continuous focus mode
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //Auto Focus
        mCamera.cancelAutoFocus();
        //Set the direction of Preview Display
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

    }

    /**
     * Select the appropriate resolution from the list
     * Default w:h = 4:3
     * w: height
     * h: width
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                //Default w:h = 4:3
                if (curRatio == 4f / 3) {
                    result = size;
                    break;
                }
            }
        }

        return result;
    }


    //Called when taking a picture
    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
        }
    };

    //Get uncompressed picture data
    private Camera.PictureCallback raw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
        }
    };

    //Create jpeg picture callback data object
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        private Bitmap bitmap;

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            topView.draw(new Canvas());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bm = null;
            if (data != null) {

            }

            try {
                //Get picture
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);

                //Rotate picture
                Matrix m = new Matrix();
                int height = bm.getHeight();
                int width = bm.getWidth();
                m.setRotate(90);
                bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, true);

                //Intercept picture
                Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap, topView.getViewWidth(), topView.getViewHeight(), true);
                bm = Bitmap.createBitmap(sizeBitmap, topView.getRectLeft(),
                        topView.getRectTop(),
                        topView.getRectRight() - topView.getRectLeft(),
                        topView.getRectBottom() - topView.getRectTop());

                //Compress picture
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                byte[] buffer = bos.toByteArray();
                FileUtil fileUtil = new FileUtil(mContext);
                fileUtil.writeImageFromByte(path, fileName + ".jpg", buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.flush();
                    bos.close();
                    bm.recycle();
                    mCamera.stopPreview();
                    mCamera.startPreview();
                    if (onPathChangedListener != null) {
                        onPathChangedListener.onValueChange(path, fileName + ".jpg");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public void takePicture() {
        //Set parameters and take photos
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        //When the camera.takePicture method is called, the camera closes the preview, then you need to call startPreview() to restart the preview
        mCamera.takePicture(null, null, jpeg);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setAutoFocus() {
        mCamera.autoFocus(this);
    }


    public interface OnPathChangedListener {
        void onValueChange(String path, String fileName);
    }
}
