package com.pachain.android.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap

class PermissionUtils private constructor() {

    private enum class STATE {
        GRANTED,
        DENIED,
        ALWAYS_DENIED
    }

    companion object {

        private const val TAG = "PermissionUtils"

        @SuppressLint("StaticFieldLeak")
        private var mInstance: PermissionUtils? = null

        const val CODE_READ_STORAGE = 0x01
        const val CODE_WRITE_STORAGE = 0X02
        const val CODE_CAMERA = 0x03
        const val CODE_LOCATION_FINE = 0X04
        const val CODE_LOCATION_COARSE = 0x05
        const val CODE_READ_PHONE_STATE = 0x06
        const val CODE_MULTI = 0x0f

        fun getInstance(): PermissionUtils {
            if (mInstance == null) {
                synchronized(PermissionUtils::class.java) {
                    mInstance = PermissionUtils()
                }
            }
            checkNotNull(mInstance) { "instance should not be null" }
            return mInstance!!
        }

        fun isGranted(permission: String, context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private lateinit var mActivity: Activity
    private var mRequestCode = -1
    private var mCallback: PermissionCallback? = null
    private lateinit var mPermissions: Array<out String>

    private val mPermissionStatusList = ConcurrentHashMap<String, STATE>()

    private var mDialog: Dialog? = null

    fun destroy() {
        if (mDialog != null) {
            mDialog?.isShowing ?: mDialog?.dismiss()
            mDialog = null
        }
        if (mInstance != null) {
            mInstance = null
        }
    }

    fun with(_object: Any): PermissionUtils {
        if (_object is Activity) {
            mActivity = _object
            return this
        }
        if (_object is Fragment) {
            checkNotNull(_object.activity) { "fragment activity is null, cannot init permissionUtils" }
            mActivity = _object.activity!!
            return this
        }
        throw IllegalArgumentException("_object is not fragment or activity, cannot init permissionUtils")
    }

    fun requestCode(requestCode: Int): PermissionUtils {
        require(requestCode >= 0) { "request code should be unless as 0" }
        this.mRequestCode = requestCode
        return this
    }

    fun permissions(vararg permissions: String): PermissionUtils {
        this.mPermissions = permissions
        return this
    }

    fun request(callback: PermissionCallback) {
        this.mCallback = callback
        if (Build.VERSION.SDK_INT >= 23) {
            var isAllGranted = true
            mPermissions.forEach {
                if (ContextCompat.checkSelfPermission(mActivity, it) == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, it)) {
                        if (mPermissionStatusList[it] == null) {
                            mPermissionStatusList[it] = STATE.DENIED
                        } else {
                            mPermissionStatusList[it] = STATE.ALWAYS_DENIED
                        }
                    } else {
                        mPermissionStatusList[it] = STATE.DENIED
                    }
                } else {
                    mPermissionStatusList[it] = STATE.GRANTED
                }
            }
            if (isAllGranted) {
                Log.e(TAG, "request isAllGranted.")
                callback.granted()
                return
            }
            if (mPermissions.isNotEmpty()) {
                Log.e(TAG, "ActivityCompat.requestPermissions======")
                ActivityCompat.requestPermissions(mActivity, mPermissions, mRequestCode)
            }
        } else {
            mCallback?.granted()
        }
    }

    fun onRequestPermissionResult(requestCode: Int,
                                  permissions: Array<out String>,
                                  grantResults: IntArray) {
        Log.e(TAG, "onRequestPermissionResult")
        if (requestCode == CODE_MULTI) {
            onRequestMultiPermissionResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPermissionStatusList[permissions[0]] = STATE.GRANTED
            mCallback?.granted()
        } else {
            mCallback?.denied()
        }
    }

    private fun onRequestMultiPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.e(TAG, "onRequestMultiPermissionResult")
        var isAllGranted = true
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                mPermissionStatusList[permission] = STATE.GRANTED
            } else {
                isAllGranted = false
            }
        }
        if (isAllGranted) {
            mCallback?.granted()
        } else {
            mCallback?.denied()
        }
    }

    fun showDialog() {
        mActivity ?: return
        if (mDialog == null) {
            mDialog = AlertDialog.Builder(mActivity)
                    .setMessage("You have set it to no longer prompt permission application, it will affect the use of the app, go to the \"Settings\" page to allow it")
                    .setPositiveButton("Go") { dialog, _ ->
                        dialog.dismiss()
                        goToSetting()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
        }
        mDialog?.show()
    }

    private fun goToSetting() {
        Log.e(TAG, "goToSetting")
        val packageName = mActivity.packageName
        val toSetting = Intent()
        toSetting.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        toSetting.data = uri
        mActivity.startActivity(toSetting)
    }

    interface PermissionCallback {
        fun granted()

        fun denied()
    }
}