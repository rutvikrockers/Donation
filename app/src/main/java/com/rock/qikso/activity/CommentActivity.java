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

import com.google.gson.Gson;
import com.rock.donation.volleyWebservice.Constants;
import com.rock.qikso.Api.ApiClient;
import com.rock.qikso.Api.RewardRestInterface;
import com.rock.qikso.R;
import com.rock.qikso.adapter.CommentListAdapter;
import com.rock.qikso.extras.EndlessRecyclerViewScrollListener;
import com.rock.qikso.localStorage.PreferencesHelper;
import com.rock.qikso.model.Comment;
import com.rock.qikso.model.CommentsData;
//import com.rockers.qikso.volleyWebservice.Constants;

import com.rock.qikso.volleyWebservice.RequestParam;
import com.rock.qikso.volleyWebservice.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentList;

    private CommentListAdapter commentListAdapter;
    private RewardRestInterface restInterface;
    private String projectId;
    private PreferencesHelper preferencesHelper;

    private RecyclerView recyclerView;

    private Bundle bundle;

    private String token;

    private SwipeRefreshLayout swipeRefreshLayout;

    private CommentsData commentsData;
    private List<Comment> listComments;
    private int offset;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        projectId = intent.getStringExtra(Constants.PROJECT_ID);
        bundle = intent.getExtras();

        setContentView(R.layout.activity_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayoutManager = new LinearLayoutManager(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_comments);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadComments(projectId,0);
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

        preferencesHelper = new PreferencesHelper(CommentActivity.this);

        recyclerView = (RecyclerView)findViewById(R.id.listView);

        loadComments(projectId,0);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager, swipeRefreshLayout) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.e("totalItemsCount",totalItemsCount+"=="+commentsData.getTotalCommentsCount());
                if(totalItemsCount<commentsData.getTotalCommentsCount()){
                    loadComments(projectId,totalItemsCount);
                }
            }
        });
    }

    private void loadComments(String ProjectId, final int offset){
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
        rp.putParams(Constants.OFFSET,String.valueOf(offset));
        restInterface = ApiClient.getClient().create(RewardRestInterface.class);
        Call<CommentsData> call = restInterface.ProjectCommentsDashboard(token,ProjectId,String.valueOf(offset),"dashboard");
        call.enqueue(new Callback<CommentsData>() {
            @Override
            public void onResponse(Call<CommentsData> call, Response<CommentsData> response) {
                try {
                    Gson gson = new Gson();
                    // commentsData = gson.fromJson(result.getJSONObject(Constants.DATA).toString(), CommentsData.class);

                    if(offset==0){
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setNestedScrollingEnabled(false);
                        listComments = commentsData.getComments();
                        commentListAdapter = new CommentListAdapter(listComments,CommentActivity.this);
                        recyclerView.setAdapter(commentListAdapter);
                    }else{
                        int curSize = commentListAdapter.getItemCount();
                        listComments.addAll(commentsData.getComments());
                        commentListAdapter.notifyItemRangeInserted(curSize, listComments.size() - 1);
                    }
                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CommentsData> call, Throwable t) {
                t.printStackTrace();
            }
        });
/*        objWebServiceHelper.apiParamsCall(Request.Method.POST, Constants.WebServiceUrls.PROJECT_COMMENTS , rp, new WebServiceHelper.OnWebServiceListener() {

            @Override
            public void successResponse(JSONObject result) {
                try {
                    Gson gson = new Gson();
                    commentsData = gson.fromJson(result.getJSONObject(Constants.DATA).toString(), CommentsData.class);

                    if(offset==0){
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setNestedScrollingEnabled(false);
                        listComments = commentsData.getComments();
                        commentListAdapter = new CommentListAdapter(listComments,CommentActivity.this);
                        recyclerView.setAdapter(commentListAdapter);
                    }else{
                        int curSize = commentListAdapter.getItemCount();
                        listComments.addAll(commentsData.getComments());
                        commentListAdapter.notifyItemRangeInserted(curSize, listComments.size() - 1);
                    }
                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failureResponse(String errorMessage, int errorCode) {
                Log.e("success", errorMessage.toString());
            }
        });*/
    }

    private void getComments(){
        if(bundle.getParcelableArrayList(Constants.COMMENTS)!=null){
            ArrayList<Comment> commentArrayList = bundle.getParcelableArrayList(Constants.COMMENTS);

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            commentListAdapter = new CommentListAdapter(commentArrayList, this);
            recyclerView.setAdapter(commentListAdapter);
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
