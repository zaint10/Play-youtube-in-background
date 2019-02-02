package com.alpha.devster.backkk.Services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.alpha.devster.backkk.BASE;
import com.alpha.devster.backkk.JSController;
import com.alpha.devster.backkk.MainActivity;
import com.alpha.devster.backkk.R;
import com.alpha.devster.backkk.myWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import static com.alpha.devster.backkk.Backk.CHANNELID;

public class GhostModeService1 extends Service implements View.OnClickListener {

    private static final String TAG=GhostModeService1.class.getName();
    private static int noItemsInPlaylist, currVideoIndex;
    private static GhostModeService1 mInstance=null;

    //----------------------------------------------------------------------------------
    private static WindowManager windowManager;
    private static WindowManager.LayoutParams headParams;
    private LinearLayout ghostHead,ghostViewlayout;
    private FrameLayout ghostPlayerFrame;
    private ImageView ghostheadImage;
    //----------------------------------------------------------------------------------

    private static String VID_URL="";
    private static String VID_ID="";
    private static String VID_TITLE="";
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static RemoteViews notifbar;
    private int LAYOUT_FLAG;
    //-----------------------------------------------------------------------------------
    private static myWebView myoutubewv;
    private static boolean nextVid = false;
    private static boolean isVideoPlaying = true;
    //Replay Video if it's ended
    private static boolean replayVid = false;


    public GhostModeService1(){ }
    public static boolean isServiceCreated(){
        return mInstance!=null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"***ghostservice-> onCreate()");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"***ghostservice-> onStartCommand()");
        if(mInstance==null)
            mInstance=this;
        if(intent.getAction()!=null){
            if (intent.getAction().equals(BASE.CONSTANTS.ACTION.STARTFOREGROUND_WEB_ACTION)){
                ghostModeOn(intent);
            }
            else if(intent.getAction().equals(BASE.CONSTANTS.ACTION.STOPFOREGROUND_WEB_ACTION)){
                Log.i(TAG, "***classghost -> trying to stop service");
                stopForeground(true);
                stopSelf();
                stopService(new Intent(this, GhostModeService1.class));
            } else if(intent.getAction().equals(BASE.CONSTANTS.ACTION.PAUSE_PLAY_ACTION)){
                if (isVideoPlaying) {
                    if (replayVid) {
                        Log.i(TAG, "*** Replay Video");
                        myoutubewv.loadScript(JSController.playVideoScript());
                        replayVid = false;

                    } else {
                        Log.i(TAG , "***Pause Video");
                        myoutubewv.loadScript(JSController.pauseVideoScript());
                    }
                } else {
                    Log.i(TAG, "***Play Video");
                    myoutubewv.loadScript(JSController.playVideoScript());
                }
            }
            else if(intent.getAction().equals(BASE.CONSTANTS.ACTION.NEXT_ACTION)){
                Log.d(TAG, "***Tryinh to Play Next");
                myoutubewv.loadScript(JSController.nextVideo());
                nextVid = true;

            }
            else if(intent.getAction().equals(BASE.CONSTANTS.ACTION.PREV_ACTION)){
                Log.d(TAG, "***Trying to Play Previous");
                myoutubewv.loadScript(JSController.prevVideo());
                nextVid = true;

            }

        }

        return START_NOT_STICKY;
    }

    private void ghostModeOn(Intent intent) {
        Bundle b=intent.getExtras();
        if(b!=null){
            Log.d(TAG,"***classghost "+" bundle not null");
            GhostModeService1.VID_URL=b.getString("VID_URL");
            GhostModeService1.VID_ID=b.getString("VID_ID");
        }
        else
            Log.d(TAG,"***classghost "+" bundle null");
        foregroundNotification();

        //startForeground(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE,BASE.CONSTANTS.notification);

        //View
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //Initializing Windows Params
        initParams();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        ghostHead =(LinearLayout) inflater.inflate(R.layout.ghost_head,null);
        ghostheadImage=(ImageView) ghostHead.findViewById(R.id.img_ghost_head);
        headParams.gravity = Gravity.BOTTOM | Gravity.END;
        headParams.x = 0;
        headParams.y = 0;
        windowManager.addView(ghostHead, headParams);

        //Player View
        ghostViewlayout = (LinearLayout) inflater.inflate(R.layout.layout_ghostmode, null, false);
        ghostPlayerFrame = (FrameLayout) ghostViewlayout.findViewById(R.id.ghost_player_frame);

        myoutubewv=new myWebView(this);
        BASE.CONSTANTS.STRINGS.VID_URL=GhostModeService1.VID_ID;


        ghostPlayerFrame.addView(myoutubewv);
        myoutubewv.loadScript(VID_URL);

        //handeling GhostHeadImage Click
        ghostheadImage.setOnClickListener(this);

    }


    public static void startNoti(){
    }

    @TargetApi(28)
    protected void foregroundNotification() {
        notifbar = new RemoteViews(
                this.getPackageName(),
                R.layout.layout_notification);
        notification = new NotificationCompat.Builder(this,CHANNELID)
                .setSmallIcon(R.mipmap.logo_app1)
                .setContentIntent(notificationIntent())
                .setCustomContentView(notifbar)
                .setCustomBigContentView(notifbar)
                .setBadgeIconType(R.mipmap.logo_app1)
                .setPriority(Notification.PRIORITY_MAX)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(false)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        setTitle(VID_ID);

        //Intent to do things
        Intent doThings = new Intent(this, GhostModeService1.class);

        //stop Service using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.stop_ghostmode,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.STOPFOREGROUND_WEB_ACTION), 0));

        //Pause, Play Video using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.pause_play_video,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.PAUSE_PLAY_ACTION) , 0));

        //Next Video using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.next_video,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.NEXT_ACTION) , 0));

        //Previous Video using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.previous_video,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.PREV_ACTION), 0));

        notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);
        startForeground(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);
    }
    private PendingIntent notificationIntent() {
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }
    private void initParams() {
        //Service Head Params
        headParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
    }

    public static void startVID(String vurl,String vId) {
        Log.d(TAG,"***classghost -> Already service running, Starting Video");
        GhostModeService1.VID_URL = vurl;
        GhostModeService1.VID_ID=vId;
        if(vurl != null && vId!=null) {
            setTitle(vId);
            myoutubewv.loadUrl(vurl);
        }
    }

    public static void setTitle(String vId) {
        try {

            String details = new LoadDetailsTask(
                    "https://youtube.com/get_video_info?video_id=" + VID_ID + "&format=json")
                    .execute().get();
            JSONObject detailsJson = new JSONObject(details);
            VID_TITLE = detailsJson.getString("title");
            Log.d(TAG,"***classghost-> Video Title "+VID_TITLE);
            notifbar.setTextViewText(R.id.title, VID_TITLE);
            notificationManager.notify(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void addStateChangeListener() {
        myoutubewv.loadScript(JSController.onPlayerStateChangeListener());
    }
    public static void setCurrVideoIndex(int currVideoIndex) {
        GhostModeService1.currVideoIndex = currVideoIndex;
    }
    //Update Image of Repeat Type Button
//    private void updateRepeatTypeImage() {
//        if(BASE.CONSTANTS.repeatType == 0){
//            repeatTypeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_none));
//        }
//        else if(BASE.CONSTANTS.repeatType == 1){
//            repeatTypeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat));
//        }
//        else if(BASE.CONSTANTS.repeatType == 2){
//            repeatTypeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
//        }
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_ghost_head:

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"***ghostservice-> onDestroy()");
        mInstance=null;
        stopSelf();
    }
}
