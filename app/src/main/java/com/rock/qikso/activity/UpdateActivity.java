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
import com.rock.qikso.adapter.UpdateListAdapter;
import com.rock.qikso.localStorage.PreferencesHelper;
import com.rock.qikso.model.Update;
import com.rock.qikso.model.UpdatesData;
//import com.rockers.qikso.volleyWebservice.Constants;

import com.rock.qikso.volleyWebservice.RequestParam;
import com.rock.qikso.volleyWebservice.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {

    private String projectId;
    private PreferencesHelper preferencesHelper;

    private RecyclerView recyclerView;

    private Bundle bundle;

    private UpdateListAdapter updateListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String token;

    private UpdatesData updatesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        projectId = intent.getStringExtra(Constants.PROJECT_ID);
        bundle = intent.getExtras();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        preferencesHelper = new PreferencesHelper(UpdateActivity.this);

        recyclerView = (RecyclerView)findViewById(R.id.list_updates);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_updates);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUpdates(projectId);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getUpdates();
    }

    private void loadUpdates(String ProjectId){
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
        rp.putParams(Constants.PROJECT_ID, ProjectId);

        objWebServiceHelper.apiParamsCall(Request.Method.POST, Constants.WebServiceUrls.PROJECT_UPDATES , rp, new WebServiceHelper.OnWebServiceListener() {

            @Override
            public void successResponse(JSONObject result) {
                try {
                    Gson gson = new Gson();
                    updatesData = gson.fromJson(result.getJSONObject(Constants.DATA).toString(), UpdatesData.class);

                    updateListAdapter.clear();

                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setNestedScrollingEnabled(false);

                    updateListAdapter = new UpdateListAdapter(updatesData.getUpdates(),UpdateActivity.this);
                    recyclerView.setAdapter(updateListAdapter);
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

    private void getUpdates(){
        if(bundle.getParcelableArrayList(Constants.UPDATES)!=null){

            ArrayList<Update> updateArrayList = bundle.getParcelableArrayList(Constants.UPDATES);

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            updateListAdapter = new UpdateListAdapter(updateArrayList, this);
            recyclerView.setAdapter(updateListAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
