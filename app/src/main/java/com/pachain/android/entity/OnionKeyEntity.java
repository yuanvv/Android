package com.pachain.android.entity;

public class OnionKeyEntity {
    private String EncryptPublicKey;
    private String PersonalPublicKey;

    public String getEncryptPublicKey() {
        return EncryptPublicKey;
    }

    public void setEncryptPublicKey(String encryptPublicKey) {
        EncryptPublicKey = encryptPublicKey;
    }

    public String getPersonalPublicKey() {
        return PersonalPublicKey;
    }

    public void setPersonalPublicKey(String personalPublicKey) {
        PersonalPublicKey = personalPublicKey;
    }
}
