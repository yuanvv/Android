package com.pachain.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pachain.android.entity.BallotEntity;
import java.util.ArrayList;

public class BallotAdapter extends BaseAdapter {
    private ArrayList<BallotEntity> mData;
    private Context mContext;
    private boolean verify;

    public BallotAdapter(Context context, ArrayList<BallotEntity> mData, boolean verify) {
        this.mData = mData;
        this.mContext = context;
        this.verify = verify;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        Holder holder = null;
        if (convertView != null) {
            view = convertView;
            holder = (Holder) view.getTag();
        }
        else {
            view = View.inflate(mContext, mContext.getResources().getIdentifier("pachain_ui_ballot_listview", "layout", mContext.getPackageName()), null);
            holder = new Holder();
            holder.tv_name = view.findViewById(mContext.getResources().getIdentifier("tv_name", "id", mContext.getPackageName()));
            holder.iv_verified = view.findViewById(mContext.getResources().getIdentifier("iv_verified", "id", mContext.getPackageName()));
            holder.tv_date = view.findViewById(mContext.getResources().getIdentifier("tv_date", "id", mContext.getPackageName()));
            holder.tv_votingDate = view.findViewById(mContext.getResources().getIdentifier("tv_votingDate", "id", mContext.getPackageName()));
            holder.tv_sample = view.findViewById(mContext.getResources().getIdentifier("tv_sample", "id", mContext.getPackageName()));
            view.setTag(holder);
        }
        BallotEntity model = mData.get(i);
        holder.tv_name.setText(model.getName());
        holder.tv_date.setText(model.getDate());
        if (verify) {
            holder.tv_votingDate.setVisibility(View.VISIBLE);
            holder.tv_votingDate.setText(mContext.getResources().getString(mContext.getResources().getIdentifier("ballot_votedOn", "string", mContext.getPackageName())) + " " + model.getVotingDate());
            if (model.isVerified()) {
                holder.iv_verified.setVisibility(View.VISIBLE);
            } else {
                holder.iv_verified.setVisibility(View.GONE);
            }
        } else {
            holder.iv_verified.setVisibility(View.GONE);
            holder.tv_votingDate.setVisibility(View.GONE);
        }
        if (model.isSample()) {
            holder.tv_sample.setVisibility(View.VISIBLE);
        } else {
            holder.tv_sample.setVisibility(View.GONE);
        }
        return view;
    }

    private static class Holder {
        TextView tv_name;
        ImageView iv_verified;
        TextView tv_date;
        TextView tv_votingDate;
        TextView tv_sample;
    }
}
