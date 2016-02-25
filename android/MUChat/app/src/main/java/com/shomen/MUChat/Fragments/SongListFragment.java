package com.shomen.MUChat.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.shomen.MUChat.Singletons.VolleyController;
import com.shomen.MUChat.libs.LoginSession;
import com.shomen.MUChat.Adapters.RecyclerViewSongListAdapter;
import com.shomen.MUChat.Interfaces.OnItemClickListener;
import com.shomen.MUChat.Models.ServerResponse;
import com.shomen.MUChat.Models.SongListDataModels;
import com.shomen.MUChat.R;
import com.shomen.MUChat.Singletons.SongListController;
import com.shomen.MUChat.SongPlayerActivity;
import com.shomen.MUChat.Utils.CONSTANT;
import com.shomen.MUChat.libs.RoomsSession;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alchemy software on 1/16/2016.
 */
public class SongListFragment extends Fragment{

    private String LOG_TAG = "ASL_"+this.getClass().getSimpleName() ;

    private RecyclerView rvSongList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewSongListAdapter rvSongListAdapter;
    private List<SongListDataModels> songListDataModels = new ArrayList<>();
    private SongListController songListController = SongListController.getIntance();
    protected Handler handler;
    private ProgressDialog progressDialog;
    private Gson gson = new Gson();
    private LoginSession loginSession ;

    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "inside onCreate method");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "inside onCreateView method");

        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        rvSongList = (RecyclerView) rootView.findViewById(R.id.rv_song_list);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvSongList.setLayoutManager(linearLayoutManager);
        rvSongList.setHasFixedSize(true);

        handler = new Handler();

        initializeData();
        initializeAdapter();

        loginSession = new LoginSession(getContext());
        for (Map.Entry<String,String> entry : loginSession.getUserDetails().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.d(LOG_TAG,"inside for loop map key :"+key+" value "+value);
        }
        inItPDialouge();
        if(loginSession.isLoggedIn()){
            requestToServer();
        }
        return rootView;
    }

    private void initializeData(){
        songListDataModels = new ArrayList<>();
/*        for(int i=0 ; i<20 ; i++){
            songListDataModels.add(new SongListDataModels().setTitle("Shomen "+i););
        }*/
    }

    private void initializeAdapter(){
        rvSongListAdapter = new RecyclerViewSongListAdapter(songListDataModels,rvSongList);
        rvSongList.setAdapter(rvSongListAdapter);
        rvSongListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                startSongPlayerActivity(position);
//                Toast.makeText(getActivity(),"item clicked...position:"+position,Toast.LENGTH_SHORT).show();
            }
        });
/*        rvSongListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(LOG_TAG, "onLoadMore get called");
                songListDataModels.add(null);
                rvSongListAdapter.notifyItemInserted(songListDataModels.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        songListDataModels.remove(songListDataModels.size() - 1);
                        rvSongListAdapter.notifyItemRemoved(songListDataModels.size());
                        for (int i = 0; i < 15; i++) {
                            songListDataModels.add(new SongListDataModels("Shomen " + i));
                        }
                        rvSongListAdapter.notifyItemInserted(songListDataModels.size());
                        rvSongListAdapter.setLoaded();
                    }
                }, 2000);
                System.out.println("load");
            }
        });*/
    }

    private void startSongPlayerActivity(int pos){
        Log.d(LOG_TAG, "inside startSongPlayerActivity " + songListDataModels.get(pos).get_id());

        createroomSession(pos);
        Intent intent = new Intent(getActivity(),SongPlayerActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("POS", pos);
        intent.putExtra("ID", songListDataModels.get(pos).get_id());
        intent.putExtra("TITLE", songListDataModels.get(pos).getTitle());
        startActivity(intent);
    }

    private void createroomSession(int pos){
        RoomsSession roomsSession = new RoomsSession(getActivity());
        roomsSession.createRoomSession( songListDataModels.get(pos).get_id(),
                                        songListDataModels.get(pos).getTitle() );
        Log.d(LOG_TAG,"roomsession value "+roomsSession.getRoomDetails().get(RoomsSession.ROOM_NAME));
    }

    private void startSingleSongFragment(int pos){
        Log.d(LOG_TAG,"inside startSingleSongFragment "+songListDataModels.get(pos).get_id());
//        SingleSongFragment fragment = SingleSongFragment.newInstance(songListDataModels.get(pos).get_id(),
//                                                                        songListDataModels.get(pos).getTitle());
        SingleSongFragment fragment = new SingleSongFragment();
        Bundle args = new Bundle();
        args.putString("ID", songListDataModels.get(pos).get_id());
        args.putString("TITLE", songListDataModels.get(pos).getTitle());
        fragment.setArguments(args);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_act_fragment_container, fragment,"SingleSongFragment");
        ft.addToBackStack("SingleSongFragment");
        ft.commit();
    }

    private void inItPDialouge(){
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
    }

    private void showPDialouge(){
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hidePDialouge(){
        if(progressDialog.isShowing())
            progressDialog.hide();
    }

    private void requestToServer(){
        showPDialouge();
        JsonObjectRequest jsonObjPostReq = new JsonObjectRequest(Request.Method.GET, CONSTANT.PRODUCTION_URL.SONG_LIST_URL+"/"+loginSession.getToken(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        ServerResponse res = gson.fromJson(response.toString(),ServerResponse.class);
                        Log.d(LOG_TAG, "---------" + res.toString());

                       /* for (Results d:res.getResults() ) {
                            Log.d(LOG_TAG,"---------"+d.toString());
                        }

                        for (SongListDataModels sd:res.getResults().get(0).getSonglist() ) {
                            Log.d(LOG_TAG,"---------"+sd.toString());
                        }*/

                        if(res.getStatus() != null && res.getStatus().equals("success") && !res.getResults().isEmpty()){
                            songListDataModels.addAll(res.getResults().get(0).getSonglist());
                            songListController.getSongsList().addAll(res.getResults().get(0).getSonglist());
                            rvSongListAdapter.notifyDataSetChanged();
                        }else{
                            Log.d(LOG_TAG, "not authorized...");
                            loginSession.logoutUser();
                            getFragmentManager().beginTransaction().replace(R.id.main_act_fragment_container, new LoginFragment(), "LoginFragment").commit();
                        }
                        hidePDialouge();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "Error: " + error.getMessage());
                        hidePDialouge();
                    }
                });

        VolleyController.getInstance(getContext()).addToRequestQueue(jsonObjPostReq,"tag");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(LOG_TAG, "inside onAttach method");
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "inside onDetach method");
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "inside onPause method");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "inside onResume method");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "inside onStop method");
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "inside onDestoy method");
        super.onDestroy();
    }

}
