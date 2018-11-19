package com.rock.qikso.adapter.user_dashboard;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rock.qikso.R;
import com.rock.qikso.fragment.user_dashboard.MyFundingFragment;
import com.rock.qikso.model.Donation;

import java.util.List;

/**
 * Created by rocku27 on 13/9/16.
 */
public class MyDonationListAdapter extends RecyclerView.Adapter<MyDonationListAdapter.ViewHolder> {

    private List<Donation> donation;
    private Activity activities;
    private MyFundingFragment fragment;
    private Context mContext;


    public MyDonationListAdapter(List<Donation> donations, FragmentActivity activity, MyFundingFragment myFundingFragment) {
        donation = donations;
        activities = activity;
        mContext = activity.getApplicationContext();
        fragment = myFundingFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_my_funding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyDonationListAdapter.ViewHolder holder, final int position) {
        holder.projectTitle.setText(donation.get(position).getProjectTitle());
        holder.amount.setText(donation.get(position).getAmount());
        holder.date.setText(donation.get(position).getTransactionDateTime());

        Log.e("project image",donation.get(position).getProjectImageUrl());

        if(donation.get(position).getProjectImageUrl()!=null && donation.get(position).getProjectImageUrl()!=""){
            Glide.with(activities)
                    .load(donation.get(position).getProjectImageUrl())
                    .crossFade()
                    .into(holder.projectImage);
        }
    }

    @Override
    public int getItemCount() {
        return donation.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView projectImage;
        public final TextView projectTitle;
        public final TextView amount;
        public final TextView date;
        public final View mView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            projectTitle = (TextView) mView.findViewById(R.id.tv);
            amount = (TextView) mView.findViewById(R.id.amount);
            date = (TextView) mView.findViewById(R.id.date);
            projectImage = (ImageView) mView.findViewById(R.id.project_image);
        }
    }
}
