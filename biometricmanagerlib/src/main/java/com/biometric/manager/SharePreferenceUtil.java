package com.biometric.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private static final String DEFAULT_NAME = "finger";
    private static String KEY_IS_FINGER_CHANGE_ENABLE = "is_finger_change_enable";
    private static String KEY_IS_FINGER_CHANGE = "is_finger_change";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
    }

    public static void saveEnableFingerDataChange(Context context, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_IS_FINGER_CHANGE_ENABLE, value);
        editor.apply();
    }

    public static boolean isEnableFingerDataChange(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_FINGER_CHANGE_ENABLE, false);
    }

    public static void saveFingerDataChange(Context context, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_IS_FINGER_CHANGE, value);
        editor.apply();
    }

    public static boolean isFingerDataChange(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_FINGER_CHANGE, false);
    }
}
