package com.pachain.android.entity;

public class VotedResultEntity {
    private String Key;
    private String VerificationCode;
    private String VotingResult;
    private String VotingDate;
    private boolean Selected;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getVerificationCode() {
        return VerificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        VerificationCode = verificationCode;
    }

    public String getVotingResult() {
        return VotingResult;
    }

    public void setVotingResult(String votingResult) {
        VotingResult = votingResult;
    }

    public String getVotingDate() {
        return VotingDate;
    }

    public void setVotingDate(String votingDate) {
        VotingDate = votingDate;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }
}
