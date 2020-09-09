package com.pachain.android;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.biometric.manager.FingerManager;
import com.biometric.manager.callback.SimpleFingerCallback;
import com.pachain.android.common.DataToolPackage;
import com.pachain.android.common.PostApi;
import com.pachain.android.common.ToolPackage;
import com.pachain.android.config.Config;
import com.pachain.android.entity.OnionKeyEntity;
import com.pachain.android.entity.VoterEntity;
import com.pachain.android.entity.VotingOnionEntity;
import com.pachain.android.tool.DBManager;
import com.pachain.android.util.SPUtils;
import com.pachain.android.util.Secp256k1Util;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class PAChainVotingActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_back;
    private TextView tv_title;
    private LinearLayout ll_voting;
    private EditText et_verificationCode;
    private TextView tv_submit;
    private TextView tv_cancel;
    private LinearLayout ll_voted;
    private TextView tv_votedBack;

    private String ballotNumber;
    private String votes;
    private String election;
    private ProgressDialog progressDialog;
    private Secp256k1Util ecKeyUtil;
    private Map<String, Object> ecKey;
    private ArrayList<VotingOnionEntity> onions;
    private DBManager dbManager;
    private DataToolPackage dataToolPackage;
    private SQLiteDatabase database;
    private VoterEntity registeredVoter;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("pachain_activity_voting", "layout", getPackageName()));

        ll_voting = findViewById(getResources().getIdentifier("ll_voting", "id", getPackageName()));
        tv_back = findViewById(getResources().getIdentifier("tv_back", "id", getPackageName()));
        tv_back.setOnClickListener(this);
        tv_title = findViewById(getResources().getIdentifier("tv_title", "id", getPackageName()));
        tv_title.setText(getResources().getString(getResources().getIdentifier("gotv_myBallots", "string", getPackageName())));
        et_verificationCode = findViewById(getResources().getIdentifier("et_verificationCode", "id", getPackageName()));
        tv_submit = findViewById(getResources().getIdentifier("tv_submit", "id", getPackageName()));
        tv_submit.setOnClickListener(this);
        tv_cancel = findViewById(getResources().getIdentifier("tv_cancel", "id", getPackageName()));
        tv_cancel.setOnClickListener(this);
        ll_voted = findViewById(getResources().getIdentifier("ll_voted", "id", getPackageName()));
        tv_votedBack = findViewById(getResources().getIdentifier("tv_votedBack", "id", getPackageName()));
        tv_votedBack.setOnClickListener(this);

        ecKeyUtil = new Secp256k1Util(this);
        ecKey = ecKeyUtil.getKeyPair();

        Bundle bundle = getIntent().getExtras();
        ballotNumber = bundle.getString("ballotno");
        votes = bundle.getString("votes");
        election = bundle.getString("election");

        onions = new ArrayList<>();
        dbManager = DBManager.getIntance(getApplicationContext());
        verificationCode = "";
        dbManager = DBManager.getIntance(getApplicationContext());
        dataToolPackage = new DataToolPackage(getApplicationContext(), dbManager);
        registeredVoter = dataToolPackage.getRegisteredVoter();

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        getOnionKeys();
    }

    private void getOnionKeys() {
        List<String> params = new ArrayList<>();
        try {
            params.add("accessToken=" + URLEncoder.encode(SPUtils.getString(PAChainVotingActivity.this, "accessToken", "")));
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(SPUtils.getString(PAChainVotingActivity.this, "accessToken", ""), (PrivateKey) ecKey.get("privateKey"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.GETONIONKEYS, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) {

            }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        String responseEncrypt = json.getString("response");
                        String response = ecKeyUtil.decryptByPrivateKey(responseEncrypt, (PrivateKey) ecKey.get("privateKey"));
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("ret")) {
                            JSONArray result = new JSONArray(object.getString("data"));
                            VotingOnionEntity onion;
                            OnionKeyEntity onionKey;
                            ArrayList<OnionKeyEntity> onionKeys;
                            for (int i = 0; i < result.length(); i++) {
                                object = result.getJSONObject(i);
                                onion = new VotingOnionEntity();
                                onion.setName(object.getString("key"));
                                JSONArray keysArray = new JSONArray(object.getString("values"));
                                onionKeys = new ArrayList<>();
                                for (int j = 0; j < keysArray.length(); j++) {
                                    onionKey = new OnionKeyEntity();
                                    onionKey.setEncryptPublicKey(keysArray.getString(j));
                                    onionKeys.add(onionKey);
                                }
                                onion.setKeys(onionKeys);
                                onions.add(onion);
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainVotingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            public void onFailed(String error) {
                Toast.makeText(PAChainVotingActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
        api.call();
    }

    private String packageByOnions() {
        String votingNumber = UUID.randomUUID().toString();
        JSONObject voteObject = new JSONObject();
        try {
            //voteObject.put("publicKey", Base64.encodeToString(ecKeyUtil.generateNewPublicKey((ECPrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP));
            voteObject.put("key", ecKeyUtil.encryptByPublicKey(verificationCode, (PublicKey) ecKey.get("publicKey")));
            voteObject.put("verificationCode", verificationCode);
            voteObject.put("state", registeredVoter.getState());
            voteObject.put("county", registeredVoter.getCounty());
            voteObject.put("precinctNumber", registeredVoter.getPrecinctNumber());
            voteObject.put("votingData", new JSONArray(votes));
            voteObject.put("votingDate", ToolPackage.getDateNow());
            voteObject.put("election", election);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray packages = new JSONArray();
        String encryptStr, encryptKey = "";
        JSONObject packageObject;
        for (VotingOnionEntity onion : onions) {
            try {
                encryptStr = "";
                encryptKey = "";
                JSONObject pkg = new JSONObject();
                pkg.put("votingnumber", votingNumber);
                pkg.put("onionkey", onion.getName());
                pkg.put("county", registeredVoter.getCounty());
                for (OnionKeyEntity key : onion.getKeys()) {
                    if (TextUtils.isEmpty(encryptStr)) {
                        encryptStr = ecKeyUtil.encryptByPublicKey(voteObject.toString(), ecKeyUtil.getPublicKeyFromString(key.getEncryptPublicKey()));
                        key.setPersonalPublicKey(voteObject.getString("key"));
                        encryptKey = voteObject.getString("key");
                    } else {
                        packageObject = new JSONObject();
                        //packageObject.put("publicKey", Base64.encodeToString(ecKeyUtil.generateNewPublicKey((ECPrivateKey) ecKey.get("privateKey")).getEncoded(), Base64.NO_WRAP));
                        packageObject.put("key", ecKeyUtil.encryptByPublicKey(encryptKey, (PublicKey) ecKey.get("publicKey")));
                        packageObject.put("package", encryptStr);
                        encryptStr = packageObject.toString();
                        encryptStr = ecKeyUtil.encryptByPublicKey(encryptStr, ecKeyUtil.getPublicKeyFromString(key.getEncryptPublicKey()));
                        key.setPersonalPublicKey(packageObject.getString("key"));
                        encryptKey = packageObject.getString("key");
                    }
                }
                pkg.put("packages", encryptStr);
                packages.put(pkg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return packages.toString();
    }

    private void saveUserVotes() {
        showProgressDialog();
        String packages = packageByOnions();
        List<String> params = new ArrayList<>();
        try {
            params.add("accessToken=" + URLEncoder.encode(SPUtils.getString(PAChainVotingActivity.this, "accessToken", "")));
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(SPUtils.getString(PAChainVotingActivity.this, "accessToken", ""), (PrivateKey) ecKey.get("privateKey"))));
            params.add("&votingDate=" + URLEncoder.encode(ToolPackage.getDateNow()));
            params.add("&ballotNumber=" + URLEncoder.encode(ballotNumber));
            params.add("&electionID=" + URLEncoder.encode(election));
            params.add("&params=" + URLEncoder.encode(packages));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.VOTE, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) { }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        String responseEncrypt = json.getString("response");
                        String response = ecKeyUtil.decryptByPrivateKey(responseEncrypt, (PrivateKey) ecKey.get("privateKey"));
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("ret")) {
                            ll_voting.setVisibility(View.GONE);
                            ll_voted.setVisibility(View.VISIBLE);

                            database = dbManager.openDb();
                            database.execSQL("DELETE FROM VotingBallot WHERE BallotNumber='" + ballotNumber + "'");
                            database.execSQL("DELETE FROM VotingBallotOnions WHERE BallotNumber='" + ballotNumber + "'");
                            database.execSQL("INSERT INTO VotingBallot(BallotNumber, Votes, VerificationCode, VotingDate) " +
                                "VALUES('" + ballotNumber + "', '" + votes.replace("'", "''") + "', '" + verificationCode.replace("'", "''") + "', '" + ToolPackage.getDateNow() + "')");
                            int i;
                            for (VotingOnionEntity onion : onions) {
                                i = 1;
                                for (OnionKeyEntity key : onion.getKeys()) {
                                    database.execSQL("INSERT INTO VotingBallotOnions(BallotNumber, OnionKey, PublicKey, PackageLevel) " +
                                        "VALUES('" + ballotNumber + "', '" + onion.getName().replace("'", "''") + "', '" + key.getPersonalPublicKey().replace("'", "''") + "', " + i + ")");
                                    i++;
                                }
                            }
                            dbManager.closeDb(database);
                        } else {
                            Toast.makeText(PAChainVotingActivity.this, getResources().getString(getResources().getIdentifier("network_unavailable", "string", getPackageName())), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(PAChainVotingActivity.this, getResources().getString(getResources().getIdentifier("network_unavailable", "string", getPackageName())), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainVotingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                closeProgressDialog();
            }

            public void onFailed(String error) {
                Toast.makeText(PAChainVotingActivity.this, error, Toast.LENGTH_LONG).show();
                closeProgressDialog();
            }
        });
        api.call();
    }

    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("tv_back", "id", getPackageName())) {
            if (ll_voted.getVisibility() == View.VISIBLE) {
                setBallotIntent();
            }
            finish();
        } else if (v.getId() == getResources().getIdentifier("tv_cancel", "id", getPackageName())) {
            finish();
        } else if (v.getId() == getResources().getIdentifier("tv_submit", "id", getPackageName())) {
            verificationCode = et_verificationCode.getText().toString().trim();
            if (TextUtils.isEmpty(verificationCode)) {
                et_verificationCode.setError(getResources().getString(getResources().getIdentifier("ballot_emptyVerificationCode", "string", getPackageName())));
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && registeredVoter.isEnableFingerprint()) {
                switch (FingerManager.checkSupport(this)) {
                    case DEVICE_UNSUPPORTED:
                    case SUPPORT_WITHOUT_DATA:
                        saveUserVotes();
                        break;
                    case SUPPORT:
                        FingerManager.build().setApplication(getApplication())
                                .setTitle(getResources().getString(getResources().getIdentifier("biometric_dialog_title", "string", getPackageName())))
                                .setDes(getResources().getString(getResources().getIdentifier("biometric_dialog_subtitle", "string", getPackageName())))
                                .setNegativeText(getResources().getString(getResources().getIdentifier("biometric_dialog_cancel", "string", getPackageName())))
                                .setFingerCallback(new SimpleFingerCallback() {
                                    @Override
                                    public void onSucceed() {
                                        saveUserVotes();
                                    }

                                    @Override
                                    public void onFailed() {

                                    }

                                    @Override
                                    public void onChange() {
                                    }
                                })
                                .create()
                                .startListener(PAChainVotingActivity.this);

                        break;
                    default:
                }
            } else {
                saveUserVotes();
            }
        } else if (v.getId() == getResources().getIdentifier("tv_votedBack", "id", getPackageName())) {
            setBallotIntent();
            finish();
        }
        if (isKeyboardShown()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean isKeyboardShown() {
        int screenHeight = getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom - getSoftButtonsBarHeight() != 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        if (ll_voted.getVisibility() == View.VISIBLE) {
            setBallotIntent();
        }
        if (isKeyboardShown()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        finish();
    }

    private void setBallotIntent() {
        Intent intent = new Intent();
        intent.putExtra("votedSuccess", true);
        setResult(0, intent);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(PAChainVotingActivity.this);
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
}
