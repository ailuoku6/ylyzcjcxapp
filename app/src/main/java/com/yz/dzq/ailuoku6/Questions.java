package com.yz.dzq.ailuoku6;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Questions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        WebView webView = (WebView) findViewById(R.id.wv1);
        WebSettings webS = webView.getSettings();
        webS.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
    }
}
