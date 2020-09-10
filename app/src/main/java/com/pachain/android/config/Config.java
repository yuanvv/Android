package com.pachain.android.config;

public class Config {
    public final static int DB_VERSION = 1;

    public final static String PROJECT_BASE_URL = "Server api url";
    public final static String CONTROLLER = "api/";
    public final static String UPLOADPHOTO = PROJECT_BASE_URL + CONTROLLER + "voter/updateimage";
    public final static String SENDVERIFICATIONCODE = PROJECT_BASE_URL + CONTROLLER + "voter/sendsmsmessage";
    public final static String REGISTER = PROJECT_BASE_URL + CONTROLLER + "voter/register";
    public final static String VERIFYVOTER = PROJECT_BASE_URL + CONTROLLER + "voter/verify";
    public final static String BALLOTS = PROJECT_BASE_URL + CONTROLLER + "ballots/getballots";
    public final static String SAMPLEBALLOTS = PROJECT_BASE_URL + CONTROLLER + "ballots/getsampleballot";
    public final static String GETONIONKEYS = PROJECT_BASE_URL + CONTROLLER + "ballots/getonionkeys";
    public final static String VOTE = PROJECT_BASE_URL + CONTROLLER + "voted/vote";
    public final static String QUERYVOTEDVOTERS = PROJECT_BASE_URL + CONTROLLER + "voted/queryvoted";
    public final static String QUERYVOTERRESULTS = PROJECT_BASE_URL + CONTROLLER + "voted/queryvoteresult";
    public final static String VERIFIEDBALLOT = PROJECT_BASE_URL + CONTROLLER + "voted/confirmvoted";
    public final static String GETVOTERESULTS = PROJECT_BASE_URL + CONTROLLER + "voted/getvoteresult";
}
