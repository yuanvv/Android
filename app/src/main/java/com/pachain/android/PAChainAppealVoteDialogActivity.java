package com.pachain.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.pachain.android.common.DataToolPackage;
import com.pachain.android.tool.DBManager;
import androidx.annotation.Nullable;

public class PAChainAppealVoteDialogActivity extends Activity implements View.OnClickListener {
    private TextView tv_ok;
    private TextView tv_tip;

    private String state;
    private String county;
    private String link;

    private DataToolPackage dataToolPackage;
    private DBManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("pachain_activity_appealvotedialog", "layout", getPackageName()));

        tv_tip = findViewById(getResources().getIdentifier("tv_tip", "id", getPackageName()));
        tv_ok = findViewById(getResources().getIdentifier("tv_ok", "id", getPackageName()));
        tv_ok.setOnClickListener(this);

        dbManager = DBManager.getIntance(getApplicationContext());
        dataToolPackage = new DataToolPackage(getApplicationContext(), dbManager);
        state = getIntent().getExtras().getString("state");
        county = getIntent().getExtras().getString("county");
        link = dataToolPackage.getSOELink(state, county);

        if (!TextUtils.isEmpty(link)) {
            tv_tip.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableStringBuilder spannable = new SpannableStringBuilder(getResources().getString(getResources().getIdentifier("verifyVote_appeal", "string", getPackageName())));
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(getResources().getIdentifier("mainBlue", "color", getPackageName()))), 70, 84, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new Goto(), 70, 84, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_tip.setText(spannable);
        } else {
            tv_tip.setText(getResources().getString(getResources().getIdentifier("verifyVote_appeal", "string", getPackageName())));
        }
    }

    private class Goto extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            finish();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("tv_ok", "id", getPackageName())) {
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }
}
