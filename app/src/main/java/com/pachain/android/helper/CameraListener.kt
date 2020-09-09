package com.pachain.android.helper

import android.hardware.camera2.CameraDevice
import android.util.Size
import java.lang.Exception

interface CameraListener {
    fun onCameraOpened(cameraDevice: CameraDevice, cameraId: String, previewSize: Size,
                       displayOrientation: Int, isMirror: Boolean)

    fun onPreview(byteArray: ByteArray)

    fun onCameraClosed()

    fun onCameraError(e: Exception)
}