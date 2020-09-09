package com.biometric.manager;

import android.app.Application;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.biometric.manager.dialog.BaseFingerDialog;
import com.biometric.manager.interfaces.IFingerCallback;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerManagerBuilder {

    private Application mApplication;

    private String mTitle;

    private String mDes;

    private String mNegativeText;

    private BaseFingerDialog mFingerDialogApi23;

    private IFingerCallback mFingerCallback;

    public FingerManagerBuilder setApplication(Application application) {
        mApplication = application;
        return this;
    }

    public Application getApplication() {
        return mApplication;
    }

    public FingerManagerBuilder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public FingerManagerBuilder setDes(String des) {
        this.mDes = des;
        return this;
    }

    public String getDes() {
        return mDes;
    }

    public FingerManagerBuilder setNegativeText(String negativeText) {
        this.mNegativeText = negativeText;
        return this;
    }

    public String getNegativeText() {
        return mNegativeText;
    }

    public FingerManagerBuilder setFingerDialogApi23(@Nullable BaseFingerDialog fingerDialogApi23) {
        this.mFingerDialogApi23 = fingerDialogApi23;
        return this;
    }

    public BaseFingerDialog getFingerDialogApi23() {
        return mFingerDialogApi23;
    }

    public FingerManagerBuilder setFingerCallback(IFingerCallback fingerCallback) {
        this.mFingerCallback = fingerCallback;
        return this;
    }

    public IFingerCallback getFingerCallback() {
        return mFingerCallback;
    }


    public FingerManager create() {
        if (mFingerCallback == null) {
            throw new RuntimeException("CompatFingerManager : FingerCheckCallback can not be null");
        }

        return FingerManager.getInstance(this);
    }

}
