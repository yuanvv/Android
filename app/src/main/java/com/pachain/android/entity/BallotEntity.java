package com.pachain.android.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class BallotEntity implements Serializable {
    private String Number;
    private String Date;
    private String Name;
    private boolean Voted;
    private String VotingDate;
    private boolean ExceededVoting;
    private boolean StartCounting;
    private boolean Verified;
    private String VerifyDate;
    private boolean Sample;
    private String Election;
    private ArrayList<CandidateEntity> Candidates;

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isVoted() {
        return Voted;
    }

    public void setVoted(boolean voted) {
        Voted = voted;
    }

    public String getVotingDate() {
        return VotingDate;
    }

    public void setVotingDate(String votingDate) {
        VotingDate = votingDate;
    }

    public boolean isExceededVoting() {
        return ExceededVoting;
    }

    public void setExceededVoting(boolean exceededVoting) {
        ExceededVoting = exceededVoting;
    }

    public boolean isStartCounting() {
        return StartCounting;
    }

    public void setStartCounting(boolean startCounting) {
        StartCounting = startCounting;
    }

    public boolean isVerified() {
        return Verified;
    }

    public void setVerified(boolean verified) {
        Verified = verified;
    }

    public String getVerifyDate() {
        return VerifyDate;
    }

    public void setVerifyDate(String verifyDate) {
        VerifyDate = verifyDate;
    }

    public boolean isSample() {
        return Sample;
    }

    public void setSample(boolean sample) {
        Sample = sample;
    }

    public String getElection() {
        return Election;
    }

    public void setElection(String election) {
        Election = election;
    }

    public ArrayList<CandidateEntity> getCandidates() {
        return Candidates;
    }

    public void setCandidates(ArrayList<CandidateEntity> candidates) {
        Candidates = candidates;
    }
}
