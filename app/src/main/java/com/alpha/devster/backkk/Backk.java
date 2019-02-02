package com.alpha.devster.backkk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RemoteViews;

import com.alpha.devster.backkk.Services.GhostModeService1;

public class Backk extends AppCompatActivity {

    private static final String TAG=Backk.class.getName();
    public static final String CHANNELID="Backk";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.FileName), Context.MODE_PRIVATE);
        Log.d(TAG,"***---------------------------------------------------------");
        Log.d(TAG,"***backkk-> onCreate()");
        createNotificationChannel();
        SharedPreferences.Editor editor=sharedPref.edit();
        if(!sharedPref.contains(getString(R.string.init))){
            Log.d(TAG,"***backkk-> sharedPreference Initializing....");


            //Setting sharedPreference as initialized
            editor.putBoolean(getString(R.string.init),true);
            editor.putBoolean(getString(R.string.firsttime),false);

            //Repeat
            //if repeatType = 0  --> no repeatType
            //if repeatType = 1  --> repeatType complete
            //if repeatType = 2  --> repeatType single
            editor.putInt(getString(R.string.repeat_type), 0);
            editor.putInt(getString(R.string.no_of_repeats), 5);

            //Type of player
            //myWebView player = 0
            editor.putInt(getString(R.string.player_type), 0);

            editor.apply();

        }




        if (BASE.isServiceRunning()) {
            Log.d("***backkk : ", "Service & App Already Running! ExpandMode");
            startActivity(new Intent(Backk.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
            return;
        }

        if (BASE.isPermission(this)) {
            Log.d(TAG,"***Permission Already granted, starting MainActivity");
            finish();
            startActivity(new Intent(this, MainActivity.class));

        } else {
            Log.d(TAG,"***Starting GetPermission Activity");
            Intent i = new Intent(this,
                    GetPermission.class);
            finish();
            startActivity(i);

        }

    }


    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel medialChannel=new NotificationChannel(CHANNELID
            ,"Channel1"
            ,NotificationManager.IMPORTANCE_DEFAULT);
        medialChannel.setDescription("Backk");
        NotificationManager manager=getSystemService(NotificationManager.class);
        manager.createNotificationChannel(medialChannel);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"***classbackk onDestroy()");

    }
}
