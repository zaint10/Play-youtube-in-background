package com.alpha.devster.backkk.Services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alpha.devster.backkk.BASE;
import com.alpha.devster.backkk.Callbacks.UpdateUI;
import com.alpha.devster.backkk.JSController;
import com.alpha.devster.backkk.MainActivity;
import com.alpha.devster.backkk.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class BackkService extends Service {
    private static final String TAG = BackkService.class.getName();
    private static BackkService mInstance = null;
    //---------------------------------------------------------------------------------------
    private static String currUrl = "";
    private static int currVideoIndex;
    private static String VID_ID = "";
    private static String VID_TITLE = "";
    private static String VID_AUTHUR = "";
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static RemoteViews notifbar;
    //-----------------------------------------------------------------------------------------
    private static boolean nextVid = false;
    private static boolean isVideoPlaying = true;
    //Replay Video if it's ended
    private static boolean replayVid = false;


    public BackkService() { }

    public static boolean isServiceCreated() {
        return mInstance != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "*** Service Is created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mInstance == null)
            mInstance = this;
        else
            Log.d(TAG, "*** Service Is Already Running");

        final String action = intent.getAction();
        if (action != null) {
            UpdateUI ui = new UpdateUI();
            switch (action) {
                case BASE.CONSTANTS.ACTION.STARTFOREGROUND_WEB_ACTION:
                    Log.d(TAG, "*** Action to start the service");
                    startNotification();
                    break;
                case BASE.CONSTANTS.ACTION.STOPFOREGROUND_WEB_ACTION:
                    Log.d(TAG, "*** Action to stop the service");
                    mInstance = null;
                    stopForeground(true);
                    stopSelf();
                    stopService(new Intent(this, BackkService.class));
                    ui.ExitApp(true);
                    EventBus.getDefault().post(ui);
                    break;

                case BASE.CONSTANTS.ACTION.PAUSE_PLAY_ACTION:
                    Log.d(TAG, "*** Action for pause/play/replay action");
                    if (isVideoPlaying) {
                        if (replayVid) {
                            Log.d(TAG, "*** Replay Video");
                            replayVid = false;
                            ui.replay(true);
                            EventBus.getDefault().post(ui);

                        } else {

                            ui.pause(true);
                            EventBus.getDefault().post(ui);

                        }
                    } else {
                        Log.d(TAG, "***Play Video");
                        ui.play(true);
                        EventBus.getDefault().post(ui);

                    }
                    break;


            }
        }


        return START_NOT_STICKY;
    }

    @TargetApi(28)
    private void startNotification() {
        notifbar = new RemoteViews(
                this.getPackageName(),
                R.layout.layout_notification);
        notification = new NotificationCompat.Builder(this, BASE.CONSTANTS.CHANNELID)
                .setSmallIcon(R.mipmap.logo_app1)
                .setCustomContentView(notifbar)
                .setCustomBigContentView(notifbar)
                .setBadgeIconType(R.mipmap.logo_app1)
                .setPriority(Notification.PRIORITY_MAX)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(false)
                .build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        BASE.CONSTANTS.notification = notification;


        //Intent to do things
        Intent doThings = new Intent(this, BackkService.class);

        //stop Service using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.stop_ghostmode,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.STOPFOREGROUND_WEB_ACTION), 0));

        //Pause, Play Video using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.pause_play_video,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.PAUSE_PLAY_ACTION), 0));

        //Next Video using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.next_video,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.NEXT_ACTION), 0));

        //Previous Video using doThings Intent
        notifbar.setOnClickPendingIntent(R.id.previous_video,
                PendingIntent.getService(getApplicationContext(), 0,
                        doThings.setAction(BASE.CONSTANTS.ACTION.PREV_ACTION), 0));

        //Expand Mode using doThings Intent
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifbar.setOnClickPendingIntent(R.id.expand_mode,
                PendingIntent.getActivity(getApplicationContext(), 0,
                        intent.setAction(BASE.CONSTANTS.ACTION.EXPAND_MODE), PendingIntent.FLAG_UPDATE_CURRENT));

        notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        startForeground(1, notification);
    }

    public static void setTitleAuthuImage(String vId) {
        try {

            String details = new LoadDetailsTask(
                    "https://youtube.com/get_video_info?video_id=" + VID_ID + "&format=json")
                    .execute().get();
            JSONObject detailsJson = new JSONObject(details);
            VID_TITLE = detailsJson.getString("title");
            VID_AUTHUR = detailsJson.getString("authur");
            Log.d(TAG, "***Video Title " + VID_TITLE + " " + "VID Authur +" + VID_AUTHUR);
            notifbar.setTextViewText(R.id.title, VID_TITLE);
            notifbar.setTextViewText(R.id.authur, VID_AUTHUR);
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

    public static void setCurrVideoIndex(int currVideIndex) {
        BackkService.currVideoIndex = currVideIndex;
    }

    public static void setPlayingStatus(int playingStatus) {
        if (playingStatus == -1) {
            nextVid = true;
        }
        if (playingStatus == 1) {
            isVideoPlaying = true;
            notifbar.setImageViewResource(R.id.pause_play_video, R.mipmap.icon_pause);
            notificationManager.notify(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
            if (nextVid) {
                nextVid = false;
                //loadScript(JavaScript.getVidUpdateNotiContent());
            }


        } else if (playingStatus == 2) {
            isVideoPlaying = false;
            notifbar.setImageViewResource(R.id.pause_play_video, R.mipmap.icon_play);

            notificationManager.notify(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        } else if (playingStatus == 0) {
            Log.d(TAG, "*** Repeat type" + BASE.CONSTANTS.repeatType + "");

            //webPlayer.loadScript(JavaScript.prevVideo());

            replayVid = true;
            //notifbar.setImageViewResource(R.id.pause_play_video, R.drawable.ic_replay);

            notificationManager.notify(BASE.CONSTANTS.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "***Service is destroyed.");
        isVideoPlaying=true;
        mInstance = null;
        stopForeground(true);
        stopSelf();
        stopService(new Intent(this, BackkService.class));
    }


}
