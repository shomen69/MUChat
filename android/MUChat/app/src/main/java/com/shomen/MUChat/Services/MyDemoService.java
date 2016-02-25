package com.shomen.MUChat.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.shomen.MUChat.Singletons.BusProvider;
import com.shomen.MUChat.Models.User;
import com.shomen.MUChat.Singletons.UsersListController;
import com.shomen.MUChat.Utils.CONSTANT;
import com.shomen.MUChat.Utils.EventType;
import com.shomen.MUChat.libs.LoginSession;
import com.shomen.MUChat.libs.RoomsSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyDemoService extends Service {

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();
    private MyTimetTask myTimetTask;
    private UsersListController usersListController = UsersListController.getIntance();

    private Socket mSocket;
    private String song_id;
    private String song_name;
    private RoomsSession roomSession;
    private final IBinder chatServiceBinder = new ChatServiceBinder();
    private Handler joinRoomEmitterHandler = new Handler();
    private Timer timer;
    private boolean isReConnected = false;

    private PowerManager.WakeLock wakeLock;

    {
        try {
            mSocket = IO.socket(CONSTANT.PRODUCTION_URL.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public MyDemoService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "inside onBind....");
        return chatServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "inside onUnbind....");
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_TAG,"inside onRebind method");
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "inside onCreate method");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        BusProvider.getInstance().register(this);
        roomSession = new RoomsSession(getApplicationContext());
        mSocket.connect();
        initializeSocket();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "inside onStartCommand method");
//        song_id = intent.getStringExtra("ID");
//        song_name = intent.getStringExtra("TITLE");
        join_to_room();
//        timer.schedule(new MyTimetTask(), 1000, 5000);

        return START_NOT_STICKY;
    }

    public void join_to_room(){
        joinRoomEmitterHandler.removeCallbacks(joinRoomEmitterEvent);
        joinRoomEmitterHandler.postDelayed(joinRoomEmitterEvent,1000);
    }

    private void join_to_room(String str){
        Log.d(LOG_TAG,"inside join_to_room method....");
        song_id = roomSession.getRoomDetails().get(RoomsSession.ROOM_ID);
        song_name = roomSession.getRoomDetails().get(RoomsSession.ROOM_NAME);
        Log.d(LOG_TAG, "roomsession value " + roomSession.getRoomDetails().get(RoomsSession.ROOM_NAME));

        LoginSession loginSess = new LoginSession(getApplicationContext());
        final JSONObject obj = new JSONObject();
        try {
            obj.put("user_id",loginSess.getUserDetails().get("user_id"));
            obj.put("user_name",loginSess.getUserDetails().get("user_email"));
            obj.put("selected_room",song_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("join_to_room", obj);
    }

    public void leave_from_room(){
        mSocket.emit("leave_from_room");
        BusProvider.postOnMain(EventType.LEAVE_ROOM);
//        usersListController.clearUserList();
    }

    public void initializeSocket(){
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT, onConnected);
        mSocket.on(Socket.EVENT_RECONNECT,onReconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("for_everyone", onForEveryOne);
        mSocket.on("user_joined", onUserJoined);
        mSocket.on("you_are_logged_in", onYouAreLoggedIn);
        mSocket.on("user_leaved", onUserLeaved);
        mSocket.on("send_private_msg", onSendPrivateMsg);
        mSocket.on("typing", onTyping);
        mSocket.on("stop_typing", onStopTyping);
        mSocket.on("demo_event", onDemoEvent);
    }

    private void clearSocket(){
        mSocket.off("for_everyone", onForEveryOne);
        mSocket.off("user_joined", onUserJoined);
        mSocket.off("you_are_logged_in",onYouAreLoggedIn);
        mSocket.off("user_leaved",onUserLeaved);
        mSocket.off("send_private_msg", onSendPrivateMsg);
        mSocket.off("typing", onTyping);
        mSocket.off("stop_typing", onStopTyping);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT,onConnected);
        mSocket.off(Socket.EVENT_RECONNECT, onReconnect);
        mSocket.off("demo_event", onDemoEvent);
        mSocket.disconnect();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "inside onDestroy method");
        clearSocket();
        wakeLock.release();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOG_TAG, "inside onTaskRemoved method");
        clearSocket();
        wakeLock.release();
        super.onTaskRemoved(rootIntent);
    }

    public class ChatServiceBinder extends Binder {
        public MyDemoService getService() {
            return MyDemoService.this;
        }
    }


///////////////////////////////////----Emitters-----/////////////////////////////////////////////////////////


    private Emitter.Listener onForEveryOne = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(LOG_TAG,"inside foreveryone emitter "+args[0].toString());
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(LOG_TAG, "inside onUserJoined emitter " + args[0].toString());
            JSONObject obj = null;
            try {
                obj = new JSONObject(args[0].toString());
                Log.d(LOG_TAG, "size of user list before  " + usersListController.getUserList().size());
                usersListController.addUserToMap(new User(obj.get("id").toString(), obj.get("name").toString()));
//              usersListController.addUser(new User(arrID.get(i).toString(), arrNAME.get(i).toString()));
                Log.d(LOG_TAG, "size of user list after  " + usersListController.getUserList().size());

                BusProvider.postOnMain(EventType.USER_JOINED);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener onYouAreLoggedIn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(LOG_TAG, "inside onYouAreLoggedIn emitter " + args[0].toString());
            usersListController.clearUserListMap();
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

                BusProvider.postOnMain(EventType.YOU_ARE_JOINED);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener onUserLeaved = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(LOG_TAG, "inside onUserLeaved emitter " + args[0].toString());
            JSONObject obj = null;
            try {
                obj = new JSONObject(args[0].toString());
                Log.d(LOG_TAG, "inside onUserLeaved emitter json  " + obj.get("id").toString());
                Log.d(LOG_TAG, "size of user list before  " + usersListController.getUserList().size());
                usersListController.removeUserFromMap(obj.get("id").toString());
                Log.d(LOG_TAG, "size of user list after  " + usersListController.getUserList().size());
                BusProvider.postOnMain("done");
                BusProvider.postOnMain(EventType.USER_LEFT);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onSendPrivateMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(LOG_TAG, "inside onSendPrivateMsg emitter " + args[0].toString());
            JSONObject obj = null;
            try {
                obj = new JSONObject(args[0].toString());
                Log.d(LOG_TAG, "inside onSendPrivateMsg emitter json  " + obj.get("from").toString());
                usersListController.updateUser(obj.get("from").toString(), obj.get("msg").toString());

                BusProvider.postOnMain(EventType.NEW_MESSAGE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(args[0].toString());
                Log.d(LOG_TAG, "inside onSendPrivateMsg emitter json  " + obj.get("from").toString());
                if(usersListController.getUserListMap().get(obj.get("from").toString()).isChatting()){
                    BusProvider.postOnMain(EventType.TYPING);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(args[0].toString());
                Log.d(LOG_TAG, "inside onStopTyping emitter json  " + obj.get("from").toString());
                if(usersListController.getUserListMap().get(obj.get("from").toString()).isChatting()){
                    BusProvider.postOnMain(EventType.STOP_TYPING);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onDemoEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(LOG_TAG, "inside onDemoEvent emitter " + mSocket.connected());
        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            if(isReConnected)
                join_to_room();
            BusProvider.postOnMain(EventType.CONNECTED);
        }
    };

    private Emitter.Listener onReconnect = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            Log.d(LOG_TAG, "inside onReconnect emitter" + mSocket.connected());
            isReConnected = true;
            //here i have to do some thing like add the user if room session is set...may be using a flag for checkin reconnection....
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            BusProvider.postOnMain(EventType.CONNECTION_ERROR);
            Log.d(LOG_TAG, "inside onConnectError emitter "+mSocket.connected());
//            clearSocket();
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            BusProvider.postOnMain(EventType.DISCONNECTED);
            Log.d(LOG_TAG, "inside onDisconnect emitter " + mSocket.connected());
        }
    };

    /////////////////////////////////////////////////////////////////////////////////////////////

    public class MyTimetTask extends TimerTask {
        double i =0;
        Context context;

        public MyTimetTask() {
            this.i = Math.random();
            BusProvider.getInstance().register(this);
        }

        @Override
        public void run() {
            Log.d(LOG_TAG, "inside timer task..." + i);

        }
    }

    private Runnable joinRoomEmitterEvent = new Runnable() {
        @Override
        public void run() {
            join_to_room("string");
        }
    };

}
