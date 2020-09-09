package com.pachain.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pachain.android.adapter.BallotHomeAdapter;
import com.pachain.android.common.DataToolPackage;
import com.pachain.android.entity.BallotEntity;
import com.pachain.android.entity.CandidateEntity;
import com.pachain.android.tool.DBManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class PAChainBallotActivity extends Activity implements View.OnClickListener {
    private TextView tv_back;
    private TextView tv_title;
    private ListView lv_contents;
    private LinearLayout ll_toolBar;
    private TextView tv_done;
    private TextView tv_cancel;

    private BallotEntity ballot;
    private BallotHomeAdapter adapter;
    private JSONArray votes;

    private DBManager dbManager;
    private DataToolPackage dataToolPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("pachain_activity_ballot", "layout", getPackageName()));

        tv_back = findViewById(getResources().getIdentifier("tv_back", "id", getPackageName()));
        tv_back.setOnClickListener(this);
        tv_title = findViewById(getResources().getIdentifier("tv_title", "id", getPackageName()));
        tv_title.setText(getResources().getString(getResources().getIdentifier("gotv_myBallots", "string", getPackageName())));
        lv_contents = findViewById(getResources().getIdentifier("lv_contents", "id", getPackageName()));
        ll_toolBar = findViewById(getResources().getIdentifier("ll_toolBar", "id", getPackageName()));
        tv_done = findViewById(getResources().getIdentifier("tv_done", "id", getPackageName()));
        tv_done.setOnClickListener(this);
        tv_cancel = findViewById(getResources().getIdentifier("tv_cancel", "id", getPackageName()));
        tv_cancel.setOnClickListener(this);

        ballot = (BallotEntity) getIntent().getExtras().getSerializable("ballot");

        dbManager = DBManager.getIntance(getApplicationContext());
        dataToolPackage = new DataToolPackage(getApplicationContext(), dbManager);
        HashMap<String, String> localVotes = dataToolPackage.getLocalVotes(ballot.getNumber());
        if (localVotes.containsKey(ballot.getNumber().toLowerCase())) {
            try {
                JSONArray votes = new JSONArray(localVotes.get(ballot.getNumber().toLowerCase()));
                ballot.setVoted(true);
                JSONObject vote;
                JSONArray candidateIDs;
                for (CandidateEntity candidateEntity : ballot.getCandidates()) {
                    if (candidateEntity.getID() < 1 && candidateEntity.getSeatID() < 1 && candidateEntity.getElectionID() > 0) {
                        candidateEntity.setVoted(true);
                    } else if (candidateEntity.getID() > 0) {
                        candidateEntity.setVoting(false);
                        for (int m = 0; m < votes.length(); m++) {
                            vote = votes.getJSONObject(m);
                            candidateIDs = new JSONArray(vote.getString("candidateIDs"));
                            for (int d = 0; d < candidateIDs.length(); d++) {
                                if (candidateIDs.getInt(d) == candidateEntity.getID()
                                    && candidateEntity.getElectionID() == vote.getInt("electionID")
                                    && candidateEntity.getSeatID() == vote.getInt("seatID")) {
                                    candidateEntity.setVoted(true);
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new BallotHomeAdapter(this, ballot.getCandidates());
        lv_contents.setAdapter(adapter);
        adapter.setOnItemClickListener(new BallotHomeAdapter.OnItemClickListener() {
            @Override
            public void onVoteClick(View view, int i) {
                ll_toolBar.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 165);
                lv_contents.setLayoutParams(layoutParams);
            }

            @Override
            public void onViewProgressClick(View view, int i) {
                Intent intent = new Intent(PAChainBallotActivity.this, PAChainViewVotingProgressActivity.class);
                intent.putExtra("ballot", ballot);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("tv_back", "id", getPackageName())) {
            finish();
        } else if (v.getId() == getResources().getIdentifier("tv_done", "id", getPackageName())) {
            votes = new JSONArray();
            JSONArray checkedCandidateIDs = new JSONArray();
            JSONObject voteObject;
            int unCheckCount = 0;
            int checkEveryCount = 0;
            int lastSeatID = 0, lastSeatVoteLimit = 0, lastElectionID = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                CandidateEntity candidateEntity = (CandidateEntity) adapter.getItem(i);
                if (candidateEntity.getID() > 0) {
                    if (lastSeatID > 0 && lastSeatID != candidateEntity.getSeatID()) {
                        if (checkEveryCount != lastSeatVoteLimit) {
                            unCheckCount++;
                        }
                        checkEveryCount = 0;
                    }
                    if (lastSeatID > 0 && lastSeatID != candidateEntity.getSeatID() && checkedCandidateIDs != null && checkedCandidateIDs.length() > 0) {
                        try {
                            boolean exists = false;
                            for (int m = 0; m < votes.length(); m++) {
                                if (votes.getJSONObject(m).getInt("electionID") == lastElectionID && votes.getJSONObject(m).getInt("seatID") == lastSeatID) {
                                    exists = true;
                                    votes.getJSONObject(m).put("candidateIDs", checkedCandidateIDs);
                                    break;
                                }
                            }
                            if (!exists) {
                                voteObject = new JSONObject();
                                voteObject.put("electionID", lastElectionID);
                                voteObject.put("seatID", lastSeatID);
                                voteObject.put("candidateIDs", checkedCandidateIDs);
                                votes.put(voteObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        checkedCandidateIDs = new JSONArray();
                    }
                    if (candidateEntity.isVoted()) {
                        checkEveryCount++;
                        checkedCandidateIDs.put(candidateEntity.getID());
                    }
                    lastSeatID = candidateEntity.getSeatID();
                    lastSeatVoteLimit = 1;
                    lastElectionID = candidateEntity.getElectionID();
                }
                if (i == adapter.getCount() - 1) {
                    if (checkedCandidateIDs != null && checkedCandidateIDs.length() > 0) {
                        try {
                            boolean exists = false;
                            for (int m = 0; m < votes.length(); m++) {
                                if (votes.getJSONObject(m).getInt("electionID") == lastElectionID && votes.getJSONObject(m).getInt("seatID") == lastSeatID) {
                                    exists = true;
                                    votes.getJSONObject(m).put("candidateIDs", checkedCandidateIDs);
                                    break;
                                }
                            }
                            if (!exists) {
                                voteObject = new JSONObject();
                                voteObject.put("electionID", lastElectionID);
                                voteObject.put("seatID", lastSeatID);
                                voteObject.put("candidateIDs", checkedCandidateIDs);
                                votes.put(voteObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (checkEveryCount != lastSeatVoteLimit) {
                        unCheckCount++;
                    }
                    if (unCheckCount > 0) {
                        Toast.makeText(PAChainBallotActivity.this, unCheckCount + " " + getResources().getString(getResources().getIdentifier("ballot_voteMissing", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(PAChainBallotActivity.this, PAChainVotingActivity.class);
                        intent.putExtra("ballotno", ballot.getNumber());
                        intent.putExtra("votes", votes.toString());
                        intent.putExtra("election", ballot.getElection());
                        startActivityForResult(intent, 1);
                    }
                }
            }
        } else if (v.getId() == getResources().getIdentifier("tv_cancel", "id", getPackageName())) {
            for (CandidateEntity candidateEntity : ballot.getCandidates()) {
                candidateEntity.setVoting(false);
                if (candidateEntity.isVoted()) {
                    candidateEntity.setVoted(false);
                }
            }
            adapter.notifyDataSetChanged();

            ll_toolBar.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            lv_contents.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getExtras() == null) {
            return;
        }
        Bundle bundle = data.getExtras();
        if (bundle.containsKey("votedSuccess") && bundle.getBoolean("votedSuccess")) {
            try {
                JSONObject vote;
                JSONArray candidateIDs;
                for (CandidateEntity candidateEntity : ballot.getCandidates()) {
                    if (candidateEntity.getID() < 1 && candidateEntity.getSeatID() < 1 && candidateEntity.getElectionID() > 0) {
                        candidateEntity.setVoted(true);
                        candidateEntity.setVoting(false);
                    } else if (candidateEntity.getID() > 0) {
                        candidateEntity.setVoting(false);
                        for (int m = 0; m < votes.length(); m++) {
                            vote = votes.getJSONObject(m);
                            candidateIDs = new JSONArray(vote.getString("candidateIDs"));
                            for (int d = 0; d < candidateIDs.length(); d++) {
                                if (candidateIDs.getInt(d) == candidateEntity.getID()
                                    && candidateEntity.getElectionID() == vote.getInt("electionID")
                                    && candidateEntity.getSeatID() == vote.getInt("seatID")) {
                                    candidateEntity.setVoted(true);
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                ll_toolBar.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 0);
                lv_contents.setLayoutParams(layoutParams);
            } catch (Exception ex) {
            }
        }
    }

}
