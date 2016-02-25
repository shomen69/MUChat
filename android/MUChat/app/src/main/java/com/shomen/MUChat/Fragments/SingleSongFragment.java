package com.shomen.MUChat.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.shomen.MUChat.Adapters.RecyclerViewUserListAdapter;
import com.shomen.MUChat.Models.User;
import com.shomen.MUChat.R;
import com.shomen.MUChat.Singletons.UsersListController;
import com.shomen.MUChat.Utils.CONSTANT;
import com.shomen.MUChat.libs.LoginSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SingleSongFragment extends Fragment {

    private String LOG_TAG = "ASL_"+this.getClass().getSimpleName();
    private static final String ID = null;
    private static final String TITLE = null;
    private String mParam1;
    private String mParam2;
    private Gson gson = new Gson();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewUserListAdapter recyclerViewUserListAdapter;

    private List<User> user_list = new ArrayList<>();

    private UsersListController usersListController;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(CONSTANT.PRODUCTION_URL.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bind(R.id.rv)
    RecyclerView rv;

    public SingleSongFragment() {
        // Required empty public constructor
    }

    public static SingleSongFragment newInstance(String param1, String param2) {
        Log.d("ASL","inside newInstance param1 "+param1+" param2 "+param2);
        SingleSongFragment fragment = new SingleSongFragment();
        Bundle args = new Bundle();
        args.putString(ID, param1);
        args.putString(TITLE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "inside onCreate method");
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ID);
            mParam2 = getArguments().getString(TITLE);
        }*/
        mSocket.connect();
        usersListController = UsersListController.getIntance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "inside onCreateView method");
        View rootView = inflater.inflate(R.layout.fragment_single_song, container, false);

        ButterKnife.bind(this, rootView);

        if (getArguments() != null) {
            mParam1 = getArguments().getString("ID");
            mParam2 = getArguments().getString("TITLE");
        }
        LoginSession loginSess = new LoginSession(getContext());
        inItRV();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id",loginSess.getUserDetails().get("user_id"));
            obj.put("user_name",loginSess.getUserDetails().get("user_email"));
            obj.put("selected_room",mParam1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("join_to_room", obj);
        Log.d(LOG_TAG, "param1 " + mParam1 + " param2 " + mParam2);
        mSocket.on("for_everyone", onForEveryOne);
        mSocket.on("user_joined", onUserJoined);
        mSocket.on("you_are_logged_in",onYouAreLoggedIn);
        mSocket.on("user_leaved",onUserLeaved);
        mSocket.on("send_private_msg",onSendPrivateMsg);

//        getActivity().startService(new Intent(getActivity(), MyDemoService.class));

        return rootView;
    }


    private void inItRV(){

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);

        recyclerViewUserListAdapter = new RecyclerViewUserListAdapter();
        rv.setAdapter(recyclerViewUserListAdapter);
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

        mSocket.off("for_everyone", onForEveryOne);
        mSocket.off("user_joined", onUserJoined);
        mSocket.off("you_are_logged_in",onYouAreLoggedIn);
        mSocket.off("user_leaved",onUserLeaved);
        mSocket.off("send_private_msg",onSendPrivateMsg);
        mSocket.disconnect();
        usersListController.clearUserListMap();
        super.onDestroy();
    }

    ///////////////////////////////////////--------------EMITT--------------//////////////////////////////

    private Emitter.Listener onForEveryOne = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(LOG_TAG,"inside foreveryone emitter "+args[0].toString());

        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "inside onUserJoined emitter " + args[0].toString());
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(args[0].toString());
                        Log.d(LOG_TAG, "size of user list before  " + usersListController.getUserList().size());
                        usersListController.addUserToMap(new User(obj.get("id").toString(), obj.get("name").toString()));
//                          usersListController.addUser(new User(arrID.get(i).toString(), arrNAME.get(i).toString()));

                        Log.d(LOG_TAG, "size of user list after  " + usersListController.getUserList().size());
                        recyclerViewUserListAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    };


    private Emitter.Listener onYouAreLoggedIn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "inside onYouAreLoggedIn emitter " + args[0].toString());
                    JSONObject obj = null;

                    try {
                        obj = new JSONObject(args[0].toString());
                        JSONArray arrID = obj.getJSONArray("id");
                        JSONArray arrNAME = obj.getJSONArray("name");
                        Log.d(LOG_TAG, "size of user list before loop " + usersListController.getUserList().size());
                        for (int i = 0; i < arrID.length(); i++) {
                            Log.d(LOG_TAG, "array id values :" + arrID.get(i).toString());
                            usersListController.addUserToMap(new User(arrID.get(i).toString(), arrNAME.get(i).toString()));
//                            usersListController.addUser(new User(arrID.get(i).toString(), arrNAME.get(i).toString()));
                        }
                        Log.d(LOG_TAG, "size of user list after loop " + usersListController.getUserList().size());
                        recyclerViewUserListAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    };

    private Emitter.Listener onUserLeaved = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "inside onUserLeaved emitter " + args[0].toString());
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(args[0].toString());
                        Log.d(LOG_TAG, "inside onUserLeaved emitter json  " + obj.get("id").toString());
                        Log.d(LOG_TAG, "size of user list before  " + usersListController.getUserList().size());
                        usersListController.removeUserFromMap(obj.get("id").toString());
                        Log.d(LOG_TAG, "size of user list after  " + usersListController.getUserList().size());
                        recyclerViewUserListAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    };


    private Emitter.Listener onSendPrivateMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "inside onSendPrivateMsg emitter " + args[0].toString());
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(args[0].toString());
                        Log.d(LOG_TAG, "inside onUserLeaved emitter json  " + obj.get("from").toString());
                        usersListController.updateUser(obj.get("from").toString(),obj.get("msg").toString());
                        recyclerViewUserListAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    };



}
