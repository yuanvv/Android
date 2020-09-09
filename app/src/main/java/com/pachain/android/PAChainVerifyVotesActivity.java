package com.pachain.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.pachain.android.adapter.BallotAdapter;
import com.pachain.android.entity.BallotEntity;
import java.util.ArrayList;

public class PAChainVerifyVotesActivity extends Activity implements View.OnClickListener {
    private TextView tv_back;
    private TextView tv_title;
    private LinearLayout ll_unverified;
    private ListView lv_unverified;
    private LinearLayout ll_verified;
    private ListView lv_verified;

    private ArrayList<BallotEntity> ballots;
    private ArrayList<BallotEntity> unverified;
    private ArrayList<BallotEntity> verified;
    private BallotAdapter unverifiedAdapter;
    private BallotAdapter verifiedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("pachain_activity_verifyvotes", "layout", getPackageName()));

        tv_back = findViewById(getResources().getIdentifier("tv_back", "id", getPackageName()));
        tv_back.setOnClickListener(this);
        tv_title = findViewById(getResources().getIdentifier("tv_title", "id", getPackageName()));
        tv_title.setText(getResources().getString(getResources().getIdentifier("verifyVote_title", "string", getPackageName())));
        ll_unverified = findViewById(getResources().getIdentifier("ll_unverified", "id", getPackageName()));
        lv_unverified = findViewById(getResources().getIdentifier("lv_unverified", "id", getPackageName()));
        ll_verified = findViewById(getResources().getIdentifier("ll_verified", "id", getPackageName()));
        lv_verified = findViewById(getResources().getIdentifier("lv_verified", "id", getPackageName()));

        unverified = new ArrayList<>();
        verified = new ArrayList<>();
        ballots = (ArrayList<BallotEntity>) getIntent().getExtras().getSerializable("ballots");
        for (BallotEntity entity : ballots) {
            if (entity.isVerified()) {
                verified.add(entity);
            } else {
                unverified.add(entity);
            }
        }

        unverifiedAdapter = new BallotAdapter(this, unverified, true);
        lv_unverified.setAdapter(unverifiedAdapter);
        lv_unverified.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BallotEntity model = (BallotEntity) unverifiedAdapter.getItem(position);

                Intent intent = new Intent(PAChainVerifyVotesActivity.this, PAChainVerifyVoteActivity.class);
                intent.putExtra("ballot", model);
                startActivity(intent);
            }
        });

        verifiedAdapter = new BallotAdapter(this, verified, true);
        lv_verified.setAdapter(verifiedAdapter);
        lv_verified.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BallotEntity model = (BallotEntity) verifiedAdapter.getItem(position);

                Intent intent = new Intent(PAChainVerifyVotesActivity.this, PAChainVerifyVoteActivity.class);
                intent.putExtra("ballot", model);
                startActivity(intent);
            }
        });

        if (unverified != null && unverified.size() > 0) {
            ll_unverified.setVisibility(View.VISIBLE);
        } else {
            ll_unverified.setVisibility(View.GONE);
        }
        if (verified != null && verified.size() > 0) {
            ll_verified.setVisibility(View.VISIBLE);
        } else {
            ll_verified.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("tv_back", "id", getPackageName())) {
            finish();
        }
    }
}
