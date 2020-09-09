package com.pachain.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pachain.android.entity.VotedResultEntity;
import java.util.ArrayList;

public class VotedResultsAdapter extends BaseAdapter {
    private ArrayList<VotedResultEntity> mData;
    private Context mContext;

    public VotedResultsAdapter(Context context, ArrayList<VotedResultEntity> mData) {
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
            view = View.inflate(mContext, mContext.getResources().getIdentifier("pachain_ui_votedresult_listview", "layout", mContext.getPackageName()), null);
            holder = new Holder();
            holder.tv_date = view.findViewById(mContext.getResources().getIdentifier("tv_date", "id", mContext.getPackageName()));
            holder.tv_key = view.findViewById(mContext.getResources().getIdentifier("tv_key", "id", mContext.getPackageName()));
            holder.tv_result = view.findViewById(mContext.getResources().getIdentifier("tv_result", "id", mContext.getPackageName()));
            holder.tv_code = view.findViewById(mContext.getResources().getIdentifier("tv_code", "id", mContext.getPackageName()));
            view.setTag(holder);
        }
        final VotedResultEntity model = mData.get(i);
        holder.tv_date.setText(model.getVotingDate());
        holder.tv_key.setText(model.getKey());
        holder.tv_result.setText(model.getVotingResult());
        holder.tv_code.setText(model.getVerificationCode());
        if (model.getVotingDate().equals(mContext.getResources().getString(mContext.getResources().getIdentifier("viewVotingProgress_date", "string", mContext.getPackageName())))) {
            holder.tv_date.getPaint().setFakeBoldText(true);
            holder.tv_key.getPaint().setFakeBoldText(true);
            holder.tv_result.getPaint().setFakeBoldText(true);
            holder.tv_code.getPaint().setFakeBoldText(true);
            holder.tv_date.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_noright", "drawable", mContext.getPackageName())));
            holder.tv_key.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_noright", "drawable", mContext.getPackageName())));
            holder.tv_result.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_noright", "drawable", mContext.getPackageName())));
            holder.tv_code.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table", "drawable", mContext.getPackageName())));
        } else {
            holder.tv_date.getPaint().setFakeBoldText(false);
            holder.tv_key.getPaint().setFakeBoldText(false);
            holder.tv_result.getPaint().setFakeBoldText(false);
            holder.tv_code.getPaint().setFakeBoldText(false);
            holder.tv_date.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notopright", "drawable", mContext.getPackageName())));
            holder.tv_key.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notopright", "drawable", mContext.getPackageName())));
            holder.tv_result.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notopright", "drawable", mContext.getPackageName())));
            holder.tv_code.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_table_notop", "drawable", mContext.getPackageName())));
        }
        if (model.isSelected()) {
            holder.tv_date.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("red", "color", mContext.getPackageName())));
            holder.tv_key.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("red", "color", mContext.getPackageName())));
            holder.tv_result.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("red", "color", mContext.getPackageName())));
            holder.tv_code.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("red", "color", mContext.getPackageName())));
        } else {
            holder.tv_date.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("black", "color", mContext.getPackageName())));
            holder.tv_key.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("black", "color", mContext.getPackageName())));
            holder.tv_result.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("black", "color", mContext.getPackageName())));
            holder.tv_code.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("black", "color", mContext.getPackageName())));
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
        TextView tv_key;
        TextView tv_result;
        TextView tv_code;
    }
}
