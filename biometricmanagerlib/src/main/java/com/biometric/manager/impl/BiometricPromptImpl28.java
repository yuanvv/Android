package com.biometric.manager.impl;

import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.biometric.manager.CipherHelper;
import com.biometric.manager.FingerManagerBuilder;
import com.biometric.manager.SharePreferenceUtil;
import com.biometric.manager.interfaces.IBiometricPrompt;
import com.biometric.manager.interfaces.IFingerCallback;

import javax.crypto.Cipher;

/**
 * Android 9.0 and above
 */
@RequiresApi(Build.VERSION_CODES.P)
public class BiometricPromptImpl28 implements IBiometricPrompt {

    private AppCompatActivity mActivity;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCanceled;
    private Cipher cipher;
    private IFingerCallback mFingerCallback;
    private BiometricPrompt mBiometricPrompt;
    private static final String SECRET_MESSAGE = "Very secret message";

    @RequiresApi(Build.VERSION_CODES.P)
    public BiometricPromptImpl28(AppCompatActivity activity, FingerManagerBuilder fingerManagerController) {
        this.mActivity = activity;
        this.cipher = CipherHelper.getInstance().createCipher();
        this.mFingerCallback = fingerManagerController.getFingerCallback();
        this.mBiometricPrompt = new BiometricPrompt
                .Builder(activity)
                .setTitle(fingerManagerController.getTitle())
                .setDescription(fingerManagerController.getDes())
                .setNegativeButton(fingerManagerController.getNegativeText(),
                        activity.getMainExecutor(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mSelfCanceled = true;
                                mFingerCallback.onCancel();
                                mCancellationSignal.cancel();
                            }
                        })
                .build();
    }

    /**
     * Start fingerprint authentication
     *
     * @param cancel
     */
    @RequiresApi(Build.VERSION_CODES.P)
    @Override
    public void authenticate(@Nullable final CancellationSignal cancel) {
        mSelfCanceled = false;
        mCancellationSignal = cancel;
        //Check whether the fingerprint library has changed
        boolean exceptionState = CipherHelper.getInstance().initCipher(cipher);
        boolean flag = SharePreferenceUtil.isEnableFingerDataChange(mActivity) && (exceptionState || SharePreferenceUtil.isFingerDataChange(mActivity));
        if (flag) {
            SharePreferenceUtil.saveFingerDataChange(mActivity, true);
            mFingerCallback.onChange();
            return;
        }
        //Start fingerprint authentication
        mBiometricPrompt.authenticate(new BiometricPrompt.CryptoObject(cipher),
                cancel, mActivity.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        //If fingerprint authentication fails five times, an error will be reported
                        cancel.cancel();
                        if (!mSelfCanceled) {
                            mFingerCallback.onError(errString.toString());
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        mFingerCallback.onHelp(helpString.toString());
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Cipher cipher = result.getCryptoObject().getCipher();
                        if (cipher != null) {
                            try {
                                if (SharePreferenceUtil.isEnableFingerDataChange(mActivity)) {
                                    cipher.doFinal(SECRET_MESSAGE.getBytes());
                                }
                                cancel.cancel();
                                mFingerCallback.onSucceed();
                                SharePreferenceUtil.saveEnableFingerDataChange(mActivity, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                                cancel.cancel();

                                SharePreferenceUtil.saveFingerDataChange(mActivity, true);
                                mFingerCallback.onChange();

                            }
                        }

                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        mFingerCallback.onFailed();
                    }
                });
    }

}
