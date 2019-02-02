package com.alpha.devster.backkk;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.alpha.devster.backkk.Services.BackkService;
import com.alpha.devster.backkk.Services.GhostModeService1;

/**
 * Created by shyam on 18/2/16.
 */
class JavaScriptInterface {
    private Context context;
    private static Handler handlerForJavascriptInterface = new Handler();

    public JavaScriptInterface(BackkService backkService) {
        Log.d("***","Interface Set");
        this.context = backkService;
    }

    @JavascriptInterface
    public void showPlayerState (final int status) {
        Log.d("Player Status ", String.valueOf(status));
        handlerForJavascriptInterface.post(new Runnable() {
            @Override
            public void run() {
                BackkService.setPlayingStatus(status);
            }
        });
    }
    @JavascriptInterface
    public void showVID (final String vId) {
        Log.d("New Video Id ", vId);
        handlerForJavascriptInterface.post(new Runnable() {
            @Override
            public void run() {
                BackkService.setTitleAuthuImage(vId);
            }
        });
    }

    @JavascriptInterface
    public void currVidIndex (final int index) {
        Log.d("Current Video Index ", String.valueOf(index));
        BackkService.setCurrVideoIndex(index);
    }

}