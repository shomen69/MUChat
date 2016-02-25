package com.shomen.MUChat.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.shomen.MUChat.Models.SongListDataModels;
import com.shomen.MUChat.Singletons.SongListController;
import com.shomen.MUChat.SongPlayerActivity;
import com.shomen.MUChat.Utils.CONSTANT;
import com.shomen.MUChat.libs.RoomsSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by server on 1/23/2016.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private MediaPlayer player;
    private List<SongListDataModels> songsList = new ArrayList<>();
    private SongListController songListController = SongListController.getIntance();
    RoomsSession roomsSession;

    private int currentSongPos = 0;

    private final IBinder musicBind = new MusicBinder();

    private final String LOGTAG = "ASL_"+this.getClass().getSimpleName();
    private Intent chatServiceIntent;
    private MyDemoService chatService;
    private boolean chatBound = false;

    private ServiceConnection chatServiceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOGTAG,"inside onServiceConnected method....");
            MyDemoService.ChatServiceBinder binder = (MyDemoService.ChatServiceBinder) service;
            chatService = binder.getService();
            chatBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOGTAG,"inside onServiceDisconnected method....");
            chatBound = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        roomsSession = new RoomsSession(getApplicationContext());
        Log.d(LOGTAG, "inside onCreate...."+roomsSession.getRoomDetails().get("room_id")+roomsSession.getRoomDetails().get("room_name"));
        player = new MediaPlayer();
        initMusicPlayer();
        chatServiceIntent = new Intent(this, MyDemoService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOGTAG, "inside onStartCommand....");

        if (intent.getAction().equals(CONSTANT.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOGTAG, "Received Start Foreground Intent ");

            Intent notificationIntent = new Intent(this, SongPlayerActivity.class);
            notificationIntent.setAction(CONSTANT.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent previousIntent = new Intent(this, MusicPlayerService.class);
            previousIntent.setAction(CONSTANT.ACTION.PREV_ACTION);

            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

            Intent stopIntent = new Intent(this, MusicPlayerService.class);
            stopIntent.setAction(CONSTANT.ACTION.STOP_ACTION);

            PendingIntent pstopIntent = PendingIntent.getService(this, 0,
                stopIntent, 0);

            Intent nextIntent = new Intent(this, MusicPlayerService.class);
            nextIntent.setAction(CONSTANT.ACTION.NEXT_ACTION);

            PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("My Music Player")
                .setTicker("My Music Player")
                .setContentText(roomsSession.getRoomDetails().get("room_id"))
                .setSmallIcon(android.support.design.R.drawable.abc_list_pressed_holo_dark)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .addAction(android.R.drawable.ic_media_previous,
                        CONSTANT.ACTION.PREV_ACTION, ppreviousIntent)
                .addAction(android.R.drawable.ic_media_pause, CONSTANT.ACTION.STOP_ACTION,
                        pstopIntent)
                .addAction(android.R.drawable.ic_media_next, CONSTANT.ACTION.NEXT_ACTION,
                        pnextIntent)
                .build();

            startForeground(CONSTANT.NOTIFICATION_ID.MUSIC_SERVICE_NOTI_ID,
                notification);

        } else if (intent.getAction().equals(CONSTANT.ACTION.PREV_ACTION)) {
            Log.i(LOGTAG, "Clicked Previous");
            currentSongPos--;
            if(currentSongPos < 0){
                currentSongPos = songListController.getSongsListSize() - 1;
            }
            playSong(currentSongPos);
            chatService.leave_from_room();
            chatService.join_to_room();

        } else if (intent.getAction().equals(CONSTANT.ACTION.STOP_ACTION)) {
            Log.i(LOGTAG, "Clicked Stop");

        } else if (intent.getAction().equals(CONSTANT.ACTION.NEXT_ACTION)) {
            Log.i(LOGTAG, "Clicked Next");
            currentSongPos++;
            if(currentSongPos > songListController.getSongsListSize()-1){
                currentSongPos = 0;
            }
            playSong(currentSongPos);
            chatService.leave_from_room();
            chatService.join_to_room();

        }else if (intent.getAction().equals(CONSTANT.ACTION.DO_NOTHING)){
            Log.i(LOGTAG, "Do nothing");
            int pos = intent.getIntExtra("position",0);
            playSong(pos);

            Intent musicServiceIntent = new Intent(this, MusicPlayerService.class);
            musicServiceIntent.setAction(CONSTANT.ACTION.STARTFOREGROUND_ACTION);
            startService(musicServiceIntent);
            startChatService();

        }else if (intent.getAction().equals(CONSTANT.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOGTAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void startChatService(){
        chatServiceIntent.putExtra("name", "shomen");
        chatServiceIntent.putExtra("ID", roomsSession.getRoomDetails().get("room_id"));
        chatServiceIntent.putExtra("TITLE", roomsSession.getRoomDetails().get("room_name"));
        bindService(chatServiceIntent, chatServiceConnection, Context.BIND_AUTO_CREATE);
        startService(chatServiceIntent);
    }

    @Override
    public void onDestroy() {
        Log.d(LOGTAG,"inside onDestroy....");
        stopForeground(true);
        unbindService(chatServiceConnection);
        stopService(chatServiceIntent);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, "inside onBind....");
//        playSong(0);
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOGTAG, "inside onUnbind....");
        player.stop();
        player.release();
        return false;
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playSong(int pos){
        currentSongPos = pos;
        roomsSession.createRoomSession( songListController.getSongsList().get(currentSongPos).get_id(),
                                            songListController.getSongsList().get(currentSongPos).getTitle() );

        player.reset();

        String songID = roomsSession.getRoomDetails().get("room_id");
        try{
            player.setDataSource(getApplicationContext(),Uri.parse(CONSTANT.PRODUCTION_URL.BASE_URL+"/songs/"+songID+".mp3"));
        }
        catch(Exception e){
            Log.e(LOGTAG, "Error setting data source", e);
        }
        player.prepareAsync();

    }

    public void reConnect(){
        chatService.initializeSocket();
        chatService.join_to_room();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOGTAG, "inside onPrepared....");
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOGTAG, "inside onCompletion....");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v(LOGTAG, "Playback Error what extra " + what + " " + extra);
        mp.reset();
        return false;
    }

    public int getCurrentPos(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
}
