package com.pachain.android.util;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;
import androidx.annotation.RequiresApi;

public class RSAUtils {
    private Context context;
    private static String aliasName;
    private static X500Principal x500Principal;
    private static KeyStore keyStore;
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    public RSAUtils(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            aliasName = "PAChainRSA";

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            x500Principal = new X500Principal("CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> generateRSAKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Map<String, Object> keyPairMap = new HashMap<>();

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 30);

        //Default: 2048
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this.context.getApplicationContext())
                .setAlias(aliasName)
                .setSubject(x500Principal)
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(Calendar.getInstance().getTime())
                .setEndDate(endDate.getTime())
                .build();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        generator.initialize(spec);

        KeyPair keyPair = generator.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();
        keyPairMap.put("publicKey", publicKey);
        keyPairMap.put("privateKey", privateKey);
        return keyPairMap;
    }

    public boolean containsAlias() throws Exception {
        boolean contains = false;
        try{
            contains = keyStore.containsAlias(aliasName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return contains;
    }

    public void deleteKey() {
        try {
            keyStore.deleteEntry(aliasName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KeyStore.PrivateKeyEntry getPrivateKeyEntry() throws Exception {
        /*
         * Use a PrivateKey in the KeyStore to create a signature over
         * some data.
         */
        KeyStore.Entry entry = keyStore.getEntry(aliasName, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return null;
        }
        else {
            return ((KeyStore.PrivateKeyEntry) entry);
        }
    }

    public PrivateKey getPrivateKeyFromString(String base64String) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] bts = Base64.decode(base64String, Base64.NO_WRAP);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bts);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    public String signByPrivateKey(String data) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
        if (privateKeyEntry == null) {
            return null;
        }

        byte[] bts = data.getBytes();
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKeyEntry.getPrivateKey());
        signature.update(bts);
        byte[] ret = signature.sign();
        return new String(ret);
    }

    public boolean verifySignature(String data, byte[] signatureData, PublicKey publicKey) throws Exception {
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(publicKey);
        s.update(data.getBytes());
        return s.verify(signatureData);
    }

    public String encryptByPublicKey(String data) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
        if (privateKeyEntry == null) {
            return null;
        }

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
        byte[] secret = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(secret, Base64.NO_WRAP);
    }

    public String decryptByPrivateKey(String data) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
        if (privateKeyEntry == null) {
            return null;
        }

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        byte[] b = cipher.doFinal(Base64.decode(data, Base64.NO_WRAP));
        return new String(b);
    }
}
