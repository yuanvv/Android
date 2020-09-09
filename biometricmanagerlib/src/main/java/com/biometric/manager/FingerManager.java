package com.biometric.manager;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.biometric.manager.dialog.BaseFingerDialog;
import com.biometric.manager.impl.BiometricPromptImpl23;
import com.biometric.manager.impl.BiometricPromptImpl28;
import com.biometric.manager.interfaces.IBiometricPrompt;

import javax.crypto.Cipher;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerManager {

    private static FingerManager fingerManager;

    private static FingerManagerBuilder mFingerManagerBuilder;

    private CancellationSignal cancellationSignal;

    private IBiometricPrompt biometricPrompt;

    public enum SupportResult {
        DEVICE_UNSUPPORTED,
        SUPPORT_WITHOUT_DATA,
        SUPPORT
    }

    private static FingerManager getInstance() {
        if (fingerManager == null) {
            synchronized (FingerManager.class) {
                if (fingerManager == null) {
                    fingerManager = new FingerManager();
                }
            }
        }
        return fingerManager;
    }

    public static FingerManager getInstance(FingerManagerBuilder fingerManagerBuilder) {
        mFingerManagerBuilder = fingerManagerBuilder;
        return getInstance();
    }

    public static SupportResult checkSupport(Context context) {
//		FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
        if (fingerprintManager.isHardwareDetected()) {
            if (fingerprintManager.hasEnrolledFingerprints()) {
                return SupportResult.SUPPORT;
            } else {
                return SupportResult.SUPPORT_WITHOUT_DATA;
            }
        } else {
            return SupportResult.DEVICE_UNSUPPORTED;
        }
    }

    public void startListener(AppCompatActivity activity) {
        createImpl(activity, mFingerManagerBuilder.getFingerDialogApi23());
        startListener();
    }

    private void createImpl(AppCompatActivity activity, BaseFingerDialog fingerDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPrompt = new BiometricPromptImpl28(activity, mFingerManagerBuilder);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            biometricPrompt = new BiometricPromptImpl23(activity, fingerDialog, mFingerManagerBuilder);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startListener() {
        if (!SharePreferenceUtil.isEnableFingerDataChange(mFingerManagerBuilder.getApplication()) && hasFingerprintChang(mFingerManagerBuilder.getApplication())) {
            updateFingerData(mFingerManagerBuilder.getApplication());
        } else {
            CipherHelper.getInstance().createKey(mFingerManagerBuilder.getApplication(), false);
        }

        if (cancellationSignal == null) {
            cancellationSignal = new CancellationSignal();
        }

        if (cancellationSignal.isCanceled()) {
            cancellationSignal = new CancellationSignal();
        }
        biometricPrompt.authenticate(cancellationSignal);
    }

    public static void updateFingerData(Context context) {
        CipherHelper.getInstance().createKey(context, true);
        SharePreferenceUtil.saveEnableFingerDataChange(context, false);
        SharePreferenceUtil.saveFingerDataChange(context, false);
    }

    public static boolean hasFingerprintChang(Context context) {
        if (SharePreferenceUtil.isFingerDataChange(context)) {
            return true;
        }
        CipherHelper.getInstance().createKey(context, false);
        Cipher cipher = CipherHelper.getInstance().createCipher();
        return CipherHelper.getInstance().initCipher(cipher);
    }

    public static boolean isHardwareDetected(Context context) {
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        return fingerprintManagerCompat.isHardwareDetected();
    }

    public static boolean hasFingerprintData(Context context) {
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        return fingerprintManagerCompat.hasEnrolledFingerprints();
    }

    public static FingerManagerBuilder build() {
        return new FingerManagerBuilder();
    }
}
