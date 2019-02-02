package com.alpha.devster.backkk;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GetPermission extends AppCompatActivity {

    private static final String TAG=GetPermission.class.getName();
    Button btn_getPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_permission);
        Log.d(TAG,"---------------------------------------------------------");
        Log.d(TAG,"***GetPermission-> onCreate()");

        btn_getPermission=findViewById(R.id.btn_get_permission);
        btn_getPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"***GetPermission-> AskingPermission for code "+BASE.CONSTANTS.OVERLAY_PERMISSION_REQ_BACKTO_ACT);
                askPermission();
            }
        });
    }

    private void askPermission() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, BASE.CONSTANTS.OVERLAY_PERMISSION_REQ_BACKTO_ACT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"***GetPermission-> onActivityResult() + resultCode "+requestCode);
        if(requestCode==BASE.CONSTANTS.OVERLAY_PERMISSION_REQ_BACKTO_ACT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            if(!Settings.canDrawOverlays(this)){
                Log.d(TAG,"***GetPermission-> Not granted, show dialog");
                needPermissionDialog(requestCode);
            }
            else{
                Log.d(TAG,"***GetPermission-> Permission Granted()");
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    @TargetApi(23)
    private void needPermissionDialog(int resultCode) {

        if(resultCode==BASE.CONSTANTS.OVERLAY_PERMISSION_REQ_BACKTO_ACT){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to grant the permission.");
            builder.setPositiveButton("OK",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(i, BASE.CONSTANTS.OVERLAY_PERMISSION_REQ_BACKTO_ACT);
                        }
                    });
            builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });

            builder.setCancelable(false);
            builder.show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"***GetPermission-> onDestroy()");
    }
}
