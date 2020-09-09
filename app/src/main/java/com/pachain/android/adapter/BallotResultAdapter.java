package com.pachain.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.pachain.android.common.ToolPackage;
import com.pachain.android.entity.CandidateEntity;
import java.util.ArrayList;

public class BallotResultAdapter extends BaseAdapter {
    private ArrayList<CandidateEntity> mData;
    private Context mContext;

    public BallotResultAdapter(Context context, ArrayList<CandidateEntity> mData) {
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
            view = View.inflate(mContext, mContext.getResources().getIdentifier("pachain_ui_ballotresult_listview", "layout", mContext.getPackageName()), null);
            holder = new Holder();
            holder.ll_seat = view.findViewById(mContext.getResources().getIdentifier("ll_seat", "id", mContext.getPackageName()));
            holder.tv_seatName = view.findViewById(mContext.getResources().getIdentifier("tv_seatName", "id", mContext.getPackageName()));
            holder.ll_candidate = view.findViewById(mContext.getResources().getIdentifier("ll_candidate", "id", mContext.getPackageName()));
            holder.iv_photo = view.findViewById(mContext.getResources().getIdentifier("iv_photo", "id", mContext.getPackageName()));
            holder.tv_party = view.findViewById(mContext.getResources().getIdentifier("tv_party", "id", mContext.getPackageName()));
            holder.tv_name = view.findViewById(mContext.getResources().getIdentifier("tv_name", "id", mContext.getPackageName()));
            holder.ll_voteResult = view.findViewById(mContext.getResources().getIdentifier("ll_voteResult", "id", mContext.getPackageName()));
            holder.ll_rate = view.findViewById(mContext.getResources().getIdentifier("ll_rate", "id", mContext.getPackageName()));
            holder.pb_rate = view.findViewById(mContext.getResources().getIdentifier("pb_rate", "id", mContext.getPackageName()));
            holder.tv_rate = view.findViewById(mContext.getResources().getIdentifier("tv_rate", "id", mContext.getPackageName()));
            holder.tv_voters = view.findViewById(mContext.getResources().getIdentifier("tv_voters", "id", mContext.getPackageName()));
            holder.tv_won = view.findViewById(mContext.getResources().getIdentifier("tv_won", "id", mContext.getPackageName()));
            view.setTag(holder);
        }
        final CandidateEntity model = mData.get(i);

        if (model.getID() < 1 && model.getSeatID() > 0) {
            holder.ll_seat.setVisibility(View.VISIBLE);
            holder.ll_candidate.setVisibility(View.GONE);
            holder.tv_seatName.setText(model.getSeatName());
        } else if (model.getID() > 0) {
            holder.ll_seat.setVisibility(View.GONE);
            holder.ll_candidate.setVisibility(View.VISIBLE);
            holder.tv_name.setText(model.getName());
            if (!TextUtils.isEmpty(model.getPartyCode())) {
                holder.tv_party.setVisibility(View.VISIBLE);
                holder.tv_party.setText(model.getPartyCode());
                if (model.getPartyCode().toUpperCase().equals("R") || model.getPartyCode().toUpperCase().equals("D") || model.getPartyCode().toUpperCase().equals("I")) {
                    holder.tv_party.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("white", "color", mContext.getPackageName())));
                } else {
                    holder.tv_party.setTextColor(mContext.getResources().getColor(mContext.getResources().getIdentifier("black", "color", mContext.getPackageName())));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (model.getPartyCode().toUpperCase().equals("R")) {
                        holder.tv_party.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_party_r", "drawable", mContext.getPackageName())));
                    } else if (model.getPartyCode().toUpperCase().equals("D")) {
                        holder.tv_party.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_party_d", "drawable", mContext.getPackageName())));
                    } else if (model.getPartyCode().toUpperCase().equals("I")) {
                        holder.tv_party.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_party_i", "drawable", mContext.getPackageName())));
                    } else {
                        holder.tv_party.setBackground(mContext.getResources().getDrawable(mContext.getResources().getIdentifier("pachain_shape_party_o", "drawable", mContext.getPackageName())));
                    }
                }
            } else {
                holder.tv_party.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(model.getPhoto())) {
                byte[] bitmapArray = Base64.decode(model.getPhoto().split(",")[1], Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                holder.iv_photo.setImageBitmap(bitmap);
            }
            if (model.getVoteRate() > -1) {
                holder.ll_rate.setVisibility(View.VISIBLE);
                holder.pb_rate.setProgress((int) model.getVoteRate());
                holder.tv_rate.setText(ToolPackage.doubleFormat(model.getVoteRate()) + "%" );
            } else {
                holder.ll_rate.setVisibility(View.GONE);
            }
            if (model.getVoteBallots() > -1) {
                holder.tv_voters.setText(mContext.getResources().getString(mContext.getResources().getIdentifier("ballotResults_votes", "string", mContext.getPackageName())) + " " + ToolPackage.decimalFormat(model.getVoteBallots()));
                holder.tv_voters.setVisibility(View.VISIBLE);
            } else {
                holder.tv_voters.setVisibility(View.GONE);
            }
        } else {
            holder.ll_seat.setVisibility(View.GONE);
            holder.ll_candidate.setVisibility(View.GONE);
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
        LinearLayout ll_seat;
        TextView tv_seatName;

        LinearLayout ll_candidate;
        ImageView iv_photo;
        TextView tv_party;
        TextView tv_name;
        LinearLayout ll_voteResult;
        LinearLayout ll_rate;
        ProgressBar pb_rate;
        TextView tv_rate;
        TextView tv_voters;
        TextView tv_won;
    }
}
