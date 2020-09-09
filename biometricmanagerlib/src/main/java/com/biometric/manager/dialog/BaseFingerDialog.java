package com.biometric.manager.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;

/**
 * Android 6.0 to 9.0
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class BaseFingerDialog extends DialogFragment {

    private IDismissListener mDismissListener;

    public BaseFingerDialog() {
        super();
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                } else {
                    return false;
                }
            }
        });
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dismissAllowingStateLoss();
    }

    public void setOnDismissListener(IDismissListener dismissListener) {
        this.mDismissListener = dismissListener;
    }

    public interface IDismissListener {
        void onDismiss();
    }

    /**
     * Fingerprint recognition fails many times and cannot continue to verify
     */
    public abstract void onError(String error);

    /**
     * The fingerprint recognition is wrong, it will prompt, do not move your fingers in a large area, you can continue to verify
     */
    public abstract void onHelp(String help);

    /**
     * Call back when recognition is successful
     */
    public abstract void onSucceed();

    /**
     * Call back when the recognized finger is not registered and does not match, but you can continue to verify
     */
    public abstract void onFailed();

}
