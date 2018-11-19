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
import com.rock.qikso.model.Comment;

import java.util.List;

/**
 * Created by rocku27 on 10/8/16.
 */
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter .ViewHolder> {
    private Activity activity;
    private List<Comment> comments;

    public CommentListAdapter(List<Comment> items, Activity activity){
        this.comments=items;
        this.activity=activity;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_comments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userName.setText(comments.get(position).getUserName());
        holder.commentText.setText(comments.get(position).getComments());
        holder.commentDuration.setText(comments.get(position).getDateAdded());

        Glide.with(activity)
                .load(comments.get(position).getUserImage())
//                .transform(new CircleTransform(activity.getApplicationContext()))
                .override(200, 200)
                .centerCrop()
                .into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView userName;
        public final TextView commentText;
        public final TextView commentDuration;

        public final ImageView userImage;
        public final View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            userName = (TextView)itemView.findViewById(R.id.team_member_text);
            commentText = (TextView)itemView.findViewById(R.id.user_name_s);
            commentDuration = (TextView)itemView.findViewById(R.id.comment_time);
            userImage = (ImageView) itemView.findViewById(R.id.user_image);
        }
    }
}
