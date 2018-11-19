package com.rock.qikso.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.rock.donation.volleyWebservice.Constants;
import com.rock.qikso.R;
import com.rock.qikso.activity.HomeActivity;
import com.rock.qikso.adapter.ActivityListAdapter;
import com.rock.qikso.localStorage.PreferencesHelper;
import com.rock.qikso.model.MyActivitiesData;
import com.rock.qikso.utils.Utils;

import com.rock.qikso.volleyWebservice.RequestParam;
import com.rock.qikso.volleyWebservice.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyActivityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private MyActivitiesData myActivitiesData;
    private String token;
    private RecyclerView recyclerView;
    private View view;
    private ActivityListAdapter activityListAdapter;
    private LinearLayoutManager linearLayoutManager;


    public MyActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyActivityFragment newInstance(String param1, String param2) {
        MyActivityFragment fragment = new MyActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activity, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.list_activity);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        getActivities(0);
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getActivities(int offset){

        Utils.showLoader(getActivity());

        WebServiceHelper objWebServiceHelper = new WebServiceHelper(getActivity());
        RequestParam rp = new RequestParam();

        if(((HomeActivity) getActivity()).preferencesHelper.getPrefBoolean(PreferencesHelper.USER_LOGGED_IN)){
            String loginUser = ((HomeActivity) getActivity()).preferencesHelper.getPrefString(PreferencesHelper.USER_LOGIN);
            try {
                JSONObject jsonObject = new JSONObject(loginUser);
                token = jsonObject.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        rp.putHeader(Constants.TOKEN,token);
        rp.putParams(Constants.OFFSET,String.valueOf(offset));


        objWebServiceHelper.apiParamsCall(Request.Method.POST, Constants.WebServiceUrls.MY_ACTIVITIES,rp,new WebServiceHelper.OnWebServiceListener(){

            @Override
            public void successResponse(JSONObject content) {

                Gson gson = new Gson();
                try {
                    myActivitiesData = gson.fromJson(content.getJSONObject(Constants.DATA).toString(),MyActivitiesData.class);
                    if(myActivitiesData.getActivities().size()>0){
//                        adapter.clear();
                        recyclerView.setLayoutManager(linearLayoutManager);
                        activityListAdapter = new ActivityListAdapter(myActivitiesData.getActivities(),getActivity(),MyActivityFragment.this);
                        recyclerView.setAdapter(activityListAdapter);

                    }else{
                        recyclerView.setVisibility(View.GONE);
                    }
                    Utils.hideLoader(getActivity());
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void failureResponse(String errorMessage, int errorCode) {
                Log.i("Login Fail Response", errorMessage);
                Utils.hideLoader(getActivity());
                Utils.showAlert(getActivity(), errorMessage);
            }
        });
    }
}
