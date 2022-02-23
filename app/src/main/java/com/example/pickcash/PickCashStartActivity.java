package com.example.pickcash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.pickcash.login.PickCashLoginActivity;
import com.example.pickcash.main.PickCashMainActivity;
import com.example.pickcash.util.HttpUtil;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.frame.util.SpUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 首页
 */
public class PickCashStartActivity extends BaseFrameActivity {

    private ExecutorService executorService;//线程池对象
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean hasLogin = SpUtil.getBoolean("hasLogin", false);
            if (msg.what == 0) {
                if (hasLogin) {
                    startActivity(new Intent(mActivity, PickCashMainActivity.class));
                    PickCashApplication.mToken = SpUtil.getString("token", "");
                    PickCashApplication.mPhoneNum = SpUtil.getString("phoneNum", "");
                } else {
                    startActivity(new Intent(mActivity, PickCashLoginActivity.class));
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_cash_start);
        findViewById(R.id.start_activity_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0);
                finish();
            }
        });
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorService = new ThreadPoolExecutor(5, 5, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(0);// 通知主线程
                    finish();
                } catch (InterruptedException e) {
                    HttpUtil.reportLog("PickCashStartActivity:" + e.toString());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }
}