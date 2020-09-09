package com.biometric.manager.callback;

import com.biometric.manager.interfaces.IFingerCallback;

public abstract class SimpleFingerCallback implements IFingerCallback {
    @Override
    public void onError(String error) {

    }

    @Override
    public void onHelp(String help) {

    }

    @Override
    public void onCancel() {

    }
}
