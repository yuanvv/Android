package com.biometric.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CipherHelper {

    private static CipherHelper instance;

    private static final String DEFAULT_KEY_NAME = "defaultKey";

    private static final String KEYSTORE_ALIAS = "keyStoreAlias";

    private static final String HAS_FINGER_KEY = "hasFingerKey";

    private KeyGenerator mKeyGenerator;

    private KeyStore keyStore;

    private CipherHelper() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CipherHelper getInstance() {
        if (instance == null) {
            synchronized (CipherHelper.class) {
                if (instance == null) {
                    instance = new CipherHelper();
                }
            }
        }
        return instance;
    }

    public Cipher createCipher() {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean initCipher(Cipher cipher) {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
            if (cipher == null) {
                cipher = createCipher();
            }
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return false;
        } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e) {
            return true;
        } catch (KeyStoreException | CertificateException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
//            throw new RuntimeException("Failed to init Cipher", e);
            e.printStackTrace();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    void createKey(Context context, boolean createNewKey) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_KEY_NAME, Context.MODE_PRIVATE);
        try {
            if (TextUtils.isEmpty(sharedPreferences.getString(HAS_FINGER_KEY, "")) || createNewKey) {
                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setInvalidatedByBiometricEnrollment(true);
                }
                mKeyGenerator.init(builder.build());
                mKeyGenerator.generateKey();
                sharedPreferences.edit().putString(HAS_FINGER_KEY, "KEY").apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}