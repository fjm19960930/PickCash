package com.example.pickcash.main.mine.record;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.pickcash.R;
import com.zcolin.frame.app.BaseFrameActivity;

public class LoanItemDetailActivity extends BaseFrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_item_detail);

        String link = getIntent().getStringExtra("link");
        WebView newsWeb = findViewById(R.id.loan_item_detail_web);
        newsWeb.setWebViewClient(new WebViewClient());
        newsWeb.loadUrl(link);//TODO load的网址地址
    }
}