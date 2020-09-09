package com.pachain.android;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import com.pachain.android.adapter.CountyAdapter;
import com.pachain.android.adapter.StateAdapter;
import com.pachain.android.common.DataToolPackage;
import com.pachain.android.entity.CountyEntity;
import com.pachain.android.entity.StateEntity;
import com.pachain.android.tool.DBManager;
import java.util.ArrayList;
import androidx.annotation.Nullable;

public class PAChainRegisterReferenceDialogActivity extends Activity implements View.OnClickListener {
    private Spinner sp_state;
    private Spinner sp_county;
    private TextView tv_go;

    private DataToolPackage dataToolPackage;
    private DBManager dbManager;
    private SQLiteDatabase database;
    private ArrayList<StateEntity> states;
    private StateAdapter stateAdapter;
    private ArrayList<CountyEntity> counties;
    private ArrayList<CountyEntity> stateCounties;
    private CountyAdapter countyAdapter;
    private String state;
    private String county;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("pachain_activity_registerreferencedialog", "layout", getPackageName()));

        sp_state = findViewById(getResources().getIdentifier("sp_state", "id", getPackageName()));
        sp_county = findViewById(getResources().getIdentifier("sp_county", "id", getPackageName()));
        tv_go = findViewById(getResources().getIdentifier("tv_go", "id", getPackageName()));
        tv_go.setOnClickListener(this);

        dbManager = DBManager.getIntance(getApplicationContext());
        dataToolPackage = new DataToolPackage(getApplicationContext(), dbManager);
        stateCounties = new ArrayList<>();

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

    @Override
    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("tv_go", "id", getPackageName())) {
            if (!TextUtils.isEmpty(state) && !TextUtils.isEmpty(county)) {
                String link = dataToolPackage.getRegisterLink(state, county);
                if (!TextUtils.isEmpty(link)) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }
}
