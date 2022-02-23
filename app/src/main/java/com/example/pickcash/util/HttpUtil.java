package com.example.pickcash.util;

import android.os.Build;

import com.example.pickcash.PickCashApplication;
import com.zcolin.frame.http.ZHttp;
import com.zcolin.frame.http.response.ZResponse;
import com.zcolin.frame.util.GsonUtil;
import com.zcolin.frame.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;

public class HttpUtil {
    public static final String TAG = "PICK_CASH:";
    public static final String BASE_URL = "https://web.tokily.cn:8084";
//    public static final String BASE_URL = "https://api.cash168.in";
    public static final String LOG_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=2f61eec0-5782-41ff-8986-0bc7303058ee";
    public static final String TM = "1";
    public static final String CHANNEL = "1";
    public static final String ACCEPT_LANGUAGE = "en-US";

    public static synchronized void reportLog(String log) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        Date date = new Date(System.currentTimeMillis());
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        final String desc = "************* Log Head ****************" +
                "\nApp PackageName      : " + PickCashApplication.APP_CONTEXT.getPackageName() +
                "\nMobile NO           : " + PickCashApplication.mPhoneNum +
                "\nTime Of Crash      : " + simpleDateFormat.format(date) +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +
                "\nDevice Model       : " + Build.MODEL +
                "\nAndroid Version    : " + Build.VERSION.RELEASE +
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                "\nApp VersionName    : " + PickCashApplication.mVersion +
                "\nApp VersionCode    : " + PickCashApplication.mVersion +
                "\n************* Log Head ****************\n\n" +
                log;
        try {
            jsonObject1.put("content", desc);
            jsonObject.put("markdown", jsonObject1);
            jsonObject.put("msgtype", "markdown");
            ZHttp.postString(LOG_URL, GsonUtil.beanToString(jsonObject), "text/x-markdown; charset=utf-8", new ZResponse<BaseLogReply>() {
                @Override
                public void onSuccess(Response response, BaseLogReply resObj) {
                    LogUtil.d(TAG, "reportLog" + response.code());
                }

                @Override
                public void onError(int code, String error) {
                    LogUtil.e(TAG, "reportLog " + code + " " + error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
