package com.biometric.manager.interfaces;

import android.os.CancellationSignal;
import androidx.annotation.NonNull;

public interface IBiometricPrompt {
    void authenticate(@NonNull CancellationSignal cancel);
}
