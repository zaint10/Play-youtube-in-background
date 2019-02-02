package com.alpha.devster.backkk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import android.widget.Toast;

import com.alpha.devster.backkk.Callbacks.UpdateUI;
import com.alpha.devster.backkk.Services.BackkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.ConnectException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();
    private Activity mainAct;

    private String currUrl;
    private boolean exit = false;

    //-----------------------------------------------------------------------------------
    private WebView myoutube;
    private DrawerLayout mdrawerLayout;
    private ActionBarDrawerToggle mdrawerToggle;

    private SwipeRefreshLayout swipeRefreshLayout;

    //------------------------------------------------------------------------------------
    private int LAYOUT_FLAG;
    private static WindowManager windowManager;
    private DrawerLayout windowMain;
    private static WindowManager.LayoutParams expandParams, ghostParams;
    private static boolean ghostMode = false;
    //--------------------------------------------------------------------------------Lvl 1 Creation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "***ClassMain---------------------------------------------------------");
        Log.d(TAG, "***-> onCreate");
        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mainAct = this;
        currUrl = BASE.CONSTANTS.URL;
        initializeViewgroup();
        windowManager.addView(windowMain, expandParams);
        if (myoutube != null) {
         myoutube.loadUrl(currUrl);
            myoutube.addJavascriptInterface(new JavaScriptInterface(new BackkService()), "Interface");

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "***-> onResume()");
        expandMode();
        this.moveTaskToBack(true);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "***-> onStop()");
        super.onStop();
    }

    //---------------------------------------------------------------------------------Lvl2 Features
    private void expandMode() {
        if (ghostMode && BASE.isServiceRunning())
            windowManager.updateViewLayout(windowMain, expandParams);
        ghostMode = false;
    }

    private void startGhostMode() {
        ghostMode = true;
        windowManager.updateViewLayout(windowMain, ghostParams);

    }


    //------------------------------------------------------------------------------Lvl3 ClickEvents
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_ghostmode:
                Toast.makeText(MainActivity.this, "Ghost Mode", Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    startGhostMode();

                } else if (Settings.canDrawOverlays(this)) {
                    startGhostMode();

                } else {

                    Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_exit:
                myoutube.loadUrl(myoutube.getUrl());
                //exitApp();
                break;
            case R.id.btn:
                if (mdrawerLayout.isDrawerOpen(Gravity.START)) {
                    mdrawerLayout.closeDrawer(Gravity.START);
                }else
                    mdrawerLayout.openDrawer(Gravity.START);
                break;

            case R.id.btn_Retry:
                mainAct.recreate();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.navigation_item_1:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.navigation_item_2:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mdrawerToggle != null)
            mdrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mdrawerToggle != null)
            mdrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mdrawerToggle != null) {
            if (mdrawerToggle.onOptionsItemSelected(item))
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(UpdateUI state) {
        if (state.isExit()) {
            exitApp();
            state.setDefault();
            return;
        }
        if (state.isPause()) {
            Log.d(TAG, "***Pause Video");
            myoutube.loadUrl(JSController.pauseVideoScript());
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getAction()!=null){
            if(intent.getAction().equals(BASE.CONSTANTS.ACTION.EXPAND_MODE))
                Log.d(TAG, "***-> ExpandMode on From notification");
        }
    }

    private void addStateChangeListener() {
        myoutube.loadUrl(JSController.onPlayerStateChangeListener());
    }

    //------------------------------------------------------------------------------------Lvl4 Inits

    private void initParams() {
        expandParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON,
                PixelFormat.TRANSLUCENT);
        expandParams.gravity = Gravity.START | Gravity.TOP;
        expandParams.x = 0;
        expandParams.y = 0;

        ghostParams = new WindowManager.LayoutParams(
                0,
                0,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        ghostParams.gravity = Gravity.START | Gravity.TOP;
        ghostParams.x = 0;
        ghostParams.y = 0;

    }
    private void initializeViewgroup() {
        //Initializing Windows Params
        initParams();
        //Window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        DrawerLayout wrapper=new DrawerLayout(this){
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                    if(event.getAction()==KeyEvent.ACTION_DOWN){
                        Log.d(TAG, "***expandMode-> onBackPressed ");
                        if(exit)
                            exitApp();
                        else{
                            if (mdrawerLayout.isDrawerOpen(Gravity.START))
                                mdrawerLayout.closeDrawer(Gravity.START);
                            else if (!ghostMode && myoutube != null) {
                                if (myoutube.canGoBack()) {
                                    Log.d(TAG, "***webview-> going back");
                                    myoutube.goBack();
                                }
                            }

                        }
                    }
                    return true;
                }
                return super.dispatchKeyEvent(event); }
        };
        windowMain = (DrawerLayout) inflater.inflate(R.layout.activity_main, wrapper);

        initializeView();
    }
    private void initializeView() {

        ImageButton btn_ghost,btn_exit, btn_menuu;

        //-------------------------------------------------------------------------------------
        ViewStub viewStub = windowMain.findViewById(R.id.view_stub);
        viewStub.setLayoutResource(R.layout.content_main_webview);
        viewStub.inflate();
        //------------------------------------------------------------------Views
        myoutube = windowMain.findViewById(R.id.wv_youtube);
        btn_ghost = windowMain.findViewById(R.id.btn_ghostmode);
        btn_exit=windowMain.findViewById(R.id.btn_exit);
        btn_menuu = windowMain.findViewById(R.id.btn);

        //-------------------------------------------------------------NavigationTab
        NavigationView mnavigation = windowMain.findViewById(R.id.navigation_view);
        mdrawerLayout = windowMain.findViewById(R.id.drawer);
        mdrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mdrawerLayout, R.string.app_name, R.string.app_name);


        btn_ghost.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_menuu.setOnClickListener(this);
        mdrawerLayout.addDrawerListener(mdrawerToggle);
        mnavigation.setNavigationItemSelectedListener(this);

        //----------------------------------------------------------------Settings

        settingWebview();
        //Swipe Refresh myWebView
        swipeRefreshLayout = windowMain.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                myoutube.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        myoutube.loadUrl(myoutube.getUrl());
//                    }
//                });
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            myoutube.loadUrl(myoutube.getUrl());

                        }
                    });
                }


        });

        // after initialization
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout swipeRefreshLayout, @Nullable View view) {
                return myoutube.getScrollY() > 0;
            }
        });


        if (!BASE.isServiceRunning()) {
            final Intent intent = new Intent(MainActivity.this, BackkService.class);
            intent.setAction(BASE.CONSTANTS.ACTION.STARTFOREGROUND_WEB_ACTION);
            startService(intent);
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void settingWebview() {

        myoutube.getSettings().setLoadsImagesAutomatically(true);
        myoutube.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myoutube.getSettings().setBuiltInZoomControls(false);
        myoutube.getSettings().setLoadsImagesAutomatically(true);
        myoutube.getSettings().setMediaPlaybackRequiresUserGesture(true);
        myoutube.getSettings().setJavaScriptEnabled(true);
        myoutube.setWebViewClient(new Myyoutube());
    }

    //--------------------------------------------------------------------------------Lvl5 Destroy

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "*** onDestroy()");
        ghostMode = false;
    }

    private void exitApp() {
        Log.d(TAG, "*** Exit from App.");
        windowManager.removeView(windowMain);
        if(BASE.isServiceRunning())
            stopService(new Intent(MainActivity.this,BackkService.class));
        finish();
    }

    //---------------------------------------------------------------------------Lvl6 Infernal Hell
    private class Myyoutube extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "***classmain-> webview refreshing "+url);

                swipeRefreshLayout.setRefreshing(true);
                exit=false;
                currUrl = url;


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "***classmain-> webview is refreshed "+url);
            swipeRefreshLayout.setRefreshing(false);
            currUrl = url;
            addStateChangeListener();

        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            Log.d(TAG,"*** ErrorLoading "+description+"     ErrorCode "+errorCode);
            swipeRefreshLayout.setRefreshing(false);
            exit=true;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @TargetApi(21)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

            if (String.valueOf(request.getUrl()).contains("http://m.youtube.com/watch?") ||
                    String.valueOf(request.getUrl()).contains("https://m.youtube.com/watch?")) {
                currUrl = String.valueOf(request.getUrl());

                //Video Id
                String VID_ID = currUrl.substring(currUrl.indexOf("?v=") + 3, currUrl.indexOf('&'));
                Log.d(TAG, "***classmain-> URL is " + currUrl);
                Log.d(TAG, "***classmain-> VID is " + VID_ID);

            }
            return super.shouldInterceptRequest(view, request);
        }
    }

}
