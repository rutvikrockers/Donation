package com.rock.qikso.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rock.donation.volleyWebservice.Constants;
import com.rock.qikso.R;
import com.rock.qikso.activity.DonationDetailActivity;
import com.rock.qikso.model.Perk;

//import com.rockers.qikso.volleyWebservice.Constants;

import java.util.List;


/**
 * Created by rocku27 on 10/8/16.
 */
public class PerkListAdapter extends RecyclerView.Adapter<PerkListAdapter .ViewHolder> {

    private Activity ativity;
    private List<Perk> perks;
    private Context mContext;



    public PerkListAdapter(List<Perk> items, Activity activity){
        this.ativity = activity;
        this.mContext = activity.getApplicationContext();
        this.perks = items;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_perks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(perks.get(position).getPerkGet()!=null && !perks.get(position).getPerkGet().isEmpty()){
            holder.claimed.setText(perks.get(position).getPerkGet()+" Claimed");
        }else{
            holder.claimed.setVisibility(View.GONE);
        }
        holder.perkTitle.setText(perks.get(position).getPerkTitle());
        holder.perkDescription.setText(perks.get(position).getPerkDescription());
        holder.perkDelivery.setText(perks.get(position).getEstdate());
        holder.perkPrice.setText(perks.get(position).getPerkAmount());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, DonationDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.PROJECT_ID,perks.get(position).getProjectId());
                intent.putExtra(Constants.PERK_ID,perks.get(position).getPerkId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return perks.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView perkTitle;
        public final TextView perkDescription;
        public final TextView perkPrice;
        public final TextView perkDelivery;
        public final TextView claimed;
        public final TextView getPerk;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            perkTitle = (TextView)itemView.findViewById(R.id.perk_title);
            perkDescription = (TextView)itemView.findViewById(R.id.perk_description);
            perkPrice = (TextView)itemView.findViewById(R.id.perk_price);
            perkDelivery = (TextView)itemView.findViewById(R.id.estimate_delivery);
            claimed = (TextView)itemView.findViewById(R.id.claimed);
            getPerk = (TextView)itemView.findViewById(R.id.get_perk);
        }
    }
}
