package com.biometric.manager.impl;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.biometric.manager.CipherHelper;
import com.biometric.manager.FingerManagerBuilder;
import com.biometric.manager.SharePreferenceUtil;
import com.biometric.manager.dialog.BaseFingerDialog;
import com.biometric.manager.dialog.DefaultFingerDialog;
import com.biometric.manager.interfaces.IBiometricPrompt;
import com.biometric.manager.interfaces.IFingerCallback;

import javax.crypto.Cipher;

/**
 * Android 6.0 and above fingerprint authentication realization
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class BiometricPromptImpl23 implements IBiometricPrompt {

    private AppCompatActivity mActivity;

    private Cipher mCipher;

    private boolean mSelfCanceled;

    private BaseFingerDialog mFingerDialog;

    private IFingerCallback mFingerCallback;

    private static final String SECRET_MESSAGE = "Very secret message";

    public BiometricPromptImpl23(AppCompatActivity activity, BaseFingerDialog fingerDialog,
                                 FingerManagerBuilder fingerManagerController) {
        this.mActivity = activity;
        this.mCipher = CipherHelper.getInstance().createCipher();
        this.mFingerCallback = fingerManagerController.getFingerCallback();
        this.mFingerDialog = fingerDialog == null ? DefaultFingerDialog.newInstance(fingerManagerController) : fingerDialog;
    }

    /**
     * Start fingerprint authentication
     *
     * @param cancel
     */
    @Override
    public void authenticate(@NonNull final CancellationSignal cancel) {
        mSelfCanceled = false;
        //Check whether the fingerprint library has changed
        boolean exceptionState = CipherHelper.getInstance().initCipher(mCipher);
        boolean flag = SharePreferenceUtil.isEnableFingerDataChange(mActivity) && (exceptionState || SharePreferenceUtil.isFingerDataChange(mActivity));
        if (flag) {
            SharePreferenceUtil.saveFingerDataChange(mActivity, true);
            mFingerCallback.onChange();
            return;
        }

        mFingerDialog.setOnDismissListener(new BaseFingerDialog.IDismissListener() {

            @Override
            public void onDismiss() {
                mSelfCanceled = !cancel.isCanceled();
                if (mSelfCanceled) {
                    cancel.cancel();
                    if (mFingerDialog.getClass() == DefaultFingerDialog.class) {
                        mFingerCallback.onCancel();
                    }
                }
            }
        });
        //Below Android 9.0
        if (!mFingerDialog.isAdded()) {
            mFingerDialog.show(mActivity.getSupportFragmentManager(), mFingerDialog.getClass().getSimpleName());
        }
        //Start fingerprint authentication
        FingerprintManager fingerprintManager = (FingerprintManager) mActivity.getSystemService(FingerprintManager.class);
        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(mCipher), cancel, 0,
                new FingerprintManager.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        //If fingerprint authentication fails five times, an error will be reported
                        cancel.cancel();
                        if (!mSelfCanceled) {
                            mFingerDialog.onError(errString.toString());
                            mFingerCallback.onError(errString.toString());
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        mFingerDialog.onHelp(helpString.toString());
                        mFingerCallback.onHelp(helpString.toString());
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Cipher cipher = result.getCryptoObject().getCipher();
                        if (cipher != null) {
                            try {
                                if (SharePreferenceUtil.isEnableFingerDataChange(mActivity)) {
                                    cipher.doFinal(SECRET_MESSAGE.getBytes());
                                }

                                cancel.cancel();
                                mFingerDialog.onSucceed();
                                mFingerCallback.onSucceed();
                                SharePreferenceUtil.saveEnableFingerDataChange(mActivity, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                                cancel.cancel();
                                mFingerDialog.onError("");

                                SharePreferenceUtil.saveFingerDataChange(mActivity, true);
                                mFingerCallback.onChange();
                            }
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        mFingerDialog.onFailed();
                        mFingerCallback.onFailed();
                    }
                }, null);

    }
}
