package com.rock.qikso.fragment.user_dashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.rock.donation.volleyWebservice.Constants;
import com.rock.qikso.R;
import com.rock.qikso.TabActivityAction;
import com.rock.qikso.adapter.user_dashboard.MyProjectListAdapter;
import com.rock.qikso.localStorage.PreferencesHelper;
import com.rock.qikso.model.MyProjectsData;
import com.rock.qikso.utils.Utils;

import com.rock.qikso.volleyWebservice.RequestParam;
import com.rock.qikso.volleyWebservice.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProjectList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProjectList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProjectList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String token;
    private View view;
    private RecyclerView recyclerView;
    private MyProjectsData myProjectsData;
    private MyProjectListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


    public MyProjectList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProjectList.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProjectList newInstance(String param1, String param2) {
        MyProjectList fragment = new MyProjectList();
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

        view = inflater.inflate(R.layout.fragment_my_project_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_projects);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        getUserProjects();
        // Inflate the layout for this fragment
        return view;
    }

    private void getUserProjects(){

        WebServiceHelper objWebServiceHelper = new WebServiceHelper(getActivity());
        RequestParam rp = new RequestParam();

        String userId="";

        if(((TabActivityAction) getActivity()).preferencesHelper.getPrefBoolean(PreferencesHelper.USER_LOGGED_IN)){
            String loginUser = ((TabActivityAction) getActivity()).preferencesHelper.getPrefString(PreferencesHelper.USER_LOGIN);
            try {
                JSONObject jsonObject = new JSONObject(loginUser);
                token = jsonObject.getString("token");
                userId = jsonObject.getString(Constants.USER_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        rp.putHeader(Constants.TOKEN,token);
        rp.putParams(Constants.USER_ID,userId);


        objWebServiceHelper.apiParamsCall(Request.Method.POST, Constants.WebServiceUrls.USER_PROJECTS,rp,new WebServiceHelper.OnWebServiceListener(){

            @Override
            public void successResponse(JSONObject content) {

                Gson gson = new Gson();
                try {
                    myProjectsData = gson.fromJson(content.getString(Constants.DATA),MyProjectsData.class);

                    adapter = new MyProjectListAdapter(myProjectsData.getProjectsData(),getActivity(),MyProjectList.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(linearLayoutManager);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void failureResponse(String errorMessage, int errorCode) {
                Utils.showAlert(getActivity(), errorMessage);
            }
        });
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}