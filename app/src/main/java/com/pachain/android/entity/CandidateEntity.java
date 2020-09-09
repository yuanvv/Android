package com.pachain.android.entity;

import java.io.Serializable;
import java.util.HashMap;

public class CandidateEntity implements Serializable {
    private int ID;
    private String Name;
    private String Party;
    private String PartyCode;
    private String Photo;

    private int ElectionID;
    private String ElectionName;
    private String ElectionDate;
    private String ElectionState;
    private HashMap<String, String> Params;

    private int SeatID;
    private String SeatName;
    private String SeatNumber;
    private String SeatOffice;
    private String SeatCounty;
    private String SeatCity;
    private String SeatState;
    private int SeatLevel;//0: Federal, 1: State, 2: County

    private boolean Voted;
    private boolean Voting;
    private boolean ExceededVoting;

    private int VoteBallots;
    private double VoteRate;

    private boolean SampleBallot;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getParty() {
        return Party;
    }

    public void setParty(String party) {
        Party = party;
    }

    public String getPartyCode() {
        return PartyCode;
    }

    public void setPartyCode(String partyCode) {
        PartyCode = partyCode;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public int getElectionID() {
        return ElectionID;
    }

    public void setElectionID(int electionID) {
        ElectionID = electionID;
    }

    public String getElectionName() {
        return ElectionName;
    }

    public void setElectionName(String electionName) {
        ElectionName = electionName;
    }

    public String getElectionDate() {
        return ElectionDate;
    }

    public void setElectionDate(String electionDate) {
        ElectionDate = electionDate;
    }

    public String getElectionState() {
        return ElectionState;
    }

    public void setElectionState(String electionState) {
        ElectionState = electionState;
    }

    public HashMap<String, String> getParams() {
        return Params;
    }

    public void setParams(HashMap<String, String> params) {
        Params = params;
    }

    public int getSeatID() {
        return SeatID;
    }

    public void setSeatID(int seatID) {
        SeatID = seatID;
    }

    public String getSeatName() {
        return SeatName;
    }

    public void setSeatName(String seatName) {
        SeatName = seatName;
    }

    public String getSeatNumber() {
        return SeatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        SeatNumber = seatNumber;
    }

    public String getSeatOffice() {
        return SeatOffice;
    }

    public void setSeatOffice(String seatOffice) {
        SeatOffice = seatOffice;
    }

    public String getSeatCounty() {
        return SeatCounty;
    }

    public void setSeatCounty(String seatCounty) {
        SeatCounty = seatCounty;
    }

    public String getSeatCity() {
        return SeatCity;
    }

    public void setSeatCity(String seatCity) {
        SeatCity = seatCity;
    }

    public String getSeatState() {
        return SeatState;
    }

    public void setSeatState(String seatState) {
        SeatState = seatState;
    }

    public int getSeatLevel() {
        return SeatLevel;
    }

    public void setSeatLevel(int seatLevel) {
        SeatLevel = seatLevel;
    }

    public boolean isVoted() {
        return Voted;
    }

    public void setVoted(boolean voted) {
        Voted = voted;
    }

    public boolean isVoting() {
        return Voting;
    }

    public void setVoting(boolean voting) {
        Voting = voting;
    }

    public boolean isExceededVoting() {
        return ExceededVoting;
    }

    public void setExceededVoting(boolean exceededVoting) {
        ExceededVoting = exceededVoting;
    }

    public int getVoteBallots() {
        return VoteBallots;
    }

    public void setVoteBallots(int voteBallots) {
        VoteBallots = voteBallots;
    }

    public double getVoteRate() {
        return VoteRate;
    }

    public void setVoteRate(double voteRate) {
        VoteRate = voteRate;
    }

    public boolean isSampleBallot() {
        return SampleBallot;
    }

    public void setSampleBallot(boolean sampleBallot) {
        SampleBallot = sampleBallot;
    }
}
