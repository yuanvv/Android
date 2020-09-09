package com.pachain.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.pachain.android.adapter.CountyAdapter;
import com.pachain.android.adapter.PrecinctAdapter;
import com.pachain.android.adapter.StateAdapter;
import com.pachain.android.adapter.VotedResultsAdapter;
import com.pachain.android.adapter.VotedVotersAdapter;
import com.pachain.android.common.DataToolPackage;
import com.pachain.android.common.PostApi;
import com.pachain.android.common.ToolPackage;
import com.pachain.android.config.Config;
import com.pachain.android.entity.BallotEntity;
import com.pachain.android.entity.CandidateEntity;
import com.pachain.android.entity.CountyEntity;
import com.pachain.android.entity.PrecinctEntity;
import com.pachain.android.entity.StateEntity;
import com.pachain.android.entity.VotedResultEntity;
import com.pachain.android.entity.VotedVoterEntity;
import com.pachain.android.entity.VoterEntity;
import com.pachain.android.tool.DBManager;
import com.pachain.android.util.SPUtils;
import com.pachain.android.util.Secp256k1Util;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.Nullable;

public class PAChainViewVotingProgressActivity extends Activity implements View.OnClickListener, DatePicker.OnDateChangedListener {
    private TextView tv_back;
    private TextView tv_title;
    private LinearLayout ll_election;
    private TextView tv_electionName;
    private TextView tv_electionDay;
    private Spinner sp_state;
    private Spinner sp_county;
    private Spinner sp_precinct;
    private LinearLayout ll_start;
    private TextView tv_start;
    private LinearLayout ll_end;
    private TextView tv_end;
    private DatePicker dp_end;
    private LinearLayout ll_seats;
    private Spinner sp_seats;
    private TextView tv_go;
    private TextView tv_total;
    private ListView lv_votedVoters;
    private ListView lv_votedResults;
    private TextView tv_none;

    private ProgressDialog progressDialog;
    private DBManager dbManager;
    private DataToolPackage dataToolPackage;

    private Secp256k1Util ecKeyUtil;
    private Map<String, Object> ecKey;

    private VoterEntity registeredVoter;
    private BallotEntity ballot;
    private VotedVotersAdapter votedVotersAdapter;
    private ArrayList<VotedVoterEntity> votedVoters;
    private int totalCount;
    private VotedResultsAdapter votedResultsAdapter;
    private ArrayList<VotedResultEntity> votedResults;

    private ArrayList<StateEntity> states;
    private StateAdapter stateAdapter;
    private ArrayList<CountyEntity> counties;
    private ArrayList<CountyEntity> stateCounties;
    private CountyAdapter countyAdapter;
    private HashMap<String, CountyEntity> countiesMap;
    private ArrayList<PrecinctEntity> precincts;
    private PrecinctAdapter precinctAdapter;
    private int stateChangeCount;
    private int countyChangeCount;
    private StateAdapter seatAdapter;
    private ArrayList<StateEntity> seats;
    private HashMap<String, CandidateEntity> candidates;
    private StateEntity stateEntity;
    private HashMap<String, String> votedKeys;

    private String state;
    private String county;
    private String precinct;
    private int startYear, startMonth, startDay;
    private int toYear, toMonth, toDay;
    private int seatID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("pachain_activity_viewvotingprogress", "layout", getPackageName()));

        state = "";
        county = "";
        precinct = "";
        stateChangeCount = 0;
        countyChangeCount = 0;
        totalCount = 0;
        seatID = 0;
        Calendar calendar = Calendar.getInstance();
        startYear = calendar.get(calendar.YEAR);
        startMonth = calendar.get(calendar.MONTH) + 1;
        startDay = calendar.get(calendar.DAY_OF_MONTH);
        toYear = startYear;
        toMonth = startMonth;
        toDay = startDay;
        votedVoters = new ArrayList<>();
        votedResults = new ArrayList<>();
        stateCounties = new ArrayList<>();
        seats = new ArrayList<>();
        candidates = new HashMap<>();

        Bundle bundle = getIntent().getExtras();
        ballot = bundle != null && bundle.containsKey("ballot") ? (BallotEntity) getIntent().getExtras().getSerializable("ballot") : null;
        dbManager = DBManager.getIntance(getApplicationContext());
        dataToolPackage = new DataToolPackage(getApplicationContext(), dbManager);
        registeredVoter = dataToolPackage.getRegisteredVoter();
        votedKeys = dataToolPackage.getVotedKeys(ballot.getNumber());
        states = dataToolPackage.getStates();
        if (states != null && states.size() > 0) {
            stateEntity = new StateEntity();
            stateEntity.setID(0);
            stateEntity.setCode("");
            stateEntity.setName(getResources().getString(getResources().getIdentifier("viewVotingProgress_state", "string", getPackageName())));
            states.add(0, stateEntity);
        }
        counties = dataToolPackage.getCounties();
        countiesMap = dataToolPackage.getCountiesMap();
        CountyEntity countyEntity = new CountyEntity();
        countyEntity.setID(0);
        countyEntity.setCode("");
        countyEntity.setNumber("");
        countyEntity.setName(getResources().getString(getResources().getIdentifier("register_county", "string", getPackageName())));
        stateCounties.add(0, countyEntity);
        precincts = new ArrayList<>();
        PrecinctEntity precinctEntity = new PrecinctEntity();
        precinctEntity.setState("");
        precinctEntity.setCounty("");
        precinctEntity.setNumber(getResources().getString(getResources().getIdentifier("viewVotingProgress_precinct", "string", getPackageName())));
        precincts.add(0, precinctEntity);

        tv_back = findViewById(getResources().getIdentifier("tv_back", "id", getPackageName()));
        tv_back.setOnClickListener(this);
        tv_title = findViewById(getResources().getIdentifier("tv_title", "id", getPackageName()));
        tv_title.setText(getResources().getString(getResources().getIdentifier("viewVotingProgress_title", "string", getPackageName())));
        ll_election = findViewById(getResources().getIdentifier("ll_election", "id", getPackageName()));
        tv_electionName = findViewById(getResources().getIdentifier("tv_electionName", "id", getPackageName()));
        tv_electionDay = findViewById(getResources().getIdentifier("tv_electionDay", "id", getPackageName()));
        sp_state = findViewById(getResources().getIdentifier("sp_state", "id", getPackageName()));
        stateAdapter = new StateAdapter(this, states);
        sp_state.setAdapter(stateAdapter);
        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stateChangeCount++;
                StateEntity model = (StateEntity) stateAdapter.getItem(i);
                state = model.getCode();
                if (stateChangeCount == 1 && state.toUpperCase().equals(registeredVoter.getState().toUpperCase()) && !TextUtils.isEmpty(registeredVoter.getCounty())) {
                } else {
                    county = "";
                    if (stateCounties != null && stateCounties.size() > 1) {
                        sp_county.setSelection(1, false);
                    }
                    getCountiesByState(state);
                    sp_county.setSelection(1, false);
                    sp_county.setSelection(0, true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        sp_county = findViewById(getResources().getIdentifier("sp_county", "id", getPackageName()));
        countyAdapter = new CountyAdapter(this, stateCounties);
        sp_county.setAdapter(countyAdapter);
        sp_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                countyChangeCount++;
                CountyEntity model = (CountyEntity) countyAdapter.getItem(i);
                if (i > 0) {
                    county = model.getNumber();
                } else {
                    county = "";
                }
                if (countyChangeCount == 1 && county.toUpperCase().equals(registeredVoter.getCounty().toUpperCase()) && !TextUtils.isEmpty(registeredVoter.getPrecinctNumber())) {
                } else {
                    precinct = "";
                    if (precincts != null && precincts.size() > 1) {
                        sp_precinct.setSelection(1, false);
                    }
                    getPrecinctsByCounty(state, county);
                    sp_precinct.setSelection(1, false);
                    sp_precinct.setSelection(0, true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        sp_precinct = findViewById(getResources().getIdentifier("sp_precinct", "id", getPackageName()));
        precinctAdapter = new PrecinctAdapter(this, precincts);
        sp_precinct.setAdapter(precinctAdapter);
        sp_precinct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PrecinctEntity model = (PrecinctEntity) precinctAdapter.getItem(i);
                if (!TextUtils.isEmpty(model.getState())) {
                    precinct = model.getNumber();
                } else {
                    precinct = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        ll_start = findViewById(getResources().getIdentifier("ll_start", "id", getPackageName()));
        ll_start.setOnClickListener(this);
        tv_start = findViewById(getResources().getIdentifier("tv_start", "id", getPackageName()));
        ll_end = findViewById(getResources().getIdentifier("ll_end", "id", getPackageName()));
        ll_end.setOnClickListener(this);
        tv_end = findViewById(getResources().getIdentifier("tv_end", "id", getPackageName()));
        ll_seats = findViewById(getResources().getIdentifier("ll_seats", "id", getPackageName()));
        sp_seats = findViewById(getResources().getIdentifier("sp_seats", "id", getPackageName()));

        tv_go = findViewById(getResources().getIdentifier("tv_go", "id", getPackageName()));
        tv_go.setOnClickListener(this);
        tv_total = findViewById(getResources().getIdentifier("tv_total", "id", getPackageName()));
        lv_votedVoters = findViewById(getResources().getIdentifier("lv_votedVoters", "id", getPackageName()));
        votedVotersAdapter = new VotedVotersAdapter(this, votedVoters);
        lv_votedVoters.setAdapter(votedVotersAdapter);

        lv_votedResults = findViewById(getResources().getIdentifier("lv_votedResults", "id", getPackageName()));
        votedResultsAdapter = new VotedResultsAdapter(this, votedResults);
        lv_votedResults.setAdapter(votedResultsAdapter);

        tv_none = findViewById(getResources().getIdentifier("tv_none", "id", getPackageName()));

        if (!TextUtils.isEmpty(registeredVoter.getState())) {
            state = registeredVoter.getState();
            int i = 0;
            for (StateEntity stateEntity : states) {
                if (stateEntity.getCode().toUpperCase().equals(registeredVoter.getState().toUpperCase())) {
                    sp_state.setSelection(i, true);
                    break;
                }
                i++;
            }

            getCountiesByState(registeredVoter.getState());
            if (!TextUtils.isEmpty(registeredVoter.getCounty())) {
                i = 0;
                county = registeredVoter.getCounty();
                for (CountyEntity entity : stateCounties) {
                    if (entity.getNumber().toUpperCase().equals(registeredVoter.getCounty().toUpperCase())) {
                        sp_county.setSelection(i, true);
                        break;
                    }
                    i++;
                }
            }

            getPrecinctsByCounty(registeredVoter.getState(), registeredVoter.getCounty());
            if (!TextUtils.isEmpty(registeredVoter.getPrecinctNumber())) {
                i = 0;
                precinct = registeredVoter.getPrecinctNumber();
                for (PrecinctEntity entity : precincts) {
                    if (entity.getNumber().toUpperCase().equals(registeredVoter.getPrecinctNumber().toUpperCase())) {
                        sp_precinct.setSelection(i, true);
                        break;
                    }
                    i++;
                }
            }
        }

        ecKeyUtil = new Secp256k1Util(this);
        ecKey = ecKeyUtil.getKeyPair();

        if (ballot != null) {
            tv_electionName.setText(ballot.getName());
            tv_electionDay.setText(ballot.getDate());
            if (ballot.isStartCounting()) {
                ll_seats.setVisibility(View.VISIBLE);
                for (CandidateEntity entity : ballot.getCandidates()) {
                    if (entity.getElectionID() > 0 && entity.getSeatID() > 0 && entity.getID() > 0) {
                        candidates.put(entity.getID() + "", entity);
                    } else if (entity.getSeatID() > 0) {
                        stateEntity = new StateEntity();
                        stateEntity.setID(entity.getSeatID());
                        stateEntity.setCode("");
                        stateEntity.setName(entity.getSeatName());
                        seats.add(stateEntity);
                    }
                }
                seatAdapter = new StateAdapter(this, seats);
                sp_seats.setAdapter(seatAdapter);
                sp_seats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        StateEntity model = (StateEntity) seatAdapter.getItem(i);
                        seatID = model.getID();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) { }
                });

                getVoterResults(registeredVoter.getState(), registeredVoter.getCounty(), registeredVoter.getPrecinctNumber(), seats.get(0).getID());
            } else {
                getVotedVoters(registeredVoter.getState(), registeredVoter.getCounty(), registeredVoter.getPrecinctNumber());
            }
        }
    }

    private void getCountiesByState(String state) {
        stateCounties.clear();
        if (!TextUtils.isEmpty(state)) {
            for (CountyEntity countyEntity : counties) {
                if (countyEntity.getState().toLowerCase().equals(state.toLowerCase())) {
                    stateCounties.add(countyEntity);
                }
            }
        }
        CountyEntity countyEntity = new CountyEntity();
        countyEntity.setID(0);
        countyEntity.setCode("");
        countyEntity.setNumber("");
        countyEntity.setName(getResources().getString(getResources().getIdentifier("viewVotingProgress_county", "string", getPackageName())));
        stateCounties.add(0, countyEntity);
        countyAdapter.notifyDataSetChanged();
    }

    private void getPrecinctsByCounty(String state, String county) {
        precincts.clear();
        if (!TextUtils.isEmpty(state) && !TextUtils.isEmpty(county)) {
            precincts.addAll(dataToolPackage.getPrecincts(state, county));
        }
        PrecinctEntity precinctEntity = new PrecinctEntity();
        precinctEntity.setState("");
        precinctEntity.setCounty("");
        precinctEntity.setNumber(getResources().getString(getResources().getIdentifier("viewVotingProgress_precinct", "string", getPackageName())));
        precincts.add(0, precinctEntity);
        precinctAdapter.notifyDataSetChanged();
    }

    private void getVotedVoters(String state, String county, String precinct) {
        showProgressDialog();
        List<String> params = new ArrayList<>();
        String accessToken = SPUtils.getString(PAChainViewVotingProgressActivity.this, "accessToken", "");
        params.add("accessToken=" + URLEncoder.encode(accessToken));
        try {
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(SPUtils.getString(PAChainViewVotingProgressActivity.this, "accessToken", ""), (PrivateKey) ecKey.get("privateKey"))));
            params.add("&ballotnumber=" + "");
            params.add("&electionID=" + URLEncoder.encode(ballot.getElection()));
            params.add("&state=" + URLEncoder.encode(state));
            params.add("&county=" + URLEncoder.encode(county));
            params.add("&precinctNumber=" + URLEncoder.encode(precinct));
            params.add("&start=" + ToolPackage.ConvertToCommonStringByDate(tv_start.getText().toString().replace(getResources().getString(getResources().getIdentifier("viewVotingProgress_dateStart", "string", getPackageName())), "")));
            params.add("&end=" + ToolPackage.ConvertToCommonStringByDate(tv_end.getText().toString().replace(getResources().getString(getResources().getIdentifier("viewVotingProgress_dateTo", "string", getPackageName())), "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.QUERYVOTEDVOTERS, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) { }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        JSONObject response = new JSONObject(json.getString("response"));
                        totalCount = response.getInt("count");
                        JSONArray votesArray = new JSONArray(response.getString("data"));
                        VotedVoterEntity entity;
                        JSONObject object;
                        votedVoters.clear();
                        for (int i = 0; i < votesArray.length(); i++) {
                            object = votesArray.getJSONObject(i);
                            entity = new VotedVoterEntity();
                            entity.setState(object.getString("state"));
                            entity.setCounty(object.getString("county"));
                            entity.setCounty(countiesMap.containsKey(entity.getCounty()) ? countiesMap.get(entity.getCounty()).getName().replace(" County", "") : entity.getCounty());
                            entity.setPrecinctNumber(object.getString("precinctNumber"));
                            entity.setVotedCount(ToolPackage.decimalFormat(object.getInt("count")));
                            entity.setVotingDate(ToolPackage.ConvertToSimpleStringByTime(object.getString("votingDate")));
                            votedVoters.add(entity);
                        }
                        if (votedVoters != null && votedVoters.size() > 0) {
                            entity = new VotedVoterEntity();
                            entity.setState(getResources().getString(getResources().getIdentifier("viewVotingProgress_state", "string", getPackageName())));
                            entity.setCounty(getResources().getString(getResources().getIdentifier("viewVotingProgress_county", "string", getPackageName())));
                            entity.setPrecinctNumber(getResources().getString(getResources().getIdentifier("viewVotingProgress_precinct", "string", getPackageName())));
                            entity.setVotingDate(getResources().getString(getResources().getIdentifier("viewVotingProgress_date", "string", getPackageName())));
                            entity.setVotedCount(getResources().getString(getResources().getIdentifier("viewVotingProgress_count", "string", getPackageName())));
                            votedVoters.add(0, entity);
                            votedVotersAdapter.notifyDataSetChanged();

                            tv_none.setVisibility(View.GONE);
                            tv_total.setText(getResources().getString(getResources().getIdentifier("viewVotingProgress_total", "string", getPackageName())) + " " + totalCount);
                            tv_total.setVisibility(View.VISIBLE);
                            lv_votedVoters.setVisibility(View.VISIBLE);
                        } else {
                            tv_none.setVisibility(View.VISIBLE);
                            tv_total.setVisibility(View.GONE);
                            lv_votedVoters.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(PAChainViewVotingProgressActivity.this, json.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainViewVotingProgressActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                closeProgressDialog();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PAChainViewVotingProgressActivity.this, error, Toast.LENGTH_LONG).show();
                closeProgressDialog();
            }
        });
        api.call();
    }

    private void getVoterResults(String state, String county, String precinct, int seatID) {
        showProgressDialog();
        List<String> params = new ArrayList<>();
        String accessToken = SPUtils.getString(PAChainViewVotingProgressActivity.this, "accessToken", "");
        params.add("accessToken=" + URLEncoder.encode(accessToken));
        try {
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(SPUtils.getString(PAChainViewVotingProgressActivity.this, "accessToken", ""), (PrivateKey) ecKey.get("privateKey"))));
            params.add("&electionID=" + URLEncoder.encode(ballot.getElection()));
            params.add("&seatID=" + seatID);
            params.add("&state=" + URLEncoder.encode(state));
            params.add("&county=" + URLEncoder.encode(county));
            params.add("&precinctNumber=" + URLEncoder.encode(precinct));
            params.add("&start=" + ToolPackage.ConvertToCommonStringByDate(tv_start.getText().toString().replace(getResources().getString(getResources().getIdentifier("viewVotingProgress_dateStart", "string", getPackageName())), "")));
            params.add("&end=" + ToolPackage.ConvertToCommonStringByDate(tv_end.getText().toString().replace(getResources().getString(getResources().getIdentifier("viewVotingProgress_dateTo", "string", getPackageName())), "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.QUERYVOTERRESULTS, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) { }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        JSONObject response = new JSONObject(json.getString("response"));
                        totalCount = response.getInt("count");
                        JSONArray votesArray = new JSONArray(response.getString("data"));
                        VotedResultEntity entity;
                        JSONObject object;
                        votedResults.clear();
                        for (int i = 0; i < votesArray.length(); i++) {
                            object = votesArray.getJSONObject(i);
                            entity = new VotedResultEntity();
                            entity.setKey(object.getString("key"));
                            entity.setVerificationCode(object.getString("verificationCode"));
                            entity.setVotingResult(object.getString("candidateID"));
                            entity.setVotingResult(candidates.containsKey(entity.getVotingResult()) ? candidates.get(entity.getVotingResult()).getName() : "");
                            entity.setVotingDate(ToolPackage.ConvertToSimpleStringByTime(object.getString("votingDate")));
                            entity.setSelected(votedKeys.containsKey(entity.getKey()) ? true : false);
                            votedResults.add(entity);
                        }
                        if (votedResults != null && votedResults.size() > 0) {
                            entity = new VotedResultEntity();
                            entity.setKey(getResources().getString(getResources().getIdentifier("viewVotingProgress_voter", "string", getPackageName())));
                            entity.setVotingResult(getResources().getString(getResources().getIdentifier("viewVotingProgress_votingResult", "string", getPackageName())));
                            entity.setVerificationCode(getResources().getString(getResources().getIdentifier("viewVotingProgress_verificationCode", "string", getPackageName())));
                            entity.setVotingDate(getResources().getString(getResources().getIdentifier("viewVotingProgress_date", "string", getPackageName())));
                            votedResults.add(0, entity);
                            votedResultsAdapter.notifyDataSetChanged();

                            tv_none.setVisibility(View.GONE);
                            tv_total.setText(getResources().getString(getResources().getIdentifier("viewVotingProgress_total", "string", getPackageName())) + " " + totalCount);
                            tv_total.setVisibility(View.VISIBLE);
                            lv_votedResults.setVisibility(View.VISIBLE);
                        } else {
                            tv_none.setVisibility(View.VISIBLE);
                            tv_total.setVisibility(View.GONE);
                            lv_votedResults.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(PAChainViewVotingProgressActivity.this, json.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainViewVotingProgressActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                closeProgressDialog();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PAChainViewVotingProgressActivity.this, error, Toast.LENGTH_LONG).show();
                closeProgressDialog();
            }
        });
        api.call();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("tv_back", "id", getPackageName())) {
            finish();
        } else if (v.getId() == getResources().getIdentifier("tv_go", "id", getPackageName())) {
            if (ballot != null) {
                if (ballot.isStartCounting()) {
                    getVoterResults(state, county, precinct, seatID);
                } else {
                    getVotedVoters(state, county, precinct);
                }
            }
        } else if (v.getId() == getResources().getIdentifier("ll_start", "id", getPackageName())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PAChainViewVotingProgressActivity.this);
            builder.setPositiveButton(getResources().getString(getResources().getIdentifier("common_ok", "string", getPackageName())), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    tv_start.setText(startMonth + "/" + startDay + "/" + startYear);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getResources().getString(getResources().getIdentifier("common_cancel", "string", getPackageName())), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            View dialogView = View.inflate(PAChainViewVotingProgressActivity.this, getResources().getIdentifier("pachain_dialog_date", "layout", getPackageName()), null);
            DatePicker datePicker = dialogView.findViewById(getResources().getIdentifier("dp_start", "id", getPackageName()));
            dialogView.findViewById(getResources().getIdentifier("dp_to", "id", getPackageName())).setVisibility(View.GONE);
            //dialog.setTitle(getResources().getString(getResources().getIdentifier("viewVotingProgress_dateStartTitle", "string", getPackageName())));
            dialog.setView(dialogView);
            dialog.show();
            datePicker.init(startYear, startMonth - 1, startDay, this);
        } else if (v.getId() == getResources().getIdentifier("ll_end", "id", getPackageName())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PAChainViewVotingProgressActivity.this);
            builder.setPositiveButton(getResources().getString(getResources().getIdentifier("common_ok", "string", getPackageName())), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    tv_end.setText(toMonth + "/" + toDay + "/" + toYear);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getResources().getString(getResources().getIdentifier("common_cancel", "string", getPackageName())), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            View dialogView = View.inflate(PAChainViewVotingProgressActivity.this, getResources().getIdentifier("pachain_dialog_date", "layout", getPackageName()), null);
            DatePicker datePicker = dialogView.findViewById(getResources().getIdentifier("dp_to", "id", getPackageName()));
            dialogView.findViewById(getResources().getIdentifier("dp_start", "id", getPackageName())).setVisibility(View.GONE);
            //dialog.setTitle(getResources().getString(getResources().getIdentifier("viewVotingProgress_dateToTitle", "string", getPackageName())));
            dialog.setView(dialogView);
            dialog.show();
            datePicker.init(toYear, toMonth - 1, toDay, this);
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(PAChainViewVotingProgressActivity.this);
            progressDialog.setMessage(getResources().getString(getResources().getIdentifier("common_loading", "string", getPackageName())));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        View dialogView = View.inflate(PAChainViewVotingProgressActivity.this, getResources().getIdentifier("pachain_dialog_date", "layout", getPackageName()), null);
        if (view.getId() == dialogView.getResources().getIdentifier("dp_start", "id", getPackageName())) {
            this.startYear = year;
            this.startMonth = monthOfYear + 1;
            this.startDay = dayOfMonth;
        } else {
            this.toYear = year;
            this.toMonth = monthOfYear + 1;
            this.toDay = dayOfMonth;
        }
    }
}
