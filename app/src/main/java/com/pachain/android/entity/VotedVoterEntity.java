package com.pachain.android.entity;

public class VotedVoterEntity {
    private String State;
    private String County;
    private String PrecinctNumber;
    private String VotedCount;
    private String VotingDate;

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

    public String getVotedCount() {
        return VotedCount;
    }

    public void setVotedCount(String votedCount) {
        VotedCount = votedCount;
    }

    public String getVotingDate() {
        return VotingDate;
    }

    public void setVotingDate(String votingDate) {
        VotingDate = votingDate;
    }
}
