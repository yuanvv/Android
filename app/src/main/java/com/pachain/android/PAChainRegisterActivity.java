package com.pachain.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.pachain.android.adapter.BallotAdapter;
import com.pachain.android.adapter.CountyAdapter;
import com.pachain.android.adapter.StateAdapter;
import com.pachain.android.common.DataToolPackage;
import com.pachain.android.common.PostApi;
import com.pachain.android.common.ToolPackage;
import com.pachain.android.config.Config;
import com.pachain.android.entity.BallotEntity;
import com.pachain.android.entity.CandidateEntity;
import com.pachain.android.entity.CountyEntity;
import com.pachain.android.entity.PartyEntity;
import com.pachain.android.entity.VoterEntity;
import com.pachain.android.tool.ListViewForScrollView;
import com.pachain.android.util.FileUtil;
import com.pachain.android.util.SDCardUtils;
import com.pachain.android.util.SPUtils;
import com.pachain.android.util.Secp256k1Util;
import com.pachain.android.entity.StateEntity;
import com.pachain.android.tool.DBManager;
import com.pachain.android.tool.SignatureView;
import com.biometric.manager.FingerManager;
import com.biometric.manager.callback.SimpleFingerCallback;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PAChainRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_back;
    private TextView tv_title;
    private ScrollView sv_mScrollView;
    private LinearLayout ll_beforeRegister;
    private LinearLayout ll_welcome;
    private EditText et_voterId;
    private TextView tv_welcome_next;
    private TextView tv_registerVoter;
    private LinearLayout ll_baseInfo;
    private Spinner sp_state;
    private Spinner sp_county;
    private EditText et_precinctNumber;
    private ImageView iv_precinctQuestion;
    private EditText et_lastName;
    private EditText et_firstName;
    private EditText et_middleName;
    private EditText et_nameSuffix;
    private EditText et_mobilePhone;
    private EditText et_email;
    private EditText et_address;
    private TextView tv_signatureClear;
    private SignatureView sv_signature;
    private TextView tv_baseInfo_next;
    private LinearLayout ll_confirmScanDocument;
    private TextView tv_confirmScanDocument;
    private LinearLayout ll_scanDocument;
    private Spinner sp_certificates;
    private TextView tv_scanFront;
    private ImageView iv_certificateFront;
    private TextView tv_scanBack;
    private ImageView iv_certificateBack;
    private TextView tv_scanDocument_next;
    private LinearLayout ll_confirmFaceRecognition;
    private TextView tv_confirmFaceRecognition;
    private LinearLayout ll_successFaceRecognition;
    private TextView tv_faceRecognition_next;
    private LinearLayout ll_verifyMobile;
    private EditText et_verificationCode;
    private TextView tv_verifyMobile_next;
    private TextView tv_requestNewCode;
    private LinearLayout ll_finishRegister;
    private LinearLayout ll_enableFingerprint;
    private Switch switch_enableFingerprint;
    private TextView tv_finishRegister_next;
    private LinearLayout ll_afterRegister;
    private TextView tv_deviceVerifiedDate;
    private ImageView iv_deviceVerified;
    private ImageView iv_photo;
    private TextView tv_electionVerifiedDate;
    private ImageView iv_electionVerified;
    private TextView tv_noneBallot;
    private ListViewForScrollView lv_ballots;
    private LinearLayout ll_results;
    private ListViewForScrollView lv_results;
    private LinearLayout ll_verifyVotes;
    private LinearLayout ll_after_enableFingerprint;
    private Switch switch_after_enableFingerprint;

    private int registerStep;
    private ProgressDialog progressDialog;
    private DataToolPackage dataToolPackage;
    private DBManager dbManager;
    private SQLiteDatabase database;
    private FileUtil fileUtil;
    private SDCardUtils sdCardUtils;
    private VoterEntity registeredVoter;
    private HashMap<String, PartyEntity> parties;
    private ArrayList<StateEntity> states;
    private StateAdapter stateAdapter;
    private ArrayList<CountyEntity> counties;
    private ArrayList<CountyEntity> stateCounties;
    private CountyAdapter countyAdapter;
    private String state;
    private String stateName;
    private String county;
    private String precinctNumber;
    private ArrayList<StateEntity> certificates;
    private StateAdapter certificatesAdapter;
    private String certificateType;
    private String certificateScanFront;
    private String certificateScanBack;
    private String faceRecognition;
    private int verificationCode;
    private boolean enableFingerprint;
    private boolean verifyFingerprint;
    private String voterID;
    private String lastName;
    private String firstName;
    private String middleName;
    private String nameSuffix;
    private String cellPhone;
    private String email;
    private String address;
    private String signature;
    private boolean requestNewCode;
    private String signatureId;
    private String certificateFrontId;
    private String certificateBackId;
    private String faceRecognitionId;

    private ArrayList<BallotEntity> ballots;
    private ArrayList<BallotEntity> sampleBallots;
    private boolean getBallots;
    private boolean getSampleBallots;
    private BallotAdapter ballotAdapter;
    private ArrayList<BallotEntity> results;
    private BallotAdapter resultAdapter;
    private ArrayList<BallotEntity> verifyVotes;

    private Secp256k1Util ecKeyUtil;
    private Map<String, Object> ecKey;

    private final int PERMISSIONS_REQUEST_ACCESS_SCAN_FRONT = 1;
    private final int PERMISSIONS_REQUEST_ACCESS_SCAN_BACK = 2;
    private final int PERMISSIONS_REQUEST_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("pachain_activity_register", "layout", getPackageName()));

        state = "";
        stateName = "";
        county = "";
        precinctNumber = "";
        certificateType = "";
        certificateScanFront = "";
        certificateScanBack = "";
        faceRecognition = "";
        verificationCode = 0;
        enableFingerprint = false;
        verifyFingerprint = false;
        registerStep = 1;
        requestNewCode = false;
        signatureId = "";
        certificateFrontId = "";
        certificateBackId = "";
        faceRecognitionId = "";
        getBallots = false;
        getSampleBallots = false;

        fileUtil = new FileUtil(this);
        sdCardUtils = new SDCardUtils(this);
        stateCounties = new ArrayList<>();
        ballots = new ArrayList<>();
        sampleBallots = new ArrayList<>();
        results = new ArrayList<>();
        verifyVotes = new ArrayList<>();

        dbManager = DBManager.getIntance(getApplicationContext());
        dataToolPackage = new DataToolPackage(getApplicationContext(), dbManager);
        registeredVoter = dataToolPackage.getRegisteredVoter();
        parties = dataToolPackage.getParties();

        ecKeyUtil = new Secp256k1Util(this);
        ecKey = ecKeyUtil.getKeyPair();
        if (!sdCardUtils.isSDCardEnableByEnvironment()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_STORAGE);
        }

        initControl();

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
    }

    private void initControl() {
        tv_back = findViewById(getResources().getIdentifier("tv_back", "id", getPackageName()));
        tv_back.setOnClickListener(this);
        tv_title = findViewById(getResources().getIdentifier("tv_title", "id", getPackageName()));
        tv_title.setText(getResources().getString(getResources().getIdentifier("common_gotv", "string", getPackageName())));
        sv_mScrollView = findViewById(getResources().getIdentifier("sv_mScrollView", "id", getPackageName()));
        ll_beforeRegister = findViewById(getResources().getIdentifier("ll_beforeRegister", "id", getPackageName()));
        ll_afterRegister = findViewById(getResources().getIdentifier("ll_afterRegister", "id", getPackageName()));
        tv_deviceVerifiedDate = findViewById(getResources().getIdentifier("tv_deviceVerifiedDate", "id", getPackageName()));
        iv_deviceVerified = findViewById(getResources().getIdentifier("iv_deviceVerified", "id", getPackageName()));
        iv_photo = findViewById(getResources().getIdentifier("iv_photo", "id", getPackageName()));
        tv_electionVerifiedDate = findViewById(getResources().getIdentifier("tv_electionVerifiedDate", "id", getPackageName()));
        iv_electionVerified = findViewById(getResources().getIdentifier("iv_electionVerified", "id", getPackageName()));

        tv_noneBallot = findViewById(getResources().getIdentifier("tv_noneBallot", "id", getPackageName()));
        lv_ballots = findViewById(getResources().getIdentifier("lv_ballots", "id", getPackageName()));
        ballotAdapter = new BallotAdapter(this, ballots, false);
        lv_ballots.setAdapter(ballotAdapter);
        lv_ballots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BallotEntity model = (BallotEntity) ballotAdapter.getItem(position);
                Intent intent = new Intent(PAChainRegisterActivity.this, PAChainBallotActivity.class);
                intent.putExtra("ballot", model);
                startActivity(intent);
            }
        });

        ll_after_enableFingerprint = findViewById(getResources().getIdentifier("ll_after_enableFingerprint", "id", getPackageName()));
        switch_after_enableFingerprint = findViewById(getResources().getIdentifier("switch_after_enableFingerprint", "id", getPackageName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (FingerManager.checkSupport(this)) {
                case DEVICE_UNSUPPORTED:
                case SUPPORT_WITHOUT_DATA:
                    ll_after_enableFingerprint.setVisibility(View.GONE);
                    break;
                case SUPPORT:
                    ll_after_enableFingerprint.setVisibility(View.VISIBLE);
                    switch_after_enableFingerprint.setChecked(registeredVoter != null ? registeredVoter.isEnableFingerprint() : false);
                    switch_after_enableFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (!registeredVoter.isEnableFingerprint()) {
                                    FingerManager.build().setApplication(getApplication())
                                            .setTitle(getResources().getString(getResources().getIdentifier("biometric_dialog_title", "string", getPackageName())))
                                            .setDes(getResources().getString(getResources().getIdentifier("biometric_dialog_subtitle", "string", getPackageName())))
                                            .setNegativeText(getResources().getString(getResources().getIdentifier("biometric_dialog_cancel", "string", getPackageName())))
                                            .setFingerCallback(new SimpleFingerCallback() {
                                                @Override
                                                public void onSucceed() {
                                                    registeredVoter.setEnableFingerprint(true);
                                                    database = dbManager.openDb();
                                                    database.execSQL("UPDATE Users SET EnableFingerprint=1 WHERE VoterID=" + registeredVoter.getVoterID());
                                                    dbManager.closeDb(database);
                                                }

                                                @Override
                                                public void onFailed() {

                                                }

                                                @Override
                                                public void onChange() {
                                                }
                                            })
                                            .create()
                                            .startListener(PAChainRegisterActivity.this);
                                }
                            } else {
                                registeredVoter.setEnableFingerprint(false);
                                database = dbManager.openDb();
                                database.execSQL("UPDATE Users SET EnableFingerprint=0 WHERE VoterID=" + registeredVoter.getVoterID());
                                dbManager.closeDb(database);
                            }
                        }
                    });
                    break;
                default:
            }
        } else {
            ll_after_enableFingerprint.setVisibility(View.GONE);
        }

        ll_results = findViewById(getResources().getIdentifier("ll_results", "id", getPackageName()));
        lv_results = findViewById(getResources().getIdentifier("lv_results", "id", getPackageName()));
        resultAdapter = new BallotAdapter(this, results, false);
        lv_results.setAdapter(resultAdapter);
        lv_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BallotEntity model = (BallotEntity) resultAdapter.getItem(position);
                Intent intent = new Intent(PAChainRegisterActivity.this, PAChainBallotResultsActivity.class);
                intent.putExtra("ballot", model);
                startActivity(intent);
            }
        });

        ll_verifyVotes = findViewById(getResources().getIdentifier("ll_verifyVotes", "id", getPackageName()));
        ll_verifyVotes.setOnClickListener(this);

        if (registeredVoter != null && registeredVoter.getVoterID() > 0) {
            ll_beforeRegister.setVisibility(View.GONE);
            ll_afterRegister.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(registeredVoter.getFacePhoto())) {
                Bitmap bm = BitmapFactory.decodeFile(fileUtil.getFilePath(registeredVoter.getFacePhoto()));
                iv_photo.setImageBitmap(bm);
            }
            if (TextUtils.isEmpty(registeredVoter.getVerifiedDate())) {
                checkVerify();
            } else {
                iv_deviceVerified.setVisibility(View.VISIBLE);
                iv_electionVerified.setVisibility(View.VISIBLE);
                tv_deviceVerifiedDate.setText(getResources().getString(getResources().getIdentifier("gotv_verified", "string", getPackageName())) + " " + registeredVoter.getVerifiedDate());
                tv_electionVerifiedDate.setText(getResources().getString(getResources().getIdentifier("gotv_verified", "string", getPackageName())) + " " + registeredVoter.getVerifiedDate());

                getBallots();
                getSampleBallots();
            }
        } else {
            ll_beforeRegister.setVisibility(View.VISIBLE);
            ll_afterRegister.setVisibility(View.GONE);

            ll_welcome = findViewById(getResources().getIdentifier("ll_welcome", "id", getPackageName()));
            et_voterId = findViewById(getResources().getIdentifier("et_voterId", "id", getPackageName()));
            tv_welcome_next = findViewById(getResources().getIdentifier("tv_welcome_next", "id", getPackageName()));
            tv_welcome_next.setOnClickListener(this);
            tv_registerVoter = findViewById(getResources().getIdentifier("tv_registerVoter", "id", getPackageName()));
            tv_registerVoter.setOnClickListener(this);
            ll_baseInfo = findViewById(getResources().getIdentifier("ll_baseInfo", "id", getPackageName()));
            sp_state = findViewById(getResources().getIdentifier("sp_state", "id", getPackageName()));

            states = dataToolPackage.getStates();
            if (states != null && states.size() > 0) {
                StateEntity stateEntity = new StateEntity();
                stateEntity.setID(0);
                stateEntity.setCode("");
                stateEntity.setName(getResources().getString(getResources().getIdentifier("register_state", "string", getPackageName())));
                states.add(0, stateEntity);
            }
            stateAdapter = new StateAdapter(this, states);
            sp_state.setAdapter(stateAdapter);
            sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    StateEntity model = (StateEntity) stateAdapter.getItem(i);
                    state = model.getCode();
                    stateName = model.getName();
                    county = "";
                    if (stateCounties != null && stateCounties.size() > 1) {
                        sp_county.setSelection(1, false);
                    }
                    getCountiesByState(state);
                    sp_county.setSelection(1, false);
                    sp_county.setSelection(0, true);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });

            sp_county = findViewById(getResources().getIdentifier("sp_county", "id", getPackageName()));
            counties = dataToolPackage.getCounties();
            CountyEntity countyEntity = new CountyEntity();
            countyEntity.setID(0);
            countyEntity.setCode("");
            countyEntity.setNumber("");
            countyEntity.setName(getResources().getString(getResources().getIdentifier("register_county", "string", getPackageName())));
            stateCounties.add(0, countyEntity);

            countyAdapter = new CountyAdapter(this, stateCounties);
            sp_county.setAdapter(countyAdapter);
            sp_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    CountyEntity model = (CountyEntity) countyAdapter.getItem(i);
                    if (i > 0) {
                        county = model.getNumber();
                    } else {
                        county = "";
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });

            et_precinctNumber = findViewById(getResources().getIdentifier("et_precinctNumber", "id", getPackageName()));
            iv_precinctQuestion = findViewById(getResources().getIdentifier("iv_precinctQuestion", "id", getPackageName()));
            iv_precinctQuestion.setOnClickListener(this);
            et_lastName = findViewById(getResources().getIdentifier("et_lastName", "id", getPackageName()));
            et_firstName = findViewById(getResources().getIdentifier("et_firstName", "id", getPackageName()));
            et_middleName = findViewById(getResources().getIdentifier("et_middleName", "id", getPackageName()));
            et_nameSuffix = findViewById(getResources().getIdentifier("et_nameSuffix", "id", getPackageName()));
            et_mobilePhone = findViewById(getResources().getIdentifier("et_mobilePhone", "id", getPackageName()));
            et_email = findViewById(getResources().getIdentifier("et_email", "id", getPackageName()));
            et_address = findViewById(getResources().getIdentifier("et_address", "id", getPackageName()));

            tv_signatureClear = findViewById(getResources().getIdentifier("tv_signatureClear", "id", getPackageName()));
            tv_signatureClear.setOnClickListener(this);
            sv_signature = findViewById(getResources().getIdentifier("sv_signature", "id", getPackageName()));
            sv_signature.setBackColor(Color.LTGRAY);
            sv_signature.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sv_mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        sv_mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                    return false;
                }
            });

            tv_baseInfo_next = findViewById(getResources().getIdentifier("tv_baseInfo_next", "id", getPackageName()));
            tv_baseInfo_next.setOnClickListener(this);
            ll_confirmScanDocument = findViewById(getResources().getIdentifier("ll_confirmScanDocument", "id", getPackageName()));
            tv_confirmScanDocument = findViewById(getResources().getIdentifier("tv_confirmScanDocument", "id", getPackageName()));
            tv_confirmScanDocument.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableStringBuilder spannable = new SpannableStringBuilder(getResources().getString(getResources().getIdentifier("register_confirmScanDocument", "string", getPackageName())));
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(getResources().getIdentifier("mainBlue", "color", getPackageName()))), 62, 65, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new YesToScanDocument(), 62, 65, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(getResources().getIdentifier("mainBlue", "color", getPackageName()))), 68, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new NoToScanDocument(), 68, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_confirmScanDocument.setText(spannable);
            ll_scanDocument = findViewById(getResources().getIdentifier("ll_scanDocument", "id", getPackageName()));
            sp_certificates = findViewById(getResources().getIdentifier("sp_certificates", "id", getPackageName()));

            certificates = new ArrayList<>();
            certificatesAdapter = new StateAdapter(this, certificates);
            sp_certificates.setAdapter(certificatesAdapter);
            sp_certificates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    StateEntity model = (StateEntity) certificatesAdapter.getItem(i);
                    certificateType = model.getName();
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });

            tv_scanFront = findViewById(getResources().getIdentifier("tv_scanFront", "id", getPackageName()));
            tv_scanFront.setOnClickListener(this);
            iv_certificateFront = findViewById(getResources().getIdentifier("iv_certificateFront", "id", getPackageName()));
            tv_scanBack = findViewById(getResources().getIdentifier("tv_scanBack", "id", getPackageName()));
            tv_scanBack.setOnClickListener(this);
            iv_certificateBack = findViewById(getResources().getIdentifier("iv_certificateBack", "id", getPackageName()));
            tv_scanDocument_next = findViewById(getResources().getIdentifier("tv_scanDocument_next", "id", getPackageName()));
            tv_scanDocument_next.setOnClickListener(this);
            ll_confirmFaceRecognition = findViewById(getResources().getIdentifier("ll_confirmFaceRecognition", "id", getPackageName()));
            tv_confirmFaceRecognition = findViewById(getResources().getIdentifier("tv_confirmFaceRecognition", "id", getPackageName()));
            tv_confirmFaceRecognition.setMovementMethod(LinkMovementMethod.getInstance());
            spannable = new SpannableStringBuilder(getResources().getString(getResources().getIdentifier("register_confirmFaceRecognition", "string", getPackageName())));
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(getResources().getIdentifier("mainBlue", "color", getPackageName()))), 70, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new YesToFaceRecognition(), 70, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(getResources().getIdentifier("mainBlue", "color", getPackageName()))), 76, 78, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new NoToFaceRecognition(), 76, 78, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_confirmFaceRecognition.setText(spannable);
            ll_successFaceRecognition = findViewById(getResources().getIdentifier("ll_successFaceRecognition", "id", getPackageName()));
            tv_faceRecognition_next = findViewById(getResources().getIdentifier("tv_faceRecognition_next", "id", getPackageName()));
            tv_faceRecognition_next.setOnClickListener(this);
            ll_verifyMobile = findViewById(getResources().getIdentifier("ll_verifyMobile", "id", getPackageName()));
            et_verificationCode = findViewById(getResources().getIdentifier("et_verificationCode", "id", getPackageName()));
            tv_verifyMobile_next = findViewById(getResources().getIdentifier("tv_verifyMobile_next", "id", getPackageName()));
            tv_verifyMobile_next.setOnClickListener(this);
            tv_requestNewCode = findViewById(getResources().getIdentifier("tv_requestNewCode", "id", getPackageName()));
            tv_requestNewCode.setOnClickListener(this);
            ll_finishRegister = findViewById(getResources().getIdentifier("ll_finishRegister", "id", getPackageName()));
            ll_enableFingerprint = findViewById(getResources().getIdentifier("ll_enableFingerprint", "id", getPackageName()));
            switch_enableFingerprint = findViewById(getResources().getIdentifier("switch_enableFingerprint", "id", getPackageName()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                switch (FingerManager.checkSupport(this)) {
                    case DEVICE_UNSUPPORTED:
                    case SUPPORT_WITHOUT_DATA:
                        ll_enableFingerprint.setVisibility(View.GONE);
                        break;
                    case SUPPORT:
                        ll_enableFingerprint.setVisibility(View.VISIBLE);
                        switch_enableFingerprint.setChecked(false);
                        switch_enableFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    enableFingerprint = true;
                                    FingerManager.build().setApplication(getApplication())
                                        .setTitle(getResources().getString(getResources().getIdentifier("biometric_dialog_title", "string", getPackageName())))
                                        .setDes(getResources().getString(getResources().getIdentifier("biometric_dialog_subtitle", "string", getPackageName())))
                                        .setNegativeText(getResources().getString(getResources().getIdentifier("biometric_dialog_cancel", "string", getPackageName())))
                                        .setFingerCallback(new SimpleFingerCallback() {
                                            @Override
                                            public void onSucceed() {
                                                Toast.makeText(PAChainRegisterActivity.this, getResources().getString(getResources().getIdentifier("biometric_dialog_state_succeeded", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                                                verifyFingerprint = true;
                                            }

                                            @Override
                                            public void onFailed() {

                                            }

                                            @Override
                                            public void onChange() {
                                            }
                                        })
                                        .create()
                                        .startListener(PAChainRegisterActivity.this);
                                } else {
                                    enableFingerprint = false;
                                    verifyFingerprint = false;
                                }
                            }
                        });
                        break;
                    default:
                }
            } else {
                ll_enableFingerprint.setVisibility(View.GONE);
            }

            tv_finishRegister_next = findViewById(getResources().getIdentifier("tv_finishRegister_next", "id", getPackageName()));
            tv_finishRegister_next.setOnClickListener(this);
        }
    }

    private class YesToScanDocument extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            ll_confirmScanDocument.setVisibility(View.GONE);
            ll_scanDocument.setVisibility(View.VISIBLE);
            registerStep++;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(true);
        }
    }

    private class NoToScanDocument extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PAChainRegisterActivity.this);
            builder.setMessage(getResources().getString(getResources().getIdentifier("register_noToScanDocument", "string", getPackageName())))
                    .setNegativeButton(getResources().getString(getResources().getIdentifier("register_continue", "string", getPackageName())), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ll_confirmScanDocument.setVisibility(View.GONE);
                            ll_scanDocument.setVisibility(View.VISIBLE);
                            registerStep++;
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(getResources().getString(getResources().getIdentifier("register_exit", "string", getPackageName())), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create().show();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(true);
        }
    }

    private class YesToFaceRecognition extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(PAChainRegisterActivity.this, PAChainFaceRecognitionActivity.class);
            intent.putExtra("path", firstName);
            startActivityForResult(intent, 1);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(true);
        }
    }

    private class NoToFaceRecognition extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PAChainRegisterActivity.this);
            builder.setMessage(getResources().getString(getResources().getIdentifier("register_noToScanDocument", "string", getPackageName())))
                    .setNegativeButton(getResources().getString(getResources().getIdentifier("register_continue", "string", getPackageName())), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PAChainRegisterActivity.this, PAChainFaceRecognitionActivity.class);
                            intent.putExtra("path", firstName);
                            startActivityForResult(intent, 1);
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(getResources().getString(getResources().getIdentifier("register_exit", "string", getPackageName())), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create().show();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(true);
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
        countyEntity.setName(getResources().getString(getResources().getIdentifier("register_county", "string", getPackageName())));
        stateCounties.add(0, countyEntity);
        countyAdapter.notifyDataSetChanged();
    }

    private void register() {
        showProgressDialog();
        List<String> params = new ArrayList<>();
        String base64PublicKey = Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP);
        params.add("publicKey=" + URLEncoder.encode(base64PublicKey));
        try {
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(base64PublicKey, (PrivateKey) ecKey.get("privateKey"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.add("&appAuthorizationId=" + "1");
        params.add("&voterId=" + URLEncoder.encode(voterID));
        params.add("&lastName=" + URLEncoder.encode(lastName));
        params.add("&firstName=" + URLEncoder.encode(firstName));
        params.add("&middleName=" + URLEncoder.encode(middleName));
        params.add("&nameSuffix=" + URLEncoder.encode(nameSuffix));
        params.add("&state=" + state);
        params.add("&county=" + county);
        params.add("&precinctNumber=" + URLEncoder.encode(precinctNumber));
        params.add("&cellphone=" + URLEncoder.encode(cellPhone));
        params.add("&email=" + URLEncoder.encode(email));
        params.add("&address=" + URLEncoder.encode(address));
        params.add("&certificateType=" + URLEncoder.encode(certificateType));
        params.add("&images=" + signatureId + "," + certificateFrontId + "," + certificateBackId + "," + faceRecognitionId);
        PostApi api = new PostApi(Config.REGISTER, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) {

            }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        database = dbManager.openDb();
                        database.execSQL("REPLACE INTO Users(VoterID, PublicKey, State, County, PrecinctNumber, FirstName, MiddleName, LastName, NameSuffix, " +
                            "CellPhone, Email, Address, Signature, CertificateType, CertificateFront, CertificateBack, FacePhoto, EnableFingerprint, RegisteredDate) " +
                            "VALUES(" + voterID + ", '" + Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP) + "', '" + state + "', '" + county.replace("'", "''") + "', '" +
                            precinctNumber.replace("'", "''") + "', '" + firstName.replace("'", "''") + "', '" + middleName.replace("'", "''") + "', '" + lastName.replace("'", "''") + "', '" +
                            nameSuffix.replace("'", "''") + "', '" + cellPhone + "', '" + email + "', '" + address.replace("'", "''") + "', '" + signature.replace("'", "''") + "', '" + certificateType.replace("'", "''") + "', '" +
                            certificateScanFront.replace("'", "''") + "', '" + certificateScanBack.replace("'", "''") + "', '" + faceRecognition.replace("'", "''") + "', " + (enableFingerprint ? 1 : 0) + ", '" +
                            ToolPackage.ConvertToStringNow() + "');");
                        dbManager.closeDb(database);

                        String responseEncrypt = json.getString("response");
                        String response = ecKeyUtil.decryptByPrivateKey(responseEncrypt, (PrivateKey) ecKey.get("privateKey"));
                        JSONObject object = new JSONObject(response);

                        String accessToken = object.getString("accessToken");
                        SPUtils.put(PAChainRegisterActivity.this, "accessToken", accessToken);

                        database = dbManager.openDb();
                        database.execSQL("UPDATE Users SET AccessToken='" + accessToken + "' WHERE VoterID=" + voterID);
                        dbManager.closeDb(database);

                        registeredVoter = dataToolPackage.getRegisteredVoter();
                        if (registeredVoter.isEnableFingerprint()) {
                            switch_after_enableFingerprint.setChecked(true);
                        }

                        if (!TextUtils.isEmpty(registeredVoter.getFacePhoto())) {
                            Bitmap bm = BitmapFactory.decodeFile(fileUtil.getFilePath(registeredVoter.getFacePhoto()));
                            iv_photo.setImageBitmap(bm);
                        }
                        ll_beforeRegister.setVisibility(View.GONE);
                        ll_afterRegister.setVisibility(View.VISIBLE);
                        checkVerify();
                    } else {
                        if (json.getString("error").toLowerCase().indexOf("exists") > -1) {
                            Toast.makeText(PAChainRegisterActivity.this, getResources().getString(getResources().getIdentifier("register_already", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PAChainRegisterActivity.this, getResources().getString(getResources().getIdentifier("network_unavailable", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    closeProgressDialog();
                    Toast.makeText(PAChainRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                closeProgressDialog();
            }

            @Override
            public void onFailed(String error) {
                closeProgressDialog();
                Toast.makeText(PAChainRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                closeProgressDialog();
            }
        });
        api.call();
    }

    private void sendVerificationCode() {
        List<String> params = new ArrayList<>();
        verificationCode = (int) ((Math.random() * 9 + 1) * 100000);
        String base64PublicKey = Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP);
        params.add("publicKey=" + URLEncoder.encode(base64PublicKey));
        try {
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(base64PublicKey, (PrivateKey) ecKey.get("privateKey"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.add("&to=1" + cellPhone);
        params.add("&message=" + verificationCode);
        PostApi api = new PostApi(Config.SENDVERIFICATIONCODE, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) {

            }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        if (requestNewCode) {
                            Toast.makeText(PAChainRegisterActivity.this, getResources().getString(getResources().getIdentifier("register_requestNewCodeSuccess", "string", getPackageName())), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(PAChainRegisterActivity.this, json.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                requestNewCode = false;
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PAChainRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                requestNewCode = false;
            }
        });
        api.call();
    }

    private void checkVerify() {
        List<String> params = new ArrayList<>();
        String accessToken = SPUtils.getString(PAChainRegisterActivity.this, "accessToken", "");
        params.add("accessToken=" + URLEncoder.encode(accessToken));
        try {
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(accessToken, (PrivateKey) ecKey.get("privateKey"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.VERIFYVOTER, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) throws Exception {

            }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        String responseEncrypt = json.getString("response");
                        String response = ecKeyUtil.decryptByPrivateKey(responseEncrypt, (PrivateKey) ecKey.get("privateKey"));
                        JSONObject object = new JSONObject(response);

                        String publickey = object.getString("publickey");
                        String signature = object.getString("signature");
                        String approvedTimestamp = object.getString("approved");
                        String approvedDate = ToolPackage.ConvertToStringByTime(approvedTimestamp);
                        if (ecKeyUtil.verifySignature(publickey, signature, ecKeyUtil.getPublicKeyFromString(publickey))) {
                            SPUtils.put(PAChainRegisterActivity.this, "serverPublicKey", publickey);

                            database = dbManager.openDb();
                            database.execSQL("UPDATE Users SET VerifiedDate='" + approvedDate + "' WHERE VoterID=" + registeredVoter.getVoterID());
                            dbManager.closeDb(database);

                            registeredVoter.setVerifiedDate(approvedDate);
                            iv_deviceVerified.setVisibility(View.VISIBLE);
                            iv_electionVerified.setVisibility(View.VISIBLE);
                            tv_deviceVerifiedDate.setText(getResources().getString(getResources().getIdentifier("gotv_verified", "string", getPackageName())) + " " + approvedDate);
                            tv_electionVerifiedDate.setText(getResources().getString(getResources().getIdentifier("gotv_verified", "string", getPackageName())) + " " + approvedDate);
                            getBallots();
                            getSampleBallots();
                        }
                    } else {
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PAChainRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                closeProgressDialog();
            }
        });
        api.call();
    }

    private void uploadPhoto(final String type, String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(fileUtil.getFilePath(filePath));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        List<String> params = new ArrayList<>();
        try {
            String base64PublicKey = Base64.encodeToString(((PublicKey) ecKey.get("publicKey")).getEncoded(), Base64.NO_WRAP);
            params.add("publicKey=" + URLEncoder.encode(base64PublicKey));
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(base64PublicKey, (PrivateKey) ecKey.get("privateKey"))));
            params.add("&voterID=" + "");
            params.add("&type=" + type);
            params.add("&image=" + URLEncoder.encode(Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.UPLOADPHOTO, params);
        api.setOnApiListener(new PostApi.onApiListener() {
            @Override
            public void onExecute(String content) {

            }

            @Override
            public void onSuccessed(String successed) {
                try {
                    JSONObject json = new JSONObject(successed);
                    if (json.getBoolean("ret")) {
                        switch (type) {
                            case "Signature":
                                signatureId = json.getInt("id") + "";
                            case "CertificateFront":
                                certificateFrontId = json.getInt("id") + "";
                            case "CertificateBack":
                                certificateBackId = json.getInt("id") + "";
                            case "FaceRecognition":
                                faceRecognitionId = json.getInt("id") + "";
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PAChainRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                closeProgressDialog();
            }
        });
        api.call();
    }

    private void getBallots() {
        List<String> params = new ArrayList<>();
        try {
            params.add("accessToken=" + URLEncoder.encode(SPUtils.getString(PAChainRegisterActivity.this, "accessToken", "")));
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(SPUtils.getString(PAChainRegisterActivity.this, "accessToken", ""), (PrivateKey) ecKey.get("privateKey"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.BALLOTS, params);
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
                        JSONArray ballotsArray = new JSONArray(response);
                        JSONObject object;
                        BallotEntity ballotEntity;
                        CandidateEntity candidateEntity;
                        ArrayList<CandidateEntity> candidates;
                        for (int i = 0; i < ballotsArray.length(); i++) {
                            object = ballotsArray.getJSONObject(i);
                            candidates = new ArrayList<>();
                            ballotEntity = new BallotEntity();
                            ballotEntity.setDate(ToolPackage.ConvertToStringByDate(object.getString("ballotdate")));
                            ballotEntity.setName(object.getString("ballotname"));
                            ballotEntity.setNumber(object.getString("ballotno"));
                            ballotEntity.setVoted(object.getBoolean("isvoted"));
                            ballotEntity.setVotingDate(ToolPackage.ConvertToStringByDate(object.getString("votingdate")));
                            ballotEntity.setExceededVoting(ToolPackage.ComparedDateWithNow(object.getString("ballotdate")));
                            ballotEntity.setStartCounting(object.getBoolean("isopenvoting"));
                            ballotEntity.setVerified(object.getBoolean("isconfirm"));
                            ballotEntity.setVerifyDate(object.getString("confirmdate"));

                            JSONArray electionsArray = new JSONArray(object.getString("elections"));
                            for (int j = 0; j < electionsArray.length(); j++) {
                                JSONObject electionObject = electionsArray.getJSONObject(j);
                                JSONObject election = new JSONObject(electionObject.getString("election"));
                                candidateEntity = new CandidateEntity();
                                candidateEntity.setElectionID(election.getInt("electionid"));
                                candidateEntity.setElectionName(election.getString("electionname"));
                                candidateEntity.setElectionDate(ToolPackage.ConvertToStringByDate(election.getString("electiondate")));
                                candidateEntity.setElectionState(election.getString("electionstate"));
                                candidateEntity.setVoted(ballotEntity.isVoted());
                                candidateEntity.setExceededVoting(ballotEntity.isExceededVoting());
                                ballotEntity.setElection((!TextUtils.isEmpty(ballotEntity.getElection()) ? "," : "") + candidateEntity.getElectionID());
                                candidates.add(candidateEntity);

                                JSONArray seatsArray = new JSONArray(electionObject.getString("seats"));
                                for (int m = 0; m < seatsArray.length(); m++) {
                                    JSONObject seatObject = seatsArray.getJSONObject(m);
                                    JSONObject seat = new JSONObject(seatObject.getString("seat"));
                                    candidateEntity = new CandidateEntity();
                                    candidateEntity.setSeatID(seat.getInt("seatid"));
                                    candidateEntity.setSeatOffice(seat.getString("office"));
                                    candidateEntity.setSeatNumber(seat.getString("number"));
                                    candidateEntity.setSeatName((!TextUtils.isEmpty(candidateEntity.getSeatNumber()) ? candidateEntity.getSeatOffice() + " " : "") + seat.getString("name"));
                                    candidateEntity.setSeatState(seat.getString("state"));
                                    candidateEntity.setSeatCounty(seat.getString("county"));
                                    candidateEntity.setSeatCity(seat.getString("city"));
                                    candidateEntity.setSeatLevel(seat.getInt("level"));
                                    candidates.add(candidateEntity);

                                    JSONArray candidatesArray = new JSONArray(seatObject.getString("candidates"));
                                    for (int n = 0; n < candidatesArray.length(); n++) {
                                        JSONObject candidateObject = candidatesArray.getJSONObject(n);
                                        candidateEntity = new CandidateEntity();
                                        candidateEntity.setElectionID(election.getInt("electionid"));
                                        candidateEntity.setSeatID(seat.getInt("seatid"));
                                        candidateEntity.setSeatName(seat.getString("name"));
                                        candidateEntity.setSeatNumber(seat.getString("number"));
                                        candidateEntity.setSeatState(seat.getString("state"));
                                        candidateEntity.setSeatCounty(seat.getString("county"));
                                        candidateEntity.setSeatCity(seat.getString("city"));
                                        candidateEntity.setSeatOffice(seat.getString("office"));
                                        candidateEntity.setSeatLevel(seat.getInt("level"));
                                        candidateEntity.setID(candidateObject.getInt("candidateid"));
                                        candidateEntity.setName(candidateObject.getString("name"));
                                        candidateEntity.setParty(candidateObject.getString("party"));
                                        candidateEntity.setPartyCode("");
                                        if (parties.containsKey(candidateEntity.getParty().toLowerCase())) {
                                            candidateEntity.setPartyCode(parties.get(candidateEntity.getParty().toLowerCase()).getCode());
                                        }
                                        if (!TextUtils.isEmpty(candidateEntity.getParty()) && TextUtils.isEmpty(candidateEntity.getPartyCode())) {
                                            candidateEntity.setPartyCode("O");
                                        }
                                        candidateEntity.setPhoto(candidateObject.getString("photo"));
                                        candidateEntity.setExceededVoting(ballotEntity.isExceededVoting());
                                        candidates.add(candidateEntity);
                                    }
                                }
                            }
                            ballotEntity.setCandidates(candidates);
                            ballots.add(ballotEntity);
                            if (ballotEntity.isVoted()) {
                                verifyVotes.add(ballotEntity);
                            }
                            if (ballotEntity.isStartCounting()) {
                                results.add(ballotEntity);
                            }
                        }
                        if (results != null && results.size() > 0) {
                            resultAdapter.notifyDataSetChanged();
                            ll_results.setVisibility(View.VISIBLE);
                        }
                        if (verifyVotes != null && verifyVotes.size() > 0) {
                            ll_verifyVotes.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                getBallots = true;
                displayBallots();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PAChainRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                getBallots = true;
                displayBallots();
            }
        });
        api.call();
    }

    private void getSampleBallots() {
        List<String> params = new ArrayList<>();
        try {
            params.add("accessToken=" + URLEncoder.encode(SPUtils.getString(PAChainRegisterActivity.this, "accessToken", "")));
            params.add("&signature=" + URLEncoder.encode(ecKeyUtil.signByPrivateKey(SPUtils.getString(PAChainRegisterActivity.this, "accessToken", ""), (PrivateKey) ecKey.get("privateKey"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostApi api = new PostApi(Config.SAMPLEBALLOTS, params);
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
                        BallotEntity ballotEntity;
                        CandidateEntity candidateEntity;
                        ArrayList<CandidateEntity> candidates;

                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("ret") && object.has("data")) {
                            object = new JSONObject(object.getString("data"));
                            candidates = new ArrayList<>();
                            ballotEntity = new BallotEntity();
                            ballotEntity.setDate(ToolPackage.ConvertToStringByDate(object.getString("ballotdate")));
                            ballotEntity.setName(object.getString("ballotname"));
                            ballotEntity.setNumber("");
                            ballotEntity.setSample(true);

                            JSONArray electionsArray = new JSONArray(object.getString("elections"));
                            for (int j = 0; j < electionsArray.length(); j++) {
                                JSONObject electionObject = electionsArray.getJSONObject(j);
                                JSONObject election = new JSONObject(electionObject.getString("election"));
                                candidateEntity = new CandidateEntity();
                                candidateEntity.setElectionID(election.getInt("electionid"));
                                candidateEntity.setElectionName(election.getString("electionname"));
                                candidateEntity.setElectionDate(ToolPackage.ConvertToStringByDate(election.getString("electiondate")));
                                candidateEntity.setElectionState(election.getString("electionstate"));
                                candidateEntity.setSampleBallot(ballotEntity.isSample());
                                candidates.add(candidateEntity);

                                JSONArray seatsArray = new JSONArray(electionObject.getString("seats"));
                                for (int m = 0; m < seatsArray.length(); m++) {
                                    JSONObject seatObject = seatsArray.getJSONObject(m);
                                    JSONObject seat = new JSONObject(seatObject.getString("seat"));
                                    candidateEntity = new CandidateEntity();
                                    candidateEntity.setSeatID(seat.getInt("seatid"));
                                    candidateEntity.setSeatOffice(seat.getString("office"));
                                    candidateEntity.setSeatNumber(seat.getString("number"));
                                    candidateEntity.setSeatName((!TextUtils.isEmpty(candidateEntity.getSeatNumber()) ? candidateEntity.getSeatOffice() + " " : "") + seat.getString("name"));
                                    candidateEntity.setSeatState(seat.getString("state"));
                                    candidateEntity.setSeatCounty(seat.getString("county"));
                                    candidateEntity.setSeatCity(seat.getString("city"));
                                    candidateEntity.setSeatLevel(seat.getInt("level"));
                                    candidates.add(candidateEntity);

                                    JSONArray candidatesArray = new JSONArray(seatObject.getString("candidates"));
                                    for (int n = 0; n < candidatesArray.length(); n++) {
                                        JSONObject candidateObject = candidatesArray.getJSONObject(n);
                                        candidateEntity = new CandidateEntity();
                                        candidateEntity.setElectionID(election.getInt("electionid"));
                                        candidateEntity.setSeatID(seat.getInt("seatid"));
                                        candidateEntity.setSeatName(seat.getString("name"));
                                        candidateEntity.setSeatNumber(seat.getString("number"));
                                        candidateEntity.setSeatState(seat.getString("state"));
                                        candidateEntity.setSeatCounty(seat.getString("county"));
                                        candidateEntity.setSeatCity(seat.getString("city"));
                                        candidateEntity.setSeatOffice(seat.getString("office"));
                                        candidateEntity.setSeatLevel(seat.getInt("level"));
                                        candidateEntity.setID(candidateObject.getInt("candidateid"));
                                        candidateEntity.setName(candidateObject.getString("name"));
                                        candidateEntity.setParty(candidateObject.getString("party"));
                                        candidateEntity.setPartyCode("");
                                        if (parties.containsKey(candidateEntity.getParty().toLowerCase())) {
                                            candidateEntity.setPartyCode(parties.get(candidateEntity.getParty().toLowerCase()).getCode());
                                        }
                                        if (!TextUtils.isEmpty(candidateEntity.getParty()) && TextUtils.isEmpty(candidateEntity.getPartyCode())) {
                                            candidateEntity.setPartyCode("O");
                                        }
                                        candidateEntity.setPhoto(candidateObject.getString("photo"));
                                        candidateEntity.setSampleBallot(ballotEntity.isSample());
                                        candidates.add(candidateEntity);
                                    }
                                }

                                ballotEntity.setCandidates(candidates);
                                sampleBallots.add(ballotEntity);
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(PAChainRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                getSampleBallots = true;
                displayBallots();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(PAChainRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                getSampleBallots = true;
                displayBallots();
            }
        });
        api.call();
    }

    private void displayBallots() {
        if (getBallots && getSampleBallots) {
            boolean isExists;
            for (BallotEntity sampleBallot : sampleBallots) {
                isExists = false;
                for (BallotEntity ballot : ballots) {
                    if (ballot.getCandidates() != null && sampleBallot.getCandidates() != null && ballot.getCandidates().get(0).getElectionID() == sampleBallot.getCandidates().get(0).getElectionID()) {
                        isExists = true;
                    }
                }
                if (!isExists) {
                    ballots.add(0, sampleBallot);
                }
            }

            if (ballots != null && ballots.size() > 0) {
                ballotAdapter.notifyDataSetChanged();
                tv_noneBallot.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm;
        Intent intent;
        if (v.getId() == getResources().getIdentifier("tv_welcome_next", "id", getPackageName())) {
            voterID = et_voterId.getText().toString().trim();
            if (TextUtils.isEmpty(voterID)) {
                et_voterId.setError(getResources().getString(getResources().getIdentifier("register_emptyVoterId", "string", getPackageName())));
                return;
            } else if (!ToolPackage.isNumeric(voterID)) {
                et_voterId.setError(getResources().getString(getResources().getIdentifier("register_errorVoterId", "string", getPackageName())));
                return;
            }

            ll_welcome.setVisibility(View.GONE);
            ll_baseInfo.setVisibility(View.VISIBLE);
            registerStep++;

            if (isKeyboardShown()) {
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } else if (v.getId() == getResources().getIdentifier("tv_baseInfo_next", "id", getPackageName())) {
            precinctNumber = et_precinctNumber.getText().toString().trim();
            lastName = et_lastName.getText().toString().trim();
            firstName = et_firstName.getText().toString().trim();
            middleName = et_middleName.getText().toString().trim();
            nameSuffix = et_nameSuffix.getText().toString().trim();
            cellPhone = et_mobilePhone.getText().toString().trim();
            email = et_email.getText().toString().trim();
            address = et_address.getText().toString().trim();

            if (TextUtils.isEmpty(state)) {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_emptyState", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(county)) {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_emptyCounty", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(precinctNumber)) {
                et_precinctNumber.setError(getResources().getString(getResources().getIdentifier("register_emptyPrecinctNumber", "string", getPackageName())));
                return;
            }
            if (TextUtils.isEmpty(lastName)) {
                et_lastName.setError(getResources().getString(getResources().getIdentifier("register_emptyLastName", "string", getPackageName())));
                return;
            }
            if (TextUtils.isEmpty(firstName)) {
                et_firstName.setError(getResources().getString(getResources().getIdentifier("register_emptyFirstName", "string", getPackageName())));
                return;
            }
            if (TextUtils.isEmpty(cellPhone)) {
                et_mobilePhone.setError(getResources().getString(getResources().getIdentifier("register_emptyMobilePhone", "string", getPackageName())));
                return;
            } else if (!ToolPackage.isNumeric(cellPhone)) {
                et_mobilePhone.setError(getResources().getString(getResources().getIdentifier("register_errorMobilePhone", "string", getPackageName())));
                return;
            } else if (cellPhone.length() != 10) {
                et_mobilePhone.setError(getResources().getString(getResources().getIdentifier("register_errorMobilePhone", "string", getPackageName())));
                return;
            }
            if (TextUtils.isEmpty(email)) {
                et_email.setError(getResources().getString(getResources().getIdentifier("register_emptyEmail", "string", getPackageName())));
                return;
            } else if (!ToolPackage.isEmail(email)) {
                et_email.setError(getResources().getString(getResources().getIdentifier("register_errorEmail", "string", getPackageName())));
                return;
            }
            if (TextUtils.isEmpty(address)) {
                et_address.setError(getResources().getString(getResources().getIdentifier("register_emptyAddress", "string", getPackageName())));
                return;
            }
            if (sv_signature.getTouched()) {
                try {
                    sv_signature.save(this, firstName + "/", "signature.png");
                    setResult(100);
                    signature = firstName + "/" + "signature.png";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_signature", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                return;
            }

            ll_baseInfo.setVisibility(View.GONE);
            ll_confirmScanDocument.setVisibility(View.VISIBLE);
            registerStep++;

            StateEntity stateEntity = new StateEntity();
            stateEntity.setID(1);
            stateEntity.setCode("");
            stateEntity.setName(stateName + " " + getResources().getString(getResources().getIdentifier("register_driverLicense", "string", getPackageName())));
            certificates.add(stateEntity);
            certificatesAdapter.notifyDataSetChanged();

            uploadPhoto("Signature", signature);

            if (isKeyboardShown()) {
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } else if (v.getId() == getResources().getIdentifier("tv_signatureClear", "id", getPackageName())) {
            sv_signature.clear();
        } else if (v.getId() == getResources().getIdentifier("tv_scanFront", "id", getPackageName())) {
            if (ActivityCompat.checkSelfPermission(PAChainRegisterActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                gotoScanFront();
            } else {
                ActivityCompat.requestPermissions(PAChainRegisterActivity.this,
                    new String[]{android.Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_SCAN_FRONT);
            }
        } else if (v.getId() == getResources().getIdentifier("tv_scanBack", "id", getPackageName())) {
            if (ActivityCompat.checkSelfPermission(PAChainRegisterActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                gotoScanBack();
            } else {
                ActivityCompat.requestPermissions(PAChainRegisterActivity.this,
                    new String[]{android.Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_SCAN_BACK);
            }
        } else if (v.getId() == getResources().getIdentifier("tv_scanDocument_next", "id", getPackageName())) {
            if (TextUtils.isEmpty(certificateType)) {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_emptyCertificate", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(certificateScanFront)) {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_noScanFront", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(certificateScanBack)) {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_noScanBack", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextUtils.isEmpty(certificateScanFront)) {
                uploadPhoto("CertificateFront", certificateScanFront);
            }
            if (!TextUtils.isEmpty(certificateScanBack)) {
                uploadPhoto("CertificateBack", certificateScanBack);
            }
            ll_scanDocument.setVisibility(View.GONE);
            ll_confirmFaceRecognition.setVisibility(View.VISIBLE);
            registerStep++;
        } else if (v.getId() == getResources().getIdentifier("tv_faceRecognition_next", "id", getPackageName())) {
            ll_successFaceRecognition.setVisibility(View.GONE);
            ll_verifyMobile.setVisibility(View.VISIBLE);
            registerStep++;
            if (verificationCode < 1) {
                sendVerificationCode();
            }
        } else if (v.getId() == getResources().getIdentifier("tv_verifyMobile_next", "id", getPackageName())) {
            if (TextUtils.isEmpty(et_verificationCode.getText().toString().trim())) {
                et_verificationCode.setError(getResources().getString(getResources().getIdentifier("register_emptyVerificationCode", "string", getPackageName())));
                return;
            } else if (!et_verificationCode.getText().toString().trim().equals(verificationCode + "")) {
                et_verificationCode.setError(getResources().getString(getResources().getIdentifier("register_errorVerificationCode", "string", getPackageName())));
                return;
            }

            ll_verifyMobile.setVisibility(View.GONE);
            ll_finishRegister.setVisibility(View.VISIBLE);
            registerStep++;
        } else if (v.getId() == getResources().getIdentifier("tv_requestNewCode", "id", getPackageName())) {
            requestNewCode = true;
            tv_requestNewCode.setEnabled(false);
            sendVerificationCode();
        } else if (v.getId() == getResources().getIdentifier("tv_finishRegister_next", "id", getPackageName())) {
            if (enableFingerprint && !verifyFingerprint) {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_errorFingerprint", "string", getPackageName())), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                register();
            } catch (Exception e) {
                e.printStackTrace();
                closeProgressDialog();
            }
            if (isKeyboardShown()) {
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } else if (v.getId() == getResources().getIdentifier("tv_back", "id", getPackageName())) {
            if (registeredVoter != null && registeredVoter.getVoterID() > 0) {
                finish();
            } else {
                switch (registerStep) {
                    case 1:
                        finish();
                        break;
                    case 2:
                        ll_welcome.setVisibility(View.VISIBLE);
                        ll_baseInfo.setVisibility(View.GONE);
                        break;
                    case 3:
                        ll_baseInfo.setVisibility(View.VISIBLE);
                        ll_confirmScanDocument.setVisibility(View.GONE);
                        break;
                    case 4:
                        ll_confirmScanDocument.setVisibility(View.VISIBLE);
                        ll_scanDocument.setVisibility(View.GONE);
                        break;
                    case 5:
                        ll_scanDocument.setVisibility(View.VISIBLE);
                        ll_confirmFaceRecognition.setVisibility(View.GONE);
                        break;
                    case 6:
                        ll_confirmFaceRecognition.setVisibility(View.VISIBLE);
                        ll_successFaceRecognition.setVisibility(View.GONE);
                        break;
                    case 7:
                        ll_successFaceRecognition.setVisibility(View.VISIBLE);
                        ll_verifyMobile.setVisibility(View.GONE);
                        break;
                    case 8:
                        ll_verifyMobile.setVisibility(View.VISIBLE);
                        ll_finishRegister.setVisibility(View.GONE);
                        break;
                }
                registerStep--;
            }
        } else if (v.getId() == getResources().getIdentifier("ll_verifyVotes", "id", getPackageName())) {
            intent = new Intent(this, PAChainVerifyVotesActivity.class);
            intent.putExtra("ballots", verifyVotes);
            startActivity(intent);
        } else if (v.getId() == getResources().getIdentifier("tv_registerVoter", "id", getPackageName())) {
            intent = new Intent(this, PAChainRegisterReferenceDialogActivity.class);
            startActivity(intent);
        }  else if (v.getId() == getResources().getIdentifier("iv_precinctQuestion", "id", getPackageName())) {
            if (!TextUtils.isEmpty(state) && !TextUtils.isEmpty(county)) {
                String link = dataToolPackage.getOfficialsLink(state, county);
                if (!TextUtils.isEmpty(link)) {
                    Uri uri = Uri.parse(link);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_questionPrecinctNumber", "string", getPackageName())), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoScanFront() {
        Intent intent = new Intent(this, PAChainCertificateActivity.class);
        intent.putExtra("path", firstName);
        intent.putExtra("type", "certificateFront");
        startActivityForResult(intent, 1);
    }

    private void gotoScanBack() {
        Intent intent = new Intent(this, PAChainCertificateActivity.class);
        intent.putExtra("path", firstName);
        intent.putExtra("type", "certificateBack");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_SCAN_FRONT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoScanFront();
                } else {
                    Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_cameraPermissionDenied", "string", getPackageName())), Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSIONS_REQUEST_ACCESS_SCAN_BACK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoScanBack();
                } else {
                    Toast.makeText(this, getResources().getString(getResources().getIdentifier("register_cameraPermissionDenied", "string", getPackageName())), Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSIONS_REQUEST_STORAGE:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getExtras() == null) {
            return;
        }
        Bundle bundle = data.getExtras();
        if (bundle.containsKey("type")) {
            if (bundle.getString("type").equals("certificateFront")) {
                Bitmap bm = BitmapFactory.decodeFile(fileUtil.getFilePath(bundle.getString("path"), bundle.getString("fileName")));
                iv_certificateFront.setImageBitmap(bm);
                certificateScanFront = bundle.getString("path") + bundle.getString("fileName");
            } else if (bundle.getString("type").equals("certificateBack")) {
                Bitmap bm = BitmapFactory.decodeFile(fileUtil.getFilePath(bundle.getString("path"), bundle.getString("fileName")));
                iv_certificateBack.setImageBitmap(bm);
                certificateScanBack = bundle.getString("path") + bundle.getString("fileName");
            } else if (bundle.getString("type").equals("faceRecognition")) {
                faceRecognition = bundle.getString("path") + bundle.getString("fileName");
                if (!TextUtils.isEmpty(faceRecognition)) {
                    ll_confirmFaceRecognition.setVisibility(View.GONE);
                    ll_successFaceRecognition.setVisibility(View.VISIBLE);
                    registerStep++;
                    uploadPhoto("FaceRecognition", faceRecognition);
                }
            }
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
        if (registeredVoter != null && registeredVoter.getVoterID() > 0) {
            finish();
        } else {
            switch (registerStep) {
                case 1:
                    finish();
                    break;
                case 2:
                    ll_welcome.setVisibility(View.VISIBLE);
                    ll_baseInfo.setVisibility(View.GONE);
                    break;
                case 3:
                    ll_baseInfo.setVisibility(View.VISIBLE);
                    ll_confirmScanDocument.setVisibility(View.GONE);
                    break;
                case 4:
                    ll_confirmScanDocument.setVisibility(View.VISIBLE);
                    ll_scanDocument.setVisibility(View.GONE);
                    break;
                case 5:
                    ll_scanDocument.setVisibility(View.VISIBLE);
                    ll_confirmFaceRecognition.setVisibility(View.GONE);
                    break;
                case 6:
                    ll_confirmFaceRecognition.setVisibility(View.VISIBLE);
                    ll_successFaceRecognition.setVisibility(View.GONE);
                    break;
                case 7:
                    ll_successFaceRecognition.setVisibility(View.VISIBLE);
                    ll_verifyMobile.setVisibility(View.GONE);
                    break;
                case 8:
                    ll_verifyMobile.setVisibility(View.VISIBLE);
                    ll_finishRegister.setVisibility(View.GONE);
                    break;
            }
            registerStep--;
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(PAChainRegisterActivity.this);
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
