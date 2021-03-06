package com.rock.qikso.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.rock.donation.volleyWebservice.Constants;
import com.rock.qikso.R;
import com.rock.qikso.adapter.FollowerListAdapterMain;
import com.rock.qikso.localStorage.PreferencesHelper;
import com.rock.qikso.model.Follower;
import com.rock.qikso.model.FollowersData;
//import com.rockers.qikso.volleyWebservice.Constants;

import com.rock.qikso.volleyWebservice.RequestParam;
import com.rock.qikso.volleyWebservice.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowerActivity extends AppCompatActivity {

    private String projectId;
    private Bundle bundle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PreferencesHelper preferencesHelper;
    private RecyclerView recyclerView;
    private FollowerListAdapterMain followerListAdapter;
    private String token;
    private FollowersData followersData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        projectId = intent.getStringExtra(Constants.PROJECT_ID);
        bundle = intent.getExtras();

        setContentView(R.layout.activity_follower);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_comments);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFollowers(projectId);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        preferencesHelper = new PreferencesHelper(FollowerActivity.this);

        recyclerView = (RecyclerView)findViewById(R.id.listView);

        recyclerView.setAdapter(followerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        loadFollowers(projectId);

    }

    private void getFollowers(){
        if(bundle.getParcelableArrayList(Constants.FUNDERS)!=null){
            ArrayList<Follower> followerArrayList = bundle.getParcelableArrayList(Constants.FOLLOWERS);

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            followerListAdapter = new FollowerListAdapterMain(followerArrayList, this);
            recyclerView.setAdapter(followerListAdapter);

        }
    }

    private void loadFollowers(String projectId){
        WebServiceHelper objWebServiceHelper = new WebServiceHelper(this);
        RequestParam rp = new RequestParam();
        String guestUser = (this.preferencesHelper.getPrefString(PreferencesHelper.GUEST_USER));
        try {
            JSONObject jsonObject = new JSONObject(guestUser);
            token = jsonObject.getString(Constants.TOKEN_STR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rp.putHeader(Constants.TOKEN, token);
        rp.putHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE);
        rp.putParams(Constants.PROJECT_ID, projectId);

        objWebServiceHelper.apiParamsCall(Request.Method.POST, Constants.WebServiceUrls.PROJECT_FOLLOWERS , rp, new WebServiceHelper.OnWebServiceListener() {

            @Override
            public void successResponse(JSONObject result) {
                try {
                    Gson gson = new Gson();
                    followersData = gson.fromJson(result.getJSONObject(Constants.DATA).toString(), FollowersData.class);

//                    followerListAdapter.clear();

                    recyclerView.setNestedScrollingEnabled(false);

                    followerListAdapter = new FollowerListAdapterMain(followersData.getFollowers(),FollowerActivity.this);
                    recyclerView.setAdapter(followerListAdapter);
                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failureResponse(String errorMessage, int errorCode) {
                Log.e("success", errorMessage.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
