package com.pachain.android.entity;

public class VoterEntity {
    private Long VoterID;
    private String PublicKey;
    private String State;
    private String County;
    private String PrecinctNumber;
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String NameSuffix;
    private String CellPhone;
    private String Email;
    private String Address;
    private String Signature;
    private String CertificateType;
    private String CertificateFront;
    private String CertificateBack;
    private String FacePhoto;
    private boolean EnableFingerprint;
    private String RegisteredDate;
    private String VerifiedDate;
    private String AccessToken;

    public Long getVoterID() {
        return VoterID;
    }

    public void setVoterID(Long voterID) {
        VoterID = voterID;
    }

    public String getPublicKey() {
        return PublicKey;
    }

    public void setPublicKey(String publicKey) {
        PublicKey = publicKey;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCounty() {
        return County;
    }

    public void setCounty(String county) {
        County = county;
    }

    public String getPrecinctNumber() {
        return PrecinctNumber;
    }

    public void setPrecinctNumber(String precinctNumber) {
        PrecinctNumber = precinctNumber;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getNameSuffix() {
        return NameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        NameSuffix = nameSuffix;
    }

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }

    public String getCertificateType() {
        return CertificateType;
    }

    public void setCertificateType(String certificateType) {
        CertificateType = certificateType;
    }

    public String getCertificateFront() {
        return CertificateFront;
    }

    public void setCertificateFront(String certificateFront) {
        CertificateFront = certificateFront;
    }

    public String getCertificateBack() {
        return CertificateBack;
    }

    public void setCertificateBack(String certificateBack) {
        CertificateBack = certificateBack;
    }

    public String getFacePhoto() {
        return FacePhoto;
    }

    public void setFacePhoto(String facePhoto) {
        FacePhoto = facePhoto;
    }

    public boolean isEnableFingerprint() {
        return EnableFingerprint;
    }

    public void setEnableFingerprint(boolean enableFingerprint) {
        EnableFingerprint = enableFingerprint;
    }

    public String getRegisteredDate() {
        return RegisteredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        RegisteredDate = registeredDate;
    }

    public String getVerifiedDate() {
        return VerifiedDate;
    }

    public void setVerifiedDate(String verifiedDate) {
        VerifiedDate = verifiedDate;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }
}
