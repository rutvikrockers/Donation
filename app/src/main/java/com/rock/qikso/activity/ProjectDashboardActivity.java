package com.rock.qikso.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.rock.donation.volleyWebservice.Constants;
import com.rock.qikso.Api.ApiClient;
import com.rock.qikso.Api.RewardRestInterface;
import com.rock.qikso.R;
import com.rock.qikso.adapter.CommentListAdapter;
import com.rock.qikso.adapter.FollowerListAdapter;
import com.rock.qikso.adapter.FunderListAdapter;
import com.rock.qikso.adapter.PerkListAdapter;
import com.rock.qikso.adapter.TeamMemberListAdapter;
import com.rock.qikso.adapter.UpdateListAdapter;
import com.rock.qikso.extras.HtmlParser;
import com.rock.qikso.localStorage.PreferencesHelper;
import com.rock.qikso.model.Comment;
import com.rock.qikso.model.Follower;
import com.rock.qikso.model.Funder;
import com.rock.qikso.model.ProjectDetailData;
import com.rock.qikso.model.TeamMember;
import com.rock.qikso.model.Update;
import com.rock.qikso.pojo.ResponseProjectDetailData;
import com.rock.qikso.utils.Utils;
///import com.rockers.qikso.volleyWebservice.Constants;

import com.rock.qikso.volleyWebservice.RequestParam;
import com.rock.qikso.volleyWebservice.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDashboardActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private Toolbar toolbar;
    private String projectId;
    private String token;
    private PreferencesHelper preferencesHelper;
    private ProjectDetailData projectDetailData;
    private TextView projectTitle;
    private TextView projectAmount;
    private TextView projectAmountRaised;
    private TextView projectCategory;
    private TextView projectPercentageFunding;
    private TextView projectDaysLeft;
    private TextView projectTotalComments;
    private TextView projectTotalFunders;
    private TextView projectTotalFollowers;
    private TextView projectTotalUpdates;
    private TextView projectTotalTeamMembers;
    private TextView projectCommentsViewAll;
    private TextView projectUpdatesViewAll;
    private TextView projectFundersViewAll;
    private TextView projectFollowersViewAll;
    private TextView projectTeamMembersViewAll;
    private TextView projectStatus;

    private RewardRestInterface restInterface;



    private ImageView projectThumbnail;

    private RecyclerView perkList;
    private RecyclerView funderList;
    private RecyclerView followerList;
    private RecyclerView commentList;
    private RecyclerView teamMemberList;
    private RecyclerView updateList;


    private ProgressBar amountProgress;

    private PerkListAdapter adapter;
    private FunderListAdapter funderListAdapter;
    private FollowerListAdapter followerListAdapter;
    private CommentListAdapter commentListAdapter;
    private TeamMemberListAdapter teamMemberListAdapter;
    private UpdateListAdapter updateListAdapter;

    private Bundle bundle;

    private Button contribute;

    private RelativeLayout rlFunderList;
    private RelativeLayout rlCommentList;
    private RelativeLayout rlTeamMemberList;
    private RelativeLayout rlUpdateList;
    private RelativeLayout rlfollowerList;
    private RelativeLayout rlPerkList;

    private RelativeLayout rlFunderListTop;
    private RelativeLayout rlCommentListTop;
    private RelativeLayout rlTeamMemberListTop;
    private RelativeLayout rlUpdateListTop;
    private RelativeLayout rlfollowerListTop;
    private RelativeLayout rlPerkListTop;

    VideoView vidView;
    private YouTubePlayerSupportFragment frag;
    private String youTubeId;

    private String readStoryValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final Intent intent = getIntent();
        projectId = intent.getStringExtra(Constants.PROJECT_ID);

        preferencesHelper = new PreferencesHelper(ProjectDashboardActivity.this);

        String guestUser = (this.preferencesHelper.getPrefString(PreferencesHelper.GUEST_USER));
        try {
            JSONObject jsonObject = new JSONObject(guestUser);
            token = jsonObject.getString(Constants.TOKEN_STR);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        frag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);

        projectTitle = (TextView) findViewById(R.id.tv_project_title);
        projectAmount = (TextView) findViewById(R.id.amount);
        projectAmountRaised = (TextView) findViewById(R.id.amount_raised);
        projectPercentageFunding = (TextView) findViewById(R.id.percentage_funded);
        projectDaysLeft = (TextView) findViewById(R.id.days_left);
        projectTotalComments = (TextView) findViewById(R.id.comments_count);
        projectTotalFollowers = (TextView) findViewById(R.id.user_name_s);
        projectTotalFunders = (TextView) findViewById(R.id.funders_count);
        projectTotalUpdates = (TextView) findViewById(R.id.updates_count);
        projectTotalTeamMembers = (TextView) findViewById(R.id.team_member_count);
        projectCommentsViewAll = (TextView) findViewById(R.id.view_all_comments);
        projectUpdatesViewAll = (TextView) findViewById(R.id.view_all_updates);
        projectFundersViewAll = (TextView) findViewById(R.id.view_all_funders);
        projectFollowersViewAll = (TextView) findViewById(R.id.view_all_followers);
        projectTeamMembersViewAll = (TextView) findViewById(R.id.team_member_view_all);
        projectStatus = (TextView) findViewById(R.id.project_status);
        projectCategory = (TextView)findViewById(R.id.category);

        projectThumbnail = (ImageView) findViewById(R.id.project_thumbnail);

        amountProgress = (ProgressBar) findViewById(R.id.progress_amount_raised);

        perkList = (RecyclerView) findViewById(R.id.perk_list);
        funderList = (RecyclerView) findViewById(R.id.funder_list);
        followerList = (RecyclerView) findViewById(R.id.follower_list);
        commentList = (RecyclerView) findViewById(R.id.comment_list);
        teamMemberList = (RecyclerView) findViewById(R.id.team_member_list);
        updateList = (RecyclerView) findViewById(R.id.update_list);

        rlFunderList = (RelativeLayout)findViewById(R.id.rl_funder_list);
        rlCommentList = (RelativeLayout)findViewById(R.id.rl_comment_list);
        rlUpdateList = (RelativeLayout)findViewById(R.id.rl_update_list);
        rlfollowerList = (RelativeLayout)findViewById(R.id.rl_follower_list);
        rlPerkList = (RelativeLayout)findViewById(R.id.rl_perk_list);
        rlTeamMemberList = (RelativeLayout)findViewById(R.id.rl_team_member_list);

        rlFunderListTop = (RelativeLayout)findViewById(R.id.rl_top_contributions);
        rlCommentListTop = (RelativeLayout)findViewById(R.id.rl_top_comments);
        rlUpdateListTop = (RelativeLayout)findViewById(R.id.rl_top_updates);
        rlfollowerListTop = (RelativeLayout)findViewById(R.id.rl_top_followers);
        rlPerkListTop = (RelativeLayout)findViewById(R.id.rl_top_perks);
        rlTeamMemberListTop = (RelativeLayout)findViewById(R.id.rl_top_team_members);
        rlPerkListTop.setVisibility(View.GONE);


        projectCommentsViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtras(bundle);
                intent.putExtra(Constants.PROJECT_ID,projectId);
                startActivity(intent);
            }
        });

        projectUpdatesViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UpdateActivity.class);
                intent.putExtras(bundle);
                intent.putExtra(Constants.PROJECT_ID,projectId);
                startActivity(intent);
            }
        });

        projectFundersViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FunderActivity.class);
                intent.putExtras(bundle);
                intent.putExtra(Constants.PROJECT_ID,projectId);
                startActivity(intent);
            }
        });

        projectFollowersViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FollowerActivity.class);
                intent.putExtras(bundle);
                intent.putExtra(Constants.PROJECT_ID,projectId);
                startActivity(intent);
            }
        });

        projectTeamMembersViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TeamMemberActivity.class);
                intent.putExtras(bundle);
                intent.putExtra(Constants.PROJECT_ID,projectId);
                startActivity(intent);
            }
        });

//        setupVideoView();

        getProjectDetail();
    }

    private void setProjectDetail() {

        getSupportActionBar().setTitle(projectDetailData.getProjectDetail().getProjectTitle());


        projectTitle.setText(projectDetailData.getProjectDetail().getProjectTitle());
        projectCategory.setText(projectDetailData.getProjectDetail().getProjectCategoryName());

        projectAmount.setText(projectDetailData.getProjectDetail().getAmountWithCurrency()+" USD");
        projectAmountRaised.setText(projectDetailData.getProjectDetail().getAmountGetWithCurrency()+" USD raised of");

        projectStatus.setText(projectDetailData.getProjectDetail().getProjectStatus().toUpperCase());


        projectStatus.setBackgroundResource(R.drawable.tags_rounded_corners);
        GradientDrawable drawable = (GradientDrawable) projectStatus.getBackground();
        drawable.setColor(Color.parseColor("#"+projectDetailData.getProjectDetail().getStatusColor()));

        String fundingTypeText = "<strong>"+projectDetailData.getProjectDetail().getFundingType()+"</strong>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            fundingTypeText = String.valueOf(Html.fromHtml(fundingTypeText,Html.FROM_HTML_MODE_LEGACY));
        } else {
            fundingTypeText = String.valueOf(Html.fromHtml(fundingTypeText));
        }


        if (projectDetailData.getProjectDetail().getPercentage()!=null){
            projectPercentageFunding.setText(projectDetailData.getProjectDetail().getPercentage().toString()+"% funded");
        }
        else{

        }

        if (projectDetailData.getTotalCommentsCount()!=null && projectDetailData.getTotalCommentsCount()>0){
            projectTotalComments.setText(projectDetailData.getTotalCommentsCount().toString());
        }else{
            rlCommentList.setVisibility(View.GONE);
            rlCommentListTop.setVisibility(View.GONE);
        }

        if (projectDetailData.getTotalFollowersCount()!=null && projectDetailData.getTotalFollowersCount()>0){
            projectTotalFollowers.setText(projectDetailData.getTotalFollowersCount().toString());
        }else {
            rlfollowerList.setVisibility(View.GONE);
            rlfollowerListTop.setVisibility(View.GONE);
        }
        if (projectDetailData.getTotalFundersCount()!=null && projectDetailData.getTotalFundersCount()>0){
            projectTotalFunders.setText(projectDetailData.getTotalFundersCount().toString());

        }else {
            rlFunderList.setVisibility(View.GONE);
            rlFunderListTop.setVisibility(View.GONE);
        }
        if (projectDetailData.getTotalUpdatesCount()!=null && projectDetailData.getTotalUpdatesCount()>0){
            projectTotalUpdates.setText(projectDetailData.getTotalUpdatesCount().toString());

        }else{
            rlUpdateList.setVisibility(View.GONE);
            rlUpdateListTop.setVisibility(View.GONE);
        }
        if (projectDetailData.getTeamMembersCount()!=null && projectDetailData.getTeamMembersCount()>0){
            projectTotalTeamMembers.setText(projectDetailData.getTeamMembersCount().toString());

        }else{
            rlTeamMemberList.setVisibility(View.GONE);
            rlTeamMemberListTop.setVisibility(View.GONE);
        }


        projectDaysLeft.setText(HtmlParser.stripHtml(projectDetailData.getProjectDetail().getEndDate()));

        amountProgress.setProgress(Integer.valueOf(projectDetailData.getProjectDetail().getPercentage().intValue()));

        if(projectDetailData.getProjectDetail().getPitchMedia().equals("video")){

            Log.e("is sssvideo",projectDetailData.getProjectDetail().getPitchMedia());

            projectThumbnail.setVisibility(View.GONE);

            String expandedUrl = null;
            try {
                expandedUrl = new getExpandUrl().execute(projectDetailData.getProjectDetail().getVideo()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(expandedUrl); //url is youtube url for which you want to extract the id.
            if (matcher.find()) {
                youTubeId = matcher.group();
            }

            frag.initialize(Constants.DEVELOPER_KEY, this);

        }else{
            Glide.with(this)
                    .load(projectDetailData.getProjectDetail().getPitchImageUrl())
                    .placeholder(R.drawable.default_project_image)
                    .crossFade()
                    .into(projectThumbnail);

            View fragm = findViewById(R.id.youtube_fragment);
            fragm.setVisibility(View.GONE);
        }


        if(projectDetailData.getPerks()!=null && !projectDetailData.getPerks().isEmpty()){
            /*Perks Adapter*/
            rlPerkListTop.setVisibility(View.VISIBLE);
            rlPerkList.setVisibility(View.VISIBLE);
            perkList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter = new PerkListAdapter(projectDetailData.getPerks(), this);
            perkList.setAdapter(adapter);
        }else{
            rlPerkListTop.setVisibility(View.GONE);
            rlPerkList.setVisibility(View.GONE);
        }

        if(projectDetailData.getFunders()!=null) {
            /*Funders Adapter*/
            funderList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
            funderListAdapter = new FunderListAdapter(projectDetailData.getFunders(), this);
            funderList.setAdapter(funderListAdapter);
        }

        if(projectDetailData.getFollowers()!=null) {
            /*Funders Adapter*/
            followerList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
            followerListAdapter = new FollowerListAdapter(projectDetailData.getFollowers(), this);
            followerList.setAdapter(followerListAdapter);
        }

        if(projectDetailData.getComments()!=null) {
            /*Comment Adapter*/
            commentList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            commentListAdapter = new CommentListAdapter(projectDetailData.getComments(), this);
            commentList.setAdapter(commentListAdapter);
        }

        if(projectDetailData.getTeamMembers()!=null) {
            /*Team Member Adapter*/
            teamMemberList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            teamMemberListAdapter = new TeamMemberListAdapter(projectDetailData.getTeamMembers(), this);
            teamMemberList.setAdapter(teamMemberListAdapter);
        }

        if(projectDetailData.getUpdates()!=null) {
            /*Updates Adapter*/
            updateList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            updateListAdapter = new UpdateListAdapter(projectDetailData.getUpdates(), this);
            updateList.setAdapter(updateListAdapter);
        }

    }
    public class getExpandUrl extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String[] str) {
            URL url;
            Log.e("url[0]",str[0]);
            String expandedURL = "";
            try {
                url = new URL(str[0]);
                // open connection
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
                // stop following browser redirect
                httpURLConnection.setInstanceFollowRedirects(false);
                // extract location header containing the actual destination URL
                expandedURL = httpURLConnection.getHeaderField("Location");
                httpURLConnection.disconnect();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return expandedURL;
        }
    }

    private void getProjectDetail() {

        Utils.showLoader(ProjectDashboardActivity.this);

        WebServiceHelper objWebServiceHelper = new WebServiceHelper(this);
        RequestParam rp = new RequestParam();

        if(this.preferencesHelper.getPrefBoolean(PreferencesHelper.USER_LOGGED_IN)){
            String loginUser = (this.preferencesHelper.getPrefString(PreferencesHelper.USER_LOGIN));
            try {
                JSONObject jsonObject = new JSONObject(loginUser);
                token = jsonObject.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            String guestUser = (this.preferencesHelper.getPrefString(PreferencesHelper.GUEST_USER));
            try {
                JSONObject jsonObject = new JSONObject(guestUser);
                token = jsonObject.getString(Constants.TOKEN_STR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        rp.putHeader(Constants.TOKEN, token);
        rp.putHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE);
        rp.putParams(Constants.PROJECT_ID, projectId);
        restInterface = ApiClient.getClient().create(RewardRestInterface.class);
        Call<ResponseProjectDetailData> call = restInterface.ProjectDetail(token,projectId);
        call.enqueue(new Callback<ResponseProjectDetailData>() {
            @Override
            public void onResponse(Call<ResponseProjectDetailData> call, Response<ResponseProjectDetailData> response) {
                Gson gson = new Gson();
                try {

                    ArrayList<ProjectDetailData> homedate=new ArrayList<>();
                    ArrayList<String> projectDetailDatas = new ArrayList<>();
                    for (int i=0; i<response.body().getData().getComments().size(); i++) {
                        projectDetailDatas.add(response.body().getData().getProject_detail().toString());
                    }
                    if (projectDetailDatas != null) {
                        bundle = new Bundle();
                        if(response.body().getData().getComments().size()>0){
                            ArrayList<Comment> commentArrayList = new ArrayList<>();
                            for (int i=0; i<response.body().getData().getComments().size(); i++) {
                                commentArrayList.add(response.body().getData().getComments().get(i));
                            }

                            bundle.putParcelableArrayList(com.rock.reward.volleyWebservice.Constants.COMMENTS,commentArrayList);
                        }

                        if(response.body().getData().getUpdates().size()>0){
                            ArrayList<Update> updateArrayList = new ArrayList<>();
                            for (int i=0; i<response.body().getData().getUpdates().size(); i++) {
                                updateArrayList.add(response.body().getData().getUpdates().get(i));
                            }
                            bundle.putParcelableArrayList(com.rock.reward.volleyWebservice.Constants.UPDATES,updateArrayList);
                        }

                        if(response.body().getData().getFunders().size()>0){
                            ArrayList<Funder> funderArrayList = new ArrayList<>();
                            for (int i=0; i<response.body().getData().getFunders().size(); i++) {
                                funderArrayList.add(response.body().getData().getFunders().get(i));
                            }
                            bundle.putParcelableArrayList(com.rock.reward.volleyWebservice.Constants.FUNDERS,funderArrayList);
                        }
                        if (response.body().getData().getFollowers() != null) {
                            if (response.body().getData().getFollowers().size() > 0) {
                                ArrayList<Follower> followerArrayList = new ArrayList<>();
                                for (int i = 0; i < response.body().getData().getFollowers().size(); i++) {
                                    followerArrayList.add(response.body().getData().getFollowers().get(i));
                                }
                                bundle.putParcelableArrayList(com.rock.reward.volleyWebservice.Constants.FOLLOWERS, followerArrayList);
                            }
                        }
                        if (response.body().getData().getTeam_members() != null) {
                            if (response.body().getData().getTeam_members().size() > 0) {
                                ArrayList<TeamMember> teamMemberArrayList = new ArrayList<>();
                                for (int i = 0; i < response.body().getData().getFollowers().size(); i++) {
                                    teamMemberArrayList.add(response.body().getData().getTeam_members().get(i));
                                }
                                bundle.putParcelableArrayList(com.rock.reward.volleyWebservice.Constants.TEAM_MEMBERS, teamMemberArrayList);
                            }
                        }
                        getSupportActionBar().setTitle(response.body().getData().getProject_detail().getProjectTitle());


                        projectTitle.setText(response.body().getData().getProject_detail().getProjectTitle());
                        projectCategory.setText(response.body().getData().getProject_detail().getProjectCategoryName());

                        projectAmount.setText(response.body().getData().getProject_detail().getAmountWithCurrency()+"");
                        projectAmountRaised.setText(response.body().getData().getProject_detail().getAmountGetWithCurrency()+" raised of");

                        projectStatus.setText(response.body().getData().getProject_detail().getProjectStatus().toUpperCase());


                        projectStatus.setBackgroundResource(R.drawable.tags_rounded_corners);
                        GradientDrawable drawable = (GradientDrawable) projectStatus.getBackground();
                        drawable.setColor(Color.parseColor("#"+response.body().getData().getProject_detail().getStatusColor()));

                        String fundingTypeText = "<strong>"+response.body().getData().getProject_detail().getFundingType()+"</strong>";
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            fundingTypeText = String.valueOf(Html.fromHtml(fundingTypeText,Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            fundingTypeText = String.valueOf(Html.fromHtml(fundingTypeText));
                        }


                        if (response.body().getData().getProject_detail().getPercentage()!=null){
                            projectPercentageFunding.setText(response.body().getData().getProject_detail().getPercentage().toString()+"% funded");
                        }
                        else{

                        }



                        projectDaysLeft.setText(HtmlParser.stripHtml(response.body().getData().getProject_detail().getEndDate()));

                        amountProgress.setProgress(Integer.valueOf(response.body().getData().getProject_detail().getPercentage().intValue()));

                        if(response.body().getData().getProject_detail().getPitchMedia().equals("video")){

                            Log.e("is sssvideo",response.body().getData().getProject_detail().getPitchMedia());

                            projectThumbnail.setVisibility(View.GONE);

                            String expandedUrl = null;
                            try {
                                expandedUrl = new getExpandUrl().execute(response.body().getData().getProject_detail().getVideo()).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                            Pattern compiledPattern = Pattern.compile(pattern);
                            Matcher matcher = compiledPattern.matcher(expandedUrl); //url is youtube url for which you want to extract the id.
                            if (matcher.find()) {
                                youTubeId = matcher.group();
                            }

                            frag.initialize(com.rock.reward.volleyWebservice.Constants.DEVELOPER_KEY, ProjectDashboardActivity.this);

                        }else{
                            Glide.with(ProjectDashboardActivity.this)
                                    .load(response.body().getData().getProject_detail().getPitchImageUrl())
                                    .placeholder(R.drawable.default_project_image)
                                    .crossFade()
                                    .into(projectThumbnail);

                            View fragm = findViewById(R.id.youtube_fragment);
                            fragm.setVisibility(View.GONE);
                        }


                        Utils.hideLoader(ProjectDashboardActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseProjectDetailData> call, Throwable t) {
                t.printStackTrace();
            }
        });
//        objWebServiceHelper.apiParamsCall(Request.Method.POST, Constants.WebServiceUrls.PROJECT_DETAIL , rp, new WebServiceHelper.OnWebServiceListener() {
//
//            @Override
//            public void successResponse(JSONObject result) {
//                try {
//                    Gson gson = new Gson();
//                    projectDetailData = gson.fromJson(result.getJSONObject(Constants.DATA).toString(), ProjectDetailData.class);
//                    if (projectDetailData.getProjectDetail() != null) {
//                        bundle = new Bundle();
//                        if(projectDetailData.getComments().size()>0){
//                            ArrayList<Comment> commentArrayList = new ArrayList<>();
//                            for (int i=0; i<projectDetailData.getComments().size(); i++) {
//                                commentArrayList.add(projectDetailData.getComments().get(i));
//                            }
//
//                            bundle.putParcelableArrayList(Constants.COMMENTS,commentArrayList);
//                        }
//
//                        if(projectDetailData.getUpdates().size()>0){
//                            ArrayList<Update> updateArrayList = new ArrayList<>();
//                            for (int i=0; i<projectDetailData.getUpdates().size(); i++) {
//                                updateArrayList.add(projectDetailData.getUpdates().get(i));
//                            }
//                            bundle.putParcelableArrayList(Constants.UPDATES,updateArrayList);
//                        }
//
//                        if(projectDetailData.getFunders().size()>0){
//                            ArrayList<Funder> funderArrayList = new ArrayList<>();
//                            for (int i=0; i<projectDetailData.getFunders().size(); i++) {
//                                funderArrayList.add(projectDetailData.getFunders().get(i));
//                            }
//                            bundle.putParcelableArrayList(Constants.FUNDERS,funderArrayList);
//                        }
//
//                        if(projectDetailData.getFollowers().size()>0){
//                            ArrayList<Follower> followerArrayList = new ArrayList<>();
//                            for (int i=0; i<projectDetailData.getFollowers().size(); i++) {
//                                followerArrayList.add(projectDetailData.getFollowers().get(i));
//                            }
//                            bundle.putParcelableArrayList(Constants.FOLLOWERS,followerArrayList);
//                        }
//                        if(projectDetailData.getTeamMembers().size()>0){
//                            ArrayList<TeamMember> teamMemberArrayList = new ArrayList<>();
//                            for (int i=0; i<projectDetailData.getFollowers().size(); i++) {
//                                teamMemberArrayList.add(projectDetailData.getTeamMembers().get(i));
//                            }
//                            bundle.putParcelableArrayList(Constants.TEAM_MEMBERS,teamMemberArrayList);
//                        }
//
//                        setProjectDetail();
//                        Utils.hideLoader(ProjectDashboardActivity.this);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void failureResponse(String errorMessage, int errorCode) {
//                Log.e("success", errorMessage.toString());
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            //I assume the below String value is your video id
            Log.e("youTubeId",youTubeId);
            player.cueVideo(youTubeId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
//            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

}
