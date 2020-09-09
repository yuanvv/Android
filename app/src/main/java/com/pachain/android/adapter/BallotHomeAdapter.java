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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pachain.android.entity.CandidateEntity;
import java.util.ArrayList;

public class BallotHomeAdapter extends BaseAdapter {
    private ArrayList<CandidateEntity> mData;
    private Context mContext;

    public BallotHomeAdapter(Context context, ArrayList<CandidateEntity> mData) {
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
            view = View.inflate(mContext, mContext.getResources().getIdentifier("pachain_ui_ballothome_listview", "layout", mContext.getPackageName()), null);
            holder = new Holder();
            holder.ll_election = view.findViewById(mContext.getResources().getIdentifier("ll_election", "id", mContext.getPackageName()));
            holder.tv_viewProgress = view.findViewById(mContext.getResources().getIdentifier("tv_viewProgress", "id", mContext.getPackageName()));
            holder.tv_sample = view.findViewById(mContext.getResources().getIdentifier("tv_sample", "id", mContext.getPackageName()));
            holder.tv_electionName = view.findViewById(mContext.getResources().getIdentifier("tv_electionName", "id", mContext.getPackageName()));
            holder.tv_electionDay = view.findViewById(mContext.getResources().getIdentifier("tv_electionDay", "id", mContext.getPackageName()));
            holder.ll_toolVote = view.findViewById(mContext.getResources().getIdentifier("ll_toolVote", "id", mContext.getPackageName()));
            holder.tv_vote = view.findViewById(mContext.getResources().getIdentifier("tv_vote", "id", mContext.getPackageName()));
            holder.ll_voted = view.findViewById(mContext.getResources().getIdentifier("ll_voted", "id", mContext.getPackageName()));
            holder.ll_voting = view.findViewById(mContext.getResources().getIdentifier("ll_voting", "id", mContext.getPackageName()));
            holder.tv_done = view.findViewById(mContext.getResources().getIdentifier("tv_done", "id", mContext.getPackageName()));
            holder.tv_cancel = view.findViewById(mContext.getResources().getIdentifier("tv_cancel", "id", mContext.getPackageName()));
            holder.ll_verifyTip = view.findViewById(mContext.getResources().getIdentifier("ll_verifyTip", "id", mContext.getPackageName()));
            holder.tv_verifyTip = view.findViewById(mContext.getResources().getIdentifier("tv_verifyTip", "id", mContext.getPackageName()));
            holder.ll_seat = view.findViewById(mContext.getResources().getIdentifier("ll_seat", "id", mContext.getPackageName()));
            holder.tv_seatName = view.findViewById(mContext.getResources().getIdentifier("tv_seatName", "id", mContext.getPackageName()));
            holder.ll_candidate = view.findViewById(mContext.getResources().getIdentifier("ll_candidate", "id", mContext.getPackageName()));
            holder.ch_check = view.findViewById(mContext.getResources().getIdentifier("ch_check", "id", mContext.getPackageName()));
            holder.iv_photo = view.findViewById(mContext.getResources().getIdentifier("iv_photo", "id", mContext.getPackageName()));
            holder.tv_party = view.findViewById(mContext.getResources().getIdentifier("tv_party", "id", mContext.getPackageName()));
            holder.tv_name = view.findViewById(mContext.getResources().getIdentifier("tv_name", "id", mContext.getPackageName()));
            holder.iv_voted = view.findViewById(mContext.getResources().getIdentifier("iv_voted", "id", mContext.getPackageName()));
            view.setTag(holder);
        }
        final CandidateEntity model = mData.get(i);

        if (model.getID() < 1 && model.getSeatID() < 1 && model.getElectionID() > 0) {
            holder.ll_election.setVisibility(View.VISIBLE);
            holder.ll_seat.setVisibility(View.GONE);
            holder.ll_candidate.setVisibility(View.GONE);
            holder.tv_electionDay.setText(model.getElectionDate());
            holder.tv_electionName.setText(model.getElectionName());
            if (model.isVoting()) {
                holder.ll_toolVote.setVisibility(View.GONE);
            } else {
                if (model.isExceededVoting() && !model.isSampleBallot()) {
                    holder.ll_toolVote.setVisibility(View.VISIBLE);
                    if (model.isVoted()) {
                        holder.ll_voted.setVisibility(View.VISIBLE);
                        holder.tv_vote.setVisibility(View.GONE);
                    } else {
                        holder.ll_voted.setVisibility(View.GONE);
                        holder.tv_vote.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.ll_toolVote.setVisibility(View.GONE);
                }
            }
            holder.tv_viewProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onViewProgressClick(v, i);
                }
            });
            holder.tv_vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (CandidateEntity candidateEntity : mData) {
                        candidateEntity.setVoting(true);
                    }
                    notifyDataSetChanged();
                    onItemClickListener.onVoteClick(v, i);
                }
            });
            if (model.getParams() != null && model.getParams().containsKey("verify") && model.getParams().get("verify").equals("true")) {
                holder.tv_viewProgress.setVisibility(View.GONE);
                holder.tv_sample.setVisibility(View.GONE);
                holder.ll_verifyTip.setVisibility(View.VISIBLE);
                if (model.getParams().containsKey("verified") && model.getParams().get("verified").equals("true")) {
                    holder.tv_verifyTip.setText(mContext.getResources().getString(mContext.getResources().getIdentifier("verifyVote_confirmed", "string", mContext.getPackageName())));
                } else {
                    holder.tv_verifyTip.setText(String.format(mContext.getResources().getString(mContext.getResources().getIdentifier("verifyVote_tip", "string", mContext.getPackageName())),
                        (model.getParams().containsKey("votingDate") ? model.getParams().get("votingDate") : "")));
                }
            } else {
                holder.ll_verifyTip.setVisibility(View.GONE);
                if (!model.isSampleBallot()) {
                    holder.tv_viewProgress.setVisibility(View.VISIBLE);
                    holder.tv_sample.setVisibility(View.GONE);
                } else {
                    holder.tv_viewProgress.setVisibility(View.GONE);
                    holder.tv_sample.setVisibility(View.VISIBLE);
                }
            }
        } else if (model.getID() < 1 && model.getSeatID() > 0) {
            holder.ll_election.setVisibility(View.GONE);
            holder.ll_verifyTip.setVisibility(View.GONE);
            holder.ll_seat.setVisibility(View.VISIBLE);
            holder.ll_candidate.setVisibility(View.GONE);
            holder.tv_seatName.setText(model.getSeatName());
        } else if (model.getID() > 0) {
            holder.ll_election.setVisibility(View.GONE);
            holder.ll_verifyTip.setVisibility(View.GONE);
            holder.ll_seat.setVisibility(View.GONE);
            holder.ll_candidate.setVisibility(View.VISIBLE);
            if (model.isVoting()) {
                holder.ch_check.setVisibility(View.VISIBLE);
                holder.ch_check.setChecked(model.isVoted());
            } else {
                holder.ch_check.setVisibility(View.GONE);
            }
            if (!model.isVoting() && model.isVoted()) {
                holder.iv_voted.setVisibility(View.VISIBLE);
            } else {
                holder.iv_voted.setVisibility(View.GONE);
            }
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
            holder.ch_check.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (model.isVoting()) {
                       model.setVoted(!model.isVoted());
                   }
                   if (model.isVoting() && model.isVoted()) {
                       ArrayList<Integer> checks = new ArrayList<>();
                       int i = 0;
                       for (CandidateEntity candidateEntity : mData) {
                           if (candidateEntity.getID() > 0 && candidateEntity.getSeatID() == model.getSeatID() &&
                               candidateEntity.isVoted() && candidateEntity.getID() != model.getID()) {
                               checks.add(i);
                           }
                           i++;
                       }
                       while (checks.size() >= 1) {
                           if (model.isVoting()) {
                               mData.get(checks.get(0)).setVoted(false);
                           }
                           checks.remove(0);
                       }
                       notifyDataSetChanged();
                   }
               }
           });
        }
        return view;
    }

    public interface OnItemClickListener {
        void onVoteClick(View view, int i);
        void onViewProgressClick(View view, int i);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private static class Holder {
        LinearLayout ll_election;
        TextView tv_viewProgress;
        TextView tv_sample;
        TextView tv_electionName;
        TextView tv_electionDay;
        LinearLayout ll_toolVote;
        TextView tv_vote;
        LinearLayout ll_voted;
        LinearLayout ll_voting;
        TextView tv_done;
        TextView tv_cancel;

        LinearLayout ll_verifyTip;
        TextView tv_verifyTip;

        LinearLayout ll_seat;
        TextView tv_seatName;

        LinearLayout ll_candidate;
        CheckBox ch_check;
        ImageView iv_photo;
        TextView tv_party;
        TextView tv_name;
        ImageView iv_voted;
    }
}
