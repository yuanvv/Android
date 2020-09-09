package com.pachain.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pachain.android.entity.PrecinctEntity;
import java.util.ArrayList;

public class PrecinctAdapter extends BaseAdapter {
    private ArrayList<PrecinctEntity> mData;
    private Context mContext;

    public PrecinctAdapter(Context context, ArrayList<PrecinctEntity> mData) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        Holder holder = null;
        if (convertView != null) {
            view = convertView;
            holder = (Holder) view.getTag();
        }
        else {
            view = View.inflate(mContext, mContext.getResources().getIdentifier("pachain_ui_state_listview", "layout", mContext.getPackageName()), null);
            holder = new Holder();
            holder.tv_title = view.findViewById(mContext.getResources().getIdentifier("tv_title", "id", mContext.getPackageName()));
            view.setTag(holder);
        }
        PrecinctEntity model = mData.get(i);
        holder.tv_title.setText(model.getNumber());
        if (TextUtils.isEmpty(model.getState())) {
            holder.tv_title.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("gray9", "color", mContext.getPackageName())));
        } else {
            holder.tv_title.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("black", "color", mContext.getPackageName())));
        }
        return view;
    }

    private static class Holder {
        TextView tv_title;
    }
}
