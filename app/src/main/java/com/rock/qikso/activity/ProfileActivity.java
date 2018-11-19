package com.rock.qikso.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rock.donation.volleyWebservice.Constants;
import com.rock.qikso.Api.RewardRestInterface;
import com.rock.qikso.R;
import com.rock.qikso.fragment.userProfile.UserCommentsFragment;
import com.rock.qikso.fragment.userProfile.UserCreatedCampaignsFragment;
import com.rock.qikso.fragment.userProfile.UserFollowersFragment;
import com.rock.qikso.fragment.user_dashboard.MyCommentFragment;
import com.rock.qikso.fragment.user_dashboard.MyFundingFragment;
import com.rock.qikso.fragment.user_dashboard.MyProjectList;
import com.rock.qikso.fragment.user_dashboard.UserOverviewFragment;
import com.rock.qikso.localStorage.PreferencesHelper;
import com.rock.qikso.model.ProfileData;
//import com.rockers.qikso.volleyWebservice.Constants;

import com.rock.qikso.volleyWebservice.RequestParam;
import com.rock.qikso.volleyWebservice.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements
        UserCommentsFragment.OnFragmentInteractionListener,
        UserOverviewFragment.OnFragmentInteractionListener,
        UserFollowersFragment.OnFragmentInteractionListener,
        UserCreatedCampaignsFragment.OnFragmentInteractionListener {

    private PreferencesHelper preferencesHelper;
    private String token;
    private String userId;
    private ProfileData profileData;
    private RewardRestInterface restInterface;
    private TextView userEmail;
    private TextView userAddress;
    private TextView projects;
    private TextView followers;
    private TextView followings;
    private TextView comments;
    private TextView donations;

    private ImageView fbLink;
    private ImageView twLink;
    private ImageView lnLink;

    private ImageView userImage;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private int[] tabIcons = {
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_toc_black_24dp,
            R.drawable.ic_monetization_on_black_24dp,
            R.drawable.ic_attach_money_black_24dp,
            R.drawable.ic_comment_black_24dp
    };

    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        preferencesHelper = new PreferencesHelper(ProfileActivity.this);


        userEmail = (TextView)findViewById(R.id.user_email);
        userAddress = (TextView)findViewById(R.id.user_address);
        projects = (TextView)findViewById(R.id.project_count);
        followers = (TextView)findViewById(R.id.followers_count);
        followings = (TextView)findViewById(R.id.followings_count);
        comments = (TextView)findViewById(R.id.comments_count);
        donations = (TextView)findViewById(R.id.donations_count);

        userImage = (ImageView)findViewById(R.id.user_image);

        fbLink = (ImageView)findViewById(R.id.facebook_link);
        twLink = (ImageView)findViewById(R.id.twitter_link);
        lnLink = (ImageView)findViewById(R.id.linkedin_link);

        final Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.USER_ID);

        try {
            Log.e("sds",preferencesHelper.getPrefString(PreferencesHelper.USER_LOGIN));
            JSONObject userInfo = new JSONObject(preferencesHelper.getPrefString(PreferencesHelper.USER_LOGIN));
            userEmail.setText( userInfo.getString(Constants.EMAIL));

            Glide.with(this)
                    .load(userInfo.getString(Constants.USER_IMAGE))
                    .into(userImage);
            getSupportActionBar().setTitle(userInfo.getString(Constants.FIRSTNAME)+" "+userInfo.getString(Constants.LASTNAME));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        getUserInfo();
    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFrag(new UserOverviewFragment(), "ONE");
        mSectionsPagerAdapter.addFrag(new MyProjectList(), "TWO");
        mSectionsPagerAdapter.addFrag(new MyFundingFragment(), "THREE");
        mSectionsPagerAdapter.addFrag(new UserOverviewFragment(), "FOUR");
        mSectionsPagerAdapter.addFrag(new MyCommentFragment(), "FIVE");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Overview");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0,tabIcons[0], 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("My Projects");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[1], 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("My Funding");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[2], 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("My Fund");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[3], 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);

        TextView tabFive = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFive.setText("My Comments");
        tabFive.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[4], 0, 0);
        tabLayout.getTabAt(4).setCustomView(tabFive);
    }

    private void getUserInfo(){
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
        rp.putParams(Constants.USER_ID, userId);
    /*    restInterface = ApiClient.getClient().create(RewardRestInterface.class);
        Call<ResponseUserProfile> call = restInterface.UserProfile(token,userId);
        call.enqueue(new Callback<ResponseUserProfile>() {
            @Override
            public void onResponse(Call<ResponseUserProfile> call, Response<ResponseUserProfile> response) {
                try {
                    Gson gson = new Gson();
                    ArrayList<String> projectDetailDatas = new ArrayList<>();

               //     profileData = gson.fromJson(result.getJSONObject(Constants.DATA).toString(), ProfileData.class);

                    userAddress.setText(response.body().getData().getProfile().getAddress());

                    projects.setText(profileData.getTotalUserProject().toString());
                    comments.setText(profileData.getTotalUserComment().toString());
                    donations.setText(profileData.getTotalMyDonation().toString());
                    followers.setText(profileData.getTotalUserFollowers().toString());
                    followings.setText(profileData.getTotalUserFollowings().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseUserProfile> call, Throwable t) {

            }
        });*/
        objWebServiceHelper.apiParamsCall(Request.Method.POST, Constants.WebServiceUrls.USER_PROFILE , rp, new WebServiceHelper.OnWebServiceListener() {

            @Override
            public void successResponse(JSONObject result) {
                try {
                    Gson gson = new Gson();
                    profileData = gson.fromJson(result.getJSONObject(Constants.DATA).toString(), ProfileData.class);

                    userAddress.setText(profileData.getProfile().getAddress());

                    projects.setText(profileData.getTotalUserProject().toString());
                    comments.setText(profileData.getTotalUserComment().toString());
                    donations.setText(profileData.getTotalMyDonation().toString());
                    followers.setText(profileData.getTotalUserFollowers().toString());
                    followings.setText(profileData.getTotalUserFollowings().toString());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            preferencesHelper.clearSpecificSharedPreferences(PreferencesHelper.USER_LOGIN);
            preferencesHelper.putPrefBoolean(PreferencesHelper.USER_LOGGED_IN,false);
            Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab_activity_action, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
