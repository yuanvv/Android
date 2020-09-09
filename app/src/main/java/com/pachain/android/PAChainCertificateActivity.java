package com.pachain.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.pachain.android.tool.CameraSurfaceView;

public class PAChainCertificateActivity extends Activity implements View.OnClickListener {
    private Button button;
    private CameraSurfaceView mCameraSurfaceView;
    private String type;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getResources().getIdentifier("pachain_activity_certificate", "layout", getPackageName()));
        mCameraSurfaceView = findViewById(getResources().getIdentifier("cameraSurfaceView", "id", getPackageName()));
        button = findViewById(getResources().getIdentifier("takePic", "id", getPackageName()));

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");
        path = bundle.getString("path");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.path = path;
                mCameraSurfaceView.fileName = type;
                mCameraSurfaceView.takePicture();

                mCameraSurfaceView.setOnPathChangedListener(new CameraSurfaceView.OnPathChangedListener() {
                    @Override
                    public void onValueChange(String path, String fileName) {
                        Intent intent = new Intent();
                        intent.putExtra("path", path);
                        intent.putExtra("fileName", fileName);
                        intent.putExtra("type", type);
                        setResult(0, intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("takePic", "id", getPackageName())) {
            mCameraSurfaceView.takePicture();
        }
    }

    /*@Override
    public void autoFocus() {
        mCameraSurfaceView.setAutoFocus();
    }*/
}
