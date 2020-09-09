package com.pachain.android

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.hardware.camera2.CameraDevice
import android.media.FaceDetector
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pachain.android.helper.CameraHelper
import com.pachain.android.helper.CameraListener
import com.pachain.android.util.FileUtil
import com.pachain.android.util.PermissionUtils
import com.pachain.android.widget.CircleTextureBorderView
import com.pachain.android.widget.RoundTextureView
import kotlin.math.min

class PAChainFaceRecognitionActivity : AppCompatActivity(), CameraListener {
    companion object {
        private const val TAG = "FaceRecognition"
        private const val CAMERA_ID = CameraHelper.CAMERA_ID_FRONT
    }

    private lateinit var mBack: TextView
    private lateinit var mTitle: TextView
    private lateinit var mTextureView: RoundTextureView
    private lateinit var mBorderView: CircleTextureBorderView
    private lateinit var mTakePhoto: TextView

    private var mCameraHelper: CameraHelper? = null

    private var mIsTakingPhoto = false

    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("pachain_activity_facerecognition", "layout", packageName))
        initViews()

        path = intent.extras?.get("path") as String

        val actionBar = supportActionBar
        actionBar?.hide()
    }

    private fun initViews() {
        mBack = findViewById(resources.getIdentifier("tv_back", "id", packageName))
        mTitle = findViewById(resources.getIdentifier("tv_title", "id", packageName))
        mTitle.text = ""
        mTextureView = findViewById(resources.getIdentifier("round_texture_view", "id", packageName))
        mBorderView = findViewById(resources.getIdentifier("border_view", "id", packageName))
        mTakePhoto = findViewById(resources.getIdentifier("tv_takePhoto", "id", packageName))

        mBorderView.setScanEnabled(true)

        mBack.setOnClickListener {
            finish()
        }

        mTakePhoto.setOnClickListener {
            mIsTakingPhoto = true
            mBorderView.setTipsText("Loading...", true)
            mCameraHelper?.takePhoto()
            mTakePhoto.isEnabled = false
        }

        mTextureView.setOnClickListener {
            mTakePhoto.isEnabled = true
            setResumePreview()
        }

        mTextureView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mTextureView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val params = mTextureView.layoutParams
                val sideLength = min(mTextureView.width, mTextureView.height * 3 / 4)
                params.width = sideLength
                params.height = sideLength
                mTextureView.layoutParams = params
                mTextureView.turnRound()
                mBorderView.setCircleTextureWidth(sideLength)
                if (PermissionUtils.isGranted(Manifest.permission.CAMERA, applicationContext)) {
                    initCamera()
                } else {
                    PermissionUtils.getInstance().with(this@PAChainFaceRecognitionActivity).permissions(Manifest.permission.CAMERA)
                            .requestCode(PermissionUtils.CODE_CAMERA)
                            .request(object : PermissionUtils.PermissionCallback {
                                override fun denied() {
                                    PermissionUtils.getInstance().showDialog()
                                }

                                override fun granted() {
                                    initCamera()
                                }
                            })
                }
            }
        })
    }

    fun setResumePreview() {
        this.mIsTakingPhoto = false
        //switchText("Please click the button to take a photo")
        mCameraHelper?.stop()
        mCameraHelper?.start()
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "main currentThread = ${Thread.currentThread().name}")
        if (!mIsTakingPhoto) {
            mCameraHelper?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!mIsTakingPhoto) {
            mCameraHelper?.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCameraHelper?.release()
        PermissionUtils.getInstance().destroy()
        mBorderView.stop()
    }

    private fun initCamera() {
        mTextureView ?: return

        mCameraHelper = CameraHelper.Companion.Builder()
                .cameraListener(this)
                .specificCameraId(CAMERA_ID)
                .mContext(applicationContext)
                .previewOn(mTextureView)
                .previewViewSize(
                        Point(
                                mTextureView.layoutParams.width,
                                mTextureView.layoutParams.height
                        )
                )
                .rotation(windowManager?.defaultDisplay?.rotation ?: 0)
                .build()
        mCameraHelper?.start()
        //switchText("Please click the button to take a photo")
    }

    override fun onCameraClosed() {

    }

    override fun onCameraError(e: Exception) {

    }

    override fun onCameraOpened(
            cameraDevice: CameraDevice,
            cameraId: String,
            previewSize: Size,
            displayOrientation: Int,
            isMirror: Boolean
    ) {
        Log.i(TAG, "onCameraOpened:  previewSize = ${previewSize.width}  x  ${previewSize.height}")
        //When the camera is turned on, add the view in the upper right corner to display the original data and preview data
        runOnUiThread {
            //Keep the preview control and preview size ratio the same to avoid stretching
            val params = mTextureView.layoutParams
            //Horizontal screen
            if (displayOrientation % 180 == 0) {
                params.height = params.width * previewSize.height / previewSize.width
            }
            //Portrait screen
            else {
                params.height = params.width * previewSize.width / previewSize.height
            }
            mTextureView.layoutParams = params
        }

    }

    override fun onPreview(byteArray: ByteArray) {
        Log.i(TAG, "onPreview: ")
        runOnUiThread {
            switchText("Loading..", true)
        }

        //Intercept
        //Bitmap result = Bitmap.createBitmap(cacheBitmap, startX, startY, 480, height);

        var bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size);
        bm = bm.copy(Bitmap.Config.RGB_565, true)
        val faceDetector = FaceDetector(bm.width, bm.height, 1)
        val face = arrayOfNulls<FaceDetector.Face>(1)
        val faces = faceDetector.findFaces(bm, face)
        if (faces > 0) {
            //switchText("Success", true);

            val fileUtil = FileUtil(this@PAChainFaceRecognitionActivity)
            fileUtil.writeImageFromByte(path, "FaceRecognition.jpg", byteArray)

            val intent = Intent()
            intent.putExtra("path", path)
            intent.putExtra("fileName", "FaceRecognition.jpg")
            intent.putExtra("type", "faceRecognition")
            setResult(0, intent)
            finish()
        } else {
            switchText("Failed", true);
        }
    }

    private fun switchText(shadowContent: String, stopAnim: Boolean = false) {
        runOnUiThread {
            if (shadowContent.isNotEmpty()) {
                mBorderView.setTipsText(shadowContent, stopAnim)
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.getInstance().with(this).onRequestPermissionResult(requestCode, permissions, grantResults)
    }
}