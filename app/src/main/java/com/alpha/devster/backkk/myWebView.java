package com.alpha.devster.backkk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alpha.devster.backkk.Services.BackkService;
import com.alpha.devster.backkk.Services.GhostModeService1;

/**
 * Created by shyam on 15/3/16.
 */
public class myWebView extends WebView{

    private Context context;

    public myWebView(Context ctxt) {
        super(ctxt);
        context = ctxt;
        initView();
    }

    public myWebView(Context ctxt, AttributeSet attrs){
        super(ctxt,attrs);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initView() {

        this.getSettings().setJavaScriptEnabled(true);
        this.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        //myoutube.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:21.0.0) Gecko/20121011 Firefox/21.0.0");

        //----------------------------To get Player Id-------------------------------------------

        this.addJavascriptInterface(new JavaScriptInterface((BackkService) context), "Interface");
        this.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                                        Log.d("***Ghost Webview","URl LOad");
                                        return false;
                                    }

                                    @Override
                                    public void onPageFinished(android.webkit.WebView view, String url) {
                                        Log.d("***Ghost Webview","URl LOad finished");
                                        GhostModeService1.addStateChangeListener();
                                    }
                                }
        );
    }

    public void loadScript(String s) {
        this.loadUrl(s);
    }


    public void destroyWV() {
        this.destroy();
    }

    public void loadDataWithUrl(String baseUrl, String videoHTML, String mimeType, String encoding, String historyUrl) {
        this.loadDataWithBaseURL(baseUrl, videoHTML, mimeType, encoding, historyUrl);
    }
}