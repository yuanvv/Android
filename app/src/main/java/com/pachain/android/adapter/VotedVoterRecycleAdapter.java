package com.pachain.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pachain.android.entity.VotedVoterEntity;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class VotedVoterRecycleAdapter extends RecyclerView.Adapter<VotedVoterRecycleAdapter.ViewHolder> {
    private ArrayList<VotedVoterEntity> mData;
    private Context mContext;

    public VotedVoterRecycleAdapter(Context context, ArrayList<VotedVoterEntity> mData) {
        this.mData = mData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mContext.getResources().getIdentifier("pachain_ui_votedvoter_listview", "layout", mContext.getPackageName()), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        VotedVoterEntity model = mData.get(position);
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
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_date;
        TextView tv_state;
        TextView tv_county;
        TextView tv_precinct;
        TextView tv_votedCount;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_date = view.findViewById(mContext.getResources().getIdentifier("tv_date", "id", mContext.getPackageName()));
            tv_state = view.findViewById(mContext.getResources().getIdentifier("tv_state", "id", mContext.getPackageName()));
            tv_county = view.findViewById(mContext.getResources().getIdentifier("tv_county", "id", mContext.getPackageName()));
            tv_precinct = view.findViewById(mContext.getResources().getIdentifier("tv_precinct", "id", mContext.getPackageName()));
            tv_votedCount = view.findViewById(mContext.getResources().getIdentifier("tv_votedCount", "id", mContext.getPackageName()));
        }
    }

}
