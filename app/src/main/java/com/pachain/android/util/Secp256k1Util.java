package com.pachain.android.util;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.ECPointUtil;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import androidx.annotation.RequiresApi;

public class Secp256k1Util {
    private static String aliasName;
    private static KeyStore keyStore;
    private Context context;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public Secp256k1Util(Context context){
        this.context = context.getApplicationContext();
        init();
    }

    private void init() {
        try {
            aliasName = "PAChainEC";

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getKeyPair() {
        Map<String, Object> ecKey = new HashMap<>();
        SDCardUtils sdCardUtils = new SDCardUtils(context);
        RSAUtils rsaUtils = new RSAUtils(context);
        Map<String, Object> rsaKeyPair = new HashMap<>();
        String sdKeyPath = "KeyStore/";
        String sdPublicPEMName = "publickey.pem";
        String sdPrivatePEMName = "privatekey.pem";
        try {
            if (!sdCardUtils.isFileExist(sdKeyPath + sdPrivatePEMName) && !SPUtils.contains(context, "privateKey")) {
                if (!rsaUtils.containsAlias()) {
                    rsaKeyPair = rsaUtils.generateRSAKeyPair();
                }
            } else {
                if (rsaUtils.containsAlias()) {
                    rsaKeyPair.put("publicKey", rsaUtils.getPublicKey());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (rsaKeyPair != null && rsaKeyPair.size() > 0) {
                if (sdCardUtils.isFileExist(sdKeyPath + sdPrivatePEMName)) {
                    String privateStr = sdCardUtils.readPEMFileFromSD(sdKeyPath, sdPrivatePEMName);
                    String publicStr = sdCardUtils.readPEMFileFromSD(sdKeyPath, sdPublicPEMName);
                    ecKey.put("publicKey", getPublicKeyFromString(publicStr));
                    ecKey.put("privateKey", getPrivateKeyFromString(rsaUtils.decryptByPrivateKey(privateStr)));
                } else if (SPUtils.contains(context, "privateKey")) {
                    ecKey.put("publicKey", getPublicKeyFromString(SPUtils.getString(context, "publicKey", "")));
                    ecKey.put("privateKey", getPrivateKeyFromString(rsaUtils.decryptByPrivateKey(SPUtils.getString(context, "privateKey", ""))));

                    if (sdCardUtils.isSDCardEnableByEnvironment()) {
                        sdCardUtils.saveKeyAsPEM(Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP), sdKeyPath, sdPublicPEMName);
                        sdCardUtils.saveKeyAsPEM(rsaUtils.encryptByPublicKey(Base64.encodeToString(((PrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP)), sdKeyPath, sdPrivatePEMName);

                        SPUtils.remove(context, "publicKey");
                        SPUtils.remove(context, "privateKey");
                    }
                } else {
                    ecKey = generateSECP256K1Keypair();

                    if (sdCardUtils.isSDCardEnableByEnvironment()) {
                        sdCardUtils.saveKeyAsPEM(Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP), sdKeyPath, sdPublicPEMName);
                        sdCardUtils.saveKeyAsPEM(rsaUtils.encryptByPublicKey(Base64.encodeToString(((PrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP)), sdKeyPath, sdPrivatePEMName);
                    } else {
                        SPUtils.put(context, "publicKey", Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP));
                        SPUtils.put(context, "privateKey", rsaUtils.encryptByPublicKey(Base64.encodeToString(((PrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP)));
                    }
                }
            } else {
                if (sdCardUtils.isFileExist(sdKeyPath + sdPrivatePEMName)) {
                    String privateStr = sdCardUtils.readPEMFileFromSD(sdKeyPath, sdPrivatePEMName);
                    String publicStr = sdCardUtils.readPEMFileFromSD(sdKeyPath, sdPublicPEMName);
                    ecKey.put("publicKey", getPublicKeyFromString(publicStr));
                    ecKey.put("privateKey", getPrivateKeyFromString(privateStr));
                } else if (SPUtils.contains(context, "privateKey")) {
                    ecKey.put("publicKey", getPublicKeyFromString(SPUtils.getString(context, "publicKey", "")));
                    ecKey.put("privateKey", getPrivateKeyFromString(SPUtils.getString(context, "privateKey", "")));

                    if (sdCardUtils.isSDCardEnableByEnvironment()) {
                        sdCardUtils.saveKeyAsPEM(Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP), sdKeyPath, sdPublicPEMName);
                        sdCardUtils.saveKeyAsPEM(Base64.encodeToString(((PrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP), sdKeyPath, sdPrivatePEMName);

                        SPUtils.remove(context, "publicKey");
                        SPUtils.remove(context, "privateKey");
                    }
                } else {
                    ecKey = generateSECP256K1Keypair();

                    if (sdCardUtils.isSDCardEnableByEnvironment()) {
                        sdCardUtils.saveKeyAsPEM(Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP), sdKeyPath, sdPublicPEMName);
                        sdCardUtils.saveKeyAsPEM(Base64.encodeToString(((PrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP), sdKeyPath, sdPrivatePEMName);
                    } else {
                        SPUtils.put(context, "publicKey", Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP));
                        SPUtils.put(context, "privateKey", Base64.encodeToString(((PrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ecKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Map<String, Object> generateECKeyPairKeystore() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Map<String, Object> keyPairMap = new HashMap<>();

        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 30);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        kpg.initialize(new KeyGenParameterSpec.Builder(
                aliasName, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY | KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256k1"))
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512)
                .setKeyValidityStart(Calendar.getInstance().getTime())
                .setKeyValidityEnd(end.getTime())
                //.setUserAuthenticationRequired(true)
                //.setInvalidatedByBiometricEnrollment(false)
                .build());

        KeyPair keyPair = kpg.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();
        keyPairMap.put("publicKey", publicKey);
        keyPairMap.put("privateKey", privateKey);
        return keyPairMap;
    }

    public Map<String, Object> generateSECP256K1Keypair() throws Exception {
        Map<String, Object> keyPairMap = new HashMap<>();

        KeyPairGenerator keypairGen = KeyPairGenerator.getInstance("ECDSA");
        ECGenParameterSpec spec = new ECGenParameterSpec("secp256k1");
        keypairGen.initialize(spec);
        KeyPair keyPair = keypairGen.genKeyPair();

        keyPairMap.put("publicKey", keyPair.getPublic());
        keyPairMap.put("privateKey", keyPair.getPrivate());
        return keyPairMap;
    }

    public ECPublicKey getPublicKeyFromString(String pubStr) throws Exception {
        byte[] keyBytes = Base64.decode(pubStr, Base64.NO_WRAP);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPublicKey publicKey = (ECPublicKey) keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public ECPublicKey getPublicKeyFromPEM(String pubStr) throws Exception {
        byte[] key = pubStr.getBytes("UTF8");
        PemObject pemObject;
        try (PemReader pemReader = new PemReader(new InputStreamReader(new ByteArrayInputStream(key)))) {
            pemObject = pemReader.readPemObject();
        }
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pemObject.getContent());
        KeyFactory factory = KeyFactory.getInstance("EC");
        ECPublicKey publicKey = (ECPublicKey) factory.generatePublic(pubKeySpec);
        return publicKey;
    }

    public ECPrivateKey getPrivateKeyFromPEM(String privateStr) throws Exception {
        byte[] key = privateStr.getBytes("UTF8");
        PemObject pemObject;
        try (PemReader pemReader = new PemReader(new InputStreamReader(new ByteArrayInputStream(key)))) {
            pemObject = pemReader.readPemObject();
        }
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public ECPrivateKey getPrivateKeyFromString(String priStr) throws Exception{
        byte[] keyBytes = Base64.decode(priStr, Base64.NO_WRAP);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
        return privateKey;
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

    public static KeyStore.PrivateKeyEntry getPrivateKeyEntry() throws Exception {
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

    public static PublicKey getPublicKey() throws Exception {
        if (keyStore.containsAlias(aliasName)) {
            return keyStore.getCertificate(aliasName).getPublicKey();
        } else {
            return null;
        }
    }

    public Enumeration<String> getAliases() throws Exception {
        try {
            return keyStore.aliases();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteKey() {
        try {
            keyStore.deleteEntry(aliasName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublicKey generateOriginalPublicKey(ECPrivateKey privateKey) throws Exception {
        // Generate public key from private key
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        org.spongycastle.math.ec.ECPoint Q = ecSpec.getG().multiply(privateKey.getS());
        byte[] publicDerBytes = Q.getEncoded(false);

        org.spongycastle.math.ec.ECPoint point = ecSpec.getCurve().decodePoint(publicDerBytes);
        org.spongycastle.jce.spec.ECPublicKeySpec publicKeySpec = new org.spongycastle.jce.spec.ECPublicKeySpec(point, ecSpec);
        return (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
    }

    //Not available
    public PublicKey generateNewPublicKey(ECPrivateKey privateKey) throws Exception {
        // Generate public key from private key
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        org.spongycastle.math.ec.ECPoint Q = ecSpec.getG().multiply(new BigInteger(1, privateKey.getEncoded()));
        byte[] publicDerBytes = Q.getEncoded(false);

        org.spongycastle.math.ec.ECPoint point = ecSpec.getCurve().decodePoint(publicDerBytes);
        org.spongycastle.jce.spec.ECPublicKeySpec publicKeySpec = new org.spongycastle.jce.spec.ECPublicKeySpec(point, ecSpec);
        return (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
    }

    public static PublicKey createSharedPubKey(PrivateKey privateKey) throws GeneralSecurityException {
        BigInteger privKey = new BigInteger(System.currentTimeMillis() + bytesToHex(privateKey.getEncoded()) + "", 16);
        X9ECParameters ecp = SECNamedCurves.getByName("secp256k1");
        ECPoint curvePt = ecp.getG().multiply(privKey);
        BigInteger x = curvePt.getX().toBigInteger();
        BigInteger y = curvePt.getY().toBigInteger();
        byte[] xBytes = removeSignByte(x.toByteArray());
        byte[] yBytes = removeSignByte(y.toByteArray());
        byte[] pubKeyBytes = new byte[65];
        pubKeyBytes[0] = new Byte("04");
        System.arraycopy(xBytes, 0, pubKeyBytes, 1, xBytes.length);
        System.arraycopy(yBytes, 0, pubKeyBytes, 33, xBytes.length);

        ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
        KeyFactory fact = KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        ECCurve curve = params.getCurve();
        java.security.spec.EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, params.getSeed());
        java.security.spec.ECPoint point = ECPointUtil.decodePoint(ellipticCurve, pubKeyBytes);
        java.security.spec.ECParameterSpec params2 = EC5Util.convertSpec(ellipticCurve, params);
        java.security.spec.ECPublicKeySpec keySpec = new java.security.spec.ECPublicKeySpec(point, params2);
        return fact.generatePublic(keySpec);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static byte[] removeSignByte(byte[] arr) {
        if (arr.length == 33) {
            byte[] newArr = new byte[32];
            System.arraycopy(arr, 1, newArr, 0, newArr.length);
            return newArr;
        }
        return arr;
    }

    public String encryptByPublicKey(String data, PublicKey public_key) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.ENCRYPT_MODE, public_key);
        byte[] secret = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(secret, Base64.NO_WRAP);
    }

    //Not available by Keystore Private Key
    public String decryptByPrivateKey(String data, PrivateKey privateKey) throws Exception {
        /*KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
        if (privateKeyEntry == null) {
            return null;
        }*/

        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] b = cipher.doFinal(Base64.decode(data, Base64.NO_WRAP));
        return new String(b);
    }

    public static String signByPrivateKey(String data, PrivateKey privateKey) throws Exception {
        /*KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry();
        if (privateKeyEntry == null) {
            return null;
        }*/
        Signature s = Signature.getInstance("SHA256withECDSA");
        s.initSign(privateKey);
        s.update(data.getBytes());
        return Base64.encodeToString(s.sign(), Base64.NO_WRAP);
    }

    public static boolean verifySignature(String data, String signature, PublicKey publicKey) throws Exception {
        Signature s = Signature.getInstance("SHA256withECDSA");
        s.initVerify(publicKey);
        s.update(data.getBytes());
        return s.verify(Base64.decode(signature, Base64.NO_WRAP));
    }
}
