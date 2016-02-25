package com.shomen.MUChat.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.shomen.MUChat.Adapters.RecyclerViewChatListAdapter;
import com.shomen.MUChat.Models.User;
import com.shomen.MUChat.Singletons.BusProvider;
import com.shomen.MUChat.Singletons.UsersListController;
import com.shomen.MUChat.Utils.CONSTANT;
import com.shomen.MUChat.Utils.EventType;
import com.shomen.MUChat.libs.LoginSession;
import com.shomen.MUChat.Models.Conversation;
import com.shomen.MUChat.R;
import com.shomen.MUChat.Utils.Utils;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by server on 1/27/2016.
 */
public class ChatFragment  extends Fragment{

    @Bind(R.id.rc_view)
    RecyclerView rv;
    @Bind(R.id.editText)
    EditText et;
    @Bind(R.id.button2)
    Button btn;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(CONSTANT.PRODUCTION_URL.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();

    private UsersListController usersListController = UsersListController.getIntance();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewChatListAdapter recyclerViewChatListAdapter;
    private String user_id,user_name;
    private Handler typinghandler = new Handler();
    private boolean istyping = false;
    private LoginSession loginSession;
    private Toolbar toolbar;

    public ChatFragment(){}

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
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        ButterKnife.bind(this, rootView);
        BusProvider.getInstance().register(this);
        loginSession = new LoginSession(getContext());

        if (getArguments() != null) {
            usersListController = UsersListController.getIntance();

            user_id = getArguments().getString("ID");
            user_name = getArguments().getString("NAME");

            toolbar.setTitle(user_name);
            toolbar.setBackgroundColor(Color.GREEN);

            User u = usersListController.getUserListMap().get(user_id);
            List<Conversation> conv =u.getConversationList();
            u.setIsChatting(true);
            inItRV(getArguments().getString("ID"));

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(LOG_TAG,"inside on text changed..mSocket is connected "+mSocket.connected());
                    if (!mSocket.connected()) return;
                    if (!istyping) {
                        istyping = true;
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("from",loginSession.getUserDetails().get("user_id"));
                            obj.put("to",user_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSocket.emit("typing",obj);
                    }
                    typinghandler.removeCallbacks(onTypingTimeout);
                    typinghandler.postDelayed(onTypingTimeout, 500);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            if(conv.size()>0) {
                u.setNoti(0);
                BusProvider.postOnMain(EventType.CLEAR_NOTI);
            }

        }else{
            this.getFragmentManager().popBackStackImmediate();
        }
        return rootView;
    }

    private void inItRV(String id){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);

        recyclerViewChatListAdapter = new RecyclerViewChatListAdapter(id);
        rv.setAdapter(recyclerViewChatListAdapter);
    }

    @OnClick(R.id.button2)
    public void sendMessage() {
        if(!mSocket.connected()) {
            Utils.showToast(getActivity(),"you are not connected to the chat server...please try again..");
            return;
        }
        usersListController.isUserExist(user_id);
        if (!TextUtils.isEmpty(et.getText().toString())){

            if(!usersListController.getUserListMap().get(user_id).isOnline()){
                Utils.showToast(getActivity(),"this user is not available");
                et.setText("");
                hideSoftKeyboard(et);
                return;
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put("from",loginSession.getUserDetails().get("user_id"));
                obj.put("to",user_id);
                obj.put("room","2222");
                obj.put("msg",et.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            usersListController.getUserListMap().get(user_id).addConversation(new Conversation(true,et.getText().toString().trim()));
            recyclerViewChatListAdapter.notifyDataSetChanged();
            et.setText("");
            hideSoftKeyboard(et);
            linearLayoutManager.scrollToPosition(usersListController.getUserList().size());
            mSocket.emit("private_chat", obj);
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    @Subscribe
    public void onEvent(EventType s) {

        Log.d(LOG_TAG,"inside event "+s.toString());

        if(s.equals(EventType.TYPING)){
            Log.d(LOG_TAG,"--------- typing");
            toolbar.setSubtitle("typing...");
            return;
        }
        if(s.equals(EventType.STOP_TYPING)){
            Log.d(LOG_TAG,"------------ stop typing");
            toolbar.setSubtitle("");
            return;
        }
        if(s.equals(EventType.USER_LEFT)){
            Log.d(LOG_TAG, "user left");
            if(usersListController.getUserListMap().get(user_id).isChatting()) {
                toolbar.setBackgroundColor(Color.RED);
            }
            return;
        }
        if(s.equals(EventType.USER_JOINED)){
            Log.d(LOG_TAG,"user joined");
            if(usersListController.getUserListMap().get(user_id).isChatting()) {
                toolbar.setBackgroundColor(Color.GREEN);
            }
            return;
        }
        if(s.equals(EventType.NEW_MESSAGE)){
            Log.d(LOG_TAG,"user NEW_MESSAGE");
            recyclerViewChatListAdapter.notifyDataSetChanged();
            rv.scrollToPosition(usersListController.getUserListMap().get(user_id).getConversationList().size()-1);
            return;
        }

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
        BusProvider.getInstance().unregister(this);

        User u = usersListController.getUserListMap().get(user_id);
        if(u != null){
            u.setIsChatting(false);
            if(!u.isOnline()){
                usersListController.removeUserFromMap(user_id);
                BusProvider.postOnMain(EventType.USER_LEFT);
            }
        }
        toolbar.setTitle("Title");
        toolbar.setBackgroundColor(Color.BLUE);
        super.onDestroy();
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!istyping) return;
            istyping = false;
            JSONObject obj = new JSONObject();
            try {
                obj.put("from",loginSession.getUserDetails().get("user_id"));
                obj.put("to",user_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("stop_typing",obj);
        }
    };

}
