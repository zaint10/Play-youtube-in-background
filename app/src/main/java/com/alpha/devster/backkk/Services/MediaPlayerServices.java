package com.alpha.devster.backkk.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;

public class MediaPlayerServices extends Service {

    private static MediaPlayerServices mInstance=null;
    private MediaSession mSession;

    private static final String ACTION_PLAY="action_play";
    private static final String ACTION_PAUSE="action_pause";
    private static final String ACTION_NEXT="action_next";
    private static final String ACTION_PREV="action_prev";
    private static final String ACTION_EXIT="action_exit";

    private MediaSessionManager mManager;
    private MediaPlayer mPlayer;
    private MediaController mController;


    public static boolean isServiceCreated(){
        return mInstance!=null;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //mSession.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInstance=null;
        stopSelf();
    }
}
