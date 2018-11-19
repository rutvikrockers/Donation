package com.rock.qikso.adapter;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rock.qikso.R;
import com.rock.qikso.model.Follower;

import java.util.List;


/**
 * Created by rocku27 on 10/8/16.
 */
public class FollowerListAdapter extends RecyclerView.Adapter<FollowerListAdapter .ViewHolder> {

    private Activity activity;
    private List<Follower> followers;


    public FollowerListAdapter(List<Follower> items, Activity activity){
        this.activity = activity;
        this.followers = items;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_followers, parent, false);
        return new ViewHolder(view);
    }

    // Clean all elements of the recycler
    public void clear() {
        followers.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.followerName.setText(followers.get(position).getUserName());

        Glide.with(activity)
                .load(followers.get(position).getUserImage())
                .crossFade()
                .override(200, 200)
                .into(holder.followerImage);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        public final ImageView followerImage;
        public final TextView followerName;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            followerImage = (ImageView) itemView.findViewById(R.id.follower_image);
            followerName = (TextView)itemView.findViewById(R.id.follower_name);

        }
    }
}
