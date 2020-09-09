package com.pachain.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pachain.android.entity.VotedVoterEntity;
import java.util.ArrayList;

public class VotedVotersAdapter extends BaseAdapter {
    private ArrayList<VotedVoterEntity> mData;
    private Context mContext;

    public VotedVotersAdapter(Context context, ArrayList<VotedVoterEntity> mData) {
        this.mData = mData;
        this.mContext = context;
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        Holder holder = null;
        if (convertView != null) {
            view = convertView;
            holder = (Holder) view.getTag();
        }
        else {
            view = View.inflate(mContext, mContext.getResources().getIdentifier("pachain_ui_votedvoter_listview", "layout", mContext.getPackageName()), null);
            holder = new Holder();
            holder.tv_date = view.findViewById(mContext.getResources().getIdentifier("tv_date", "id", mContext.getPackageName()));
            holder.tv_state = view.findViewById(mContext.getResources().getIdentifier("tv_state", "id", mContext.getPackageName()));
            holder.tv_county = view.findViewById(mContext.getResources().getIdentifier("tv_county", "id", mContext.getPackageName()));
            holder.tv_precinct = view.findViewById(mContext.getResources().getIdentifier("tv_precinct", "id", mContext.getPackageName()));
            holder.tv_votedCount = view.findViewById(mContext.getResources().getIdentifier("tv_votedCount", "id", mContext.getPackageName()));
            view.setTag(holder);
        }
        final VotedVoterEntity model = mData.get(i);
        holder.tv_date.setText(model.getVotingDate());
        holder.tv_state.setText(model.getState());
        holder.tv_county.setText(model.getCounty());
        holder.tv_precinct.setText(model.getPrecinctNumber());
        holder.tv_votedCount.setText(model.getVotedCount());
        if (model.getState().equals(mContext.getResources().getString(mContext.getResources().getIdentifier("viewVotingProgress_state", "string", mContext.getPackageName())))) {
            holder.tv_date.getPaint().setFakeBoldText(true);
            holder.tv_state.getPaint().setFakeBoldText(true);
            holder.tv_county.getPaint().setFakeBoldText(true);
            holder.tv_precinct.getPaint().setFakeBoldText(true);
            holder.tv_votedCount.getPaint().setFakeBoldText(true);

            holder.tv_date.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_noright", "drawable", mContext.getPackageName())));
            holder.tv_state.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_noright", "drawable", mContext.getPackageName())));
            holder.tv_county.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_noright", "drawable", mContext.getPackageName())));
            holder.tv_precinct.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_noright", "drawable", mContext.getPackageName())));
            holder.tv_votedCount.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table", "drawable", mContext.getPackageName())));
        } else {
            holder.tv_date.getPaint().setFakeBoldText(false);
            holder.tv_state.getPaint().setFakeBoldText(false);
            holder.tv_county.getPaint().setFakeBoldText(false);
            holder.tv_precinct.getPaint().setFakeBoldText(false);
            holder.tv_votedCount.getPaint().setFakeBoldText(false);

            holder.tv_date.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notopright", "drawable", mContext.getPackageName())));
            holder.tv_state.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notopright", "drawable", mContext.getPackageName())));
            holder.tv_county.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notopright", "drawable", mContext.getPackageName())));
            holder.tv_precinct.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notopright", "drawable", mContext.getPackageName())));
            holder.tv_votedCount.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notop", "drawable", mContext.getPackageName())));
        }
        return view;
    }

    public interface OnItemClickListener {
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private static class Holder {
        TextView tv_date;
        TextView tv_state;
        TextView tv_county;
        TextView tv_precinct;
        TextView tv_votedCount;
    }
}
