package com.alpha.devster.backkk;


import android.app.Notification;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import com.alpha.devster.backkk.Services.BackkService;
import com.alpha.devster.backkk.Services.GhostModeService1;
import com.alpha.devster.backkk.Services.MediaPlayerServices;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class BASE {


    static boolean isPermission(Context context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context))
                return true;
            else
                return false;
        }else
            return true;
    }

    static boolean isServiceRunning() {
        return BackkService.isServiceCreated();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo!=null && netInfo.isConnected());
    }


     public static class CONSTANTS{
        // Request for Overlay over other apps
        final static int OVERLAY_PERMISSION_REQ_BACKTO_ACT = 2345;
        final static Class<MediaPlayerServices> playerServiceClass=MediaPlayerServices.class;
        final static String URL="https://www.youtube.com/";
         public static final String CHANNELID="Backk";
        //Type of player
        //myWebView player = 0
        static  int playerType = 0;

        //Repeat
        //if repeatType = 0  --> no repeatType
        //if repeatType = 1  --> repeatType complete
        //if repeatType = 2  --> repeatType single
        public static  int repeatType = 0;
        public static  int noOfRepeats = 0;

        //Finish service on end video
        static boolean finishOnEnd = false;


        //Actions
        public interface ACTION {
            String EXPAND_MODE = "com.alpha.devster.expandmode";
            String PREV_ACTION = "com.alpha.devster.action.prev";
            String PAUSE_PLAY_ACTION = "com.alpha.devster.action.play";
            String NEXT_ACTION = "com.alpha.devster.action.next";
            String STARTFOREGROUND_WEB_ACTION = "com.alpha.devster.playingweb";
            String STOPFOREGROUND_WEB_ACTION = "com.alpha.devster.stopplayingweb";

        }

        //Notification Id
        public interface NOTIFICATION_ID {
             int FOREGROUND_SERVICE = 1;
        }
        public static Notification notification;

        public static class STRINGS{
            public static String VID_URL = "";
            public static void setVid(String vid_url) { STRINGS.VID_URL = vid_url; }
            public static String getVideoHTML() {
                return  "<!DOCTYPE HTML>\n" +
                        "<html>\n" +
                        "  <head>\n" +
                        "    <script src=\"https://www.youtube.com/iframe_api\"></script>\n" +
                        "    <style type=\"text/css\">\n" +
                        "        html, body {\n" +
                        "            margin: 0px;\n" +
                        "            padding: 0px;\n" +
                        "            border: 0px;\n" +
                        "            width: 100%;\n" +
                        "            height: 100%;\n" +
                        "        }\n" +
                        "    </style>" +
                        "  </head>\n" +
                        "\n" +
                        "  <body>\n" +
                        "    <iframe style=\"display: block;\" id=\"player\" frameborder=\"0\"  width=\"100%\" height=\"100%\" " +
                        "       src=\"blob:https://www.youtube.com/watch?=" + VID_URL  +
                        "?enablejsapi=1&autoplay=1&iv_load_policy=3&fs=0&rel=0\">" +
                        "    </iframe>\n" +
                        "    <script type=\"text/javascript\">\n" +
                        "      var tag = document.createElement('script');\n" +
                        "      tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                        "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                        "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                        "      var player;\n" +
                        "      function onYouTubeIframeAPIReady() {\n" +
                        "          player = new YT.Player('player', {\n" +
                        "              events: {\n" +
                        "                  'onReady': onPlayerReady\n" +
                        "              }\n" +
                        "          });\n" +
                        "      }\n" +
                        "      function onPlayerReady(event) {\n" +
                        "          player.setPlaybackQuality(\"" + 2 + "\");\n" +
                        "      }\n" +
                        "    </script>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>";
            }


        }

    }


}
