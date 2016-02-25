package com.shomen.MUChat;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shomen.MUChat.Adapters.RecyclerViewUserListAdapter;
import com.shomen.MUChat.Fragments.ChatFragment;
import com.shomen.MUChat.Interfaces.OnItemClickListener;
import com.shomen.MUChat.Services.MusicPlayerService;
import com.shomen.MUChat.Services.MyDemoService;
import com.shomen.MUChat.Singletons.BusProvider;
import com.shomen.MUChat.Singletons.UsersListController;
import com.shomen.MUChat.Utils.CONSTANT;
import com.shomen.MUChat.Utils.EventType;
import com.shomen.MUChat.Utils.Utils;
import com.shomen.MUChat.libs.LoginSession;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SongPlayerActivity extends AppCompatActivity {
    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();

    private Gson gson = new Gson();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewUserListAdapter recyclerViewUserListAdapter;
    private UsersListController usersListController;

    private int song_pos;
    private String song_id;
    private String song_name;
    private MusicPlayerService musicServ;
    private Intent musicIntent;
    private Intent chatIntent;
    private boolean musicBound = false;

    private boolean removeFragment = false;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG_TAG, "inside onServiceConnected method");
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            musicServ = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "inside onServiceDisconnected method");
            musicBound = false;
        }
    };

    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.tv_one)
    TextView tv_one;

    private Toolbar toolbar;
    private TextView tv ;
    private RelativeLayout rl_parent;
    private ProgressDialog pDialog;

    private FragmentManager frag_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "inside onCreate method");

        BusProvider.getInstance().register(this);

        invalidateOptionsMenu();
        setContentView(R.layout.activity_song_player);

        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rl_parent= (RelativeLayout) findViewById(R.id.ac_so_pl_parent_rl);
        toolbar.setTitle("Title");
        toolbar.setBackgroundColor(Color.BLUE);
        setSupportActionBar(toolbar);

/*        Button btn = (Button) findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.tv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment fragment = new ChatFragment();

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.ac_so_pl_parent_rl, fragment, "ChatFragment");
                ft.addToBackStack("ChatFragment");
                ft.commit();
            }
        });*/

/*        Timer timer = new Timer();
        TimerTask timerTask = new MyTimetTask();
        timer.schedule(timerTask, 1000, 1000);*/

        if(new LoginSession(this).isLoggedIn()){
//            initializeSocket();
            usersListController = UsersListController.getIntance();

            Intent intent = getIntent();

            song_pos = intent.getIntExtra("POS", 0);
            song_id = intent.getStringExtra("ID");
            song_name = intent.getStringExtra("TITLE");

            toolbar.setTitle(song_name);

            Log.d(LOG_TAG, "param1 " + song_id + " param2 " + song_name + intent.getStringExtra("name"));

            LoginSession loginSess = new LoginSession(this);
            initializeAdapter();

//            startChatService();
            startMusicService();

            inItProgDialog();
            frag_manager = getSupportFragmentManager();

        }else{
            finish();
        }

    }

    private void inItProgDialog(){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hideProgDialog(){
            pDialog.hide();
    }

    private void showProgDialog(){
            pDialog.show();
    }

    private void startChatService(){
        chatIntent = new Intent(this, MyDemoService.class);
        chatIntent.putExtra("name", "shomen");
        chatIntent.putExtra("ID", song_id);
        chatIntent.putExtra("TITLE", song_name);
        startService(chatIntent);
    }

    private void startMusicService(){
        if(musicIntent == null){
            Log.d(LOG_TAG, "inside playintent check...");
            musicIntent = new Intent(this, MusicPlayerService.class);
            musicIntent.setAction(CONSTANT.ACTION.DO_NOTHING);
            musicIntent.putExtra("position", song_pos);
            bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicIntent);
        }

    }

    private void initializeAdapter(){
        linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);

        recyclerViewUserListAdapter = new RecyclerViewUserListAdapter();

        recyclerViewUserListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                startChatFragment(position);
                Toast.makeText(getApplicationContext(), "item clicked...position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        rv.setAdapter(recyclerViewUserListAdapter);
    }

    private void startChatFragment(int position){
        ChatFragment fragment = new ChatFragment();

        Bundle args = new Bundle();
        args.putString("ID", usersListController.getUserList().get(position).getId());
        args.putString("NAME", usersListController.getUserList().get(position).getName());
        fragment.setArguments(args);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.ac_so_pl_parent_rl, fragment, "ChatFragment");
        ft.addToBackStack("ChatFragment");
        ft.commit();
    }

    @Subscribe
    public void onEvent(EventType s) {
        Log.d(LOG_TAG, "inside new event type " + s.toString());

        if(s.equals(EventType.CONNECTION_ERROR)){
            toolbar.setTitle("Connection lost.....");
            Utils.showToast(this, "Connection Error.....");
            return;
        }
        if(s.equals(EventType.DISCONNECTED)){
            toolbar.setTitle("Disconnected.....");
            showProgDialog();
            return;
        }
        if(s.equals(EventType.CONNECTED)){
            toolbar.setTitle("CONNECTED.....");
            hideProgDialog();
            return;
        }
        if(s.equals(EventType.CLEAR_NOTI)){
            recyclerViewUserListAdapter.notifyDataSetChanged();
            return;
        }
        if(s.equals(EventType.USER_LEFT)){
            recyclerViewUserListAdapter.notifyDataSetChanged();
            return;
        }
        if(s.equals(EventType.USER_JOINED)){
            recyclerViewUserListAdapter.notifyDataSetChanged();
            return;
        }
        if(s.equals(EventType.YOU_ARE_JOINED)){
            recyclerViewUserListAdapter.notifyDataSetChanged();
            return;
        }
        if(s.equals(EventType.NEW_MESSAGE)){
            recyclerViewUserListAdapter.notifyDataSetChanged();
            return;
        }
        if(s.equals(EventType.LEAVE_ROOM)){
            if(removeFragment){
                return;
            }else{
                ChatFragment fr = (ChatFragment) frag_manager.findFragmentByTag("ChatFragment");
                if (fr != null && fr.isVisible()) {
                    frag_manager.popBackStackImmediate();
                }
            }
            return;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(removeFragment){
            removeFragment = false;
            ChatFragment fr = (ChatFragment) frag_manager.findFragmentByTag("ChatFragment");
            if (fr != null ) {
                frag_manager.popBackStackImmediate();
            }
        }
        Log.d(LOG_TAG, "inside onStart method");
        usersListController = UsersListController.getIntance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "inside onResume method");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "inside onPause method");
        removeFragment = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "inside onStop method");
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "inside onDestroy method");

//        stopService(chatIntent);

        unbindService(musicConnection);
        stopService(musicIntent);

        usersListController.clearUserListMap();
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "inside onCreateOptionsMenu method");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "inside onPrepareOptionsMenu method");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////--------------EMITT--------------//////////////////////////////
    {
/*        private void initializeSocket(){
            mSocket.connect();
            mSocket.on("for_everyone", onForEveryOne);
            mSocket.on("user_joined", onUserJoined);
            mSocket.on("you_are_logged_in", onYouAreLoggedIn);
            mSocket.on("user_leaved", onUserLeaved);
            mSocket.on("send_private_msg", onSendPrivateMsg);
        }

        private void clearSocket(){
            mSocket.off("for_everyone", onForEveryOne);
            mSocket.off("user_joined", onUserJoined);
            mSocket.off("you_are_logged_in", onYouAreLoggedIn);
            mSocket.off("user_leaved", onUserLeaved);
            mSocket.off("send_private_msg", onSendPrivateMsg);
            mSocket.disconnect();
            mSocket.disconnect();
        }*/


    /*private Emitter.Listener onForEveryOne = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(LOG_TAG,"inside foreveryone emitter "+args[0].toString());

        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
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
    };*/
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////

}
