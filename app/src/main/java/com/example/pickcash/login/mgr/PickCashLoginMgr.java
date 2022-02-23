package com.example.pickcash.login.mgr;

import android.app.Activity;
import android.util.Log;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.util.HttpUtil;
import com.example.pickcash.util.NumberUtils;
import com.zcolin.frame.http.ZHttp;
import com.zcolin.frame.http.response.ZResponse;
import com.zcolin.frame.util.SpUtil;
import com.example.pickcash.login.mgr.entity.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Response;

public class PickCashLoginMgr {
    public static void getVoiceSupportState(GetVoiceSupportStateListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/sms/type", headParams, params, new ZResponse<VoiceSupportStateReply>() {
            @Override
            public void onSuccess(Response response, VoiceSupportStateReply resObj) {
                if (listener != null) {
                    listener.onSuccess(resObj.data);
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("getVoiceSupportState:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void getVerifyCode(String phoneNum, int type, GetVerifyCodeListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        String params = "{\n" +
                "\"phone\": \"" + phoneNum + "\",\n" +
                "\"type\": \"" + type + "\",\n" +
                "\"channelCode\": \"1\"\n" +
                "}";
        ZHttp.postJsonWithHeader(HttpUtil.BASE_URL + "/app/sms/getverifycode", headParams, params, new ZResponse<VerifyCodeReply>() {
            @Override
            public void onSuccess(Response response, VerifyCodeReply resObj) {
                if (resObj.code == 200) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("getVerifyCode:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void login(Activity activity, String phoneNum, String verifyCode, LoginListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        String params = "{\n" +
                "\"phone\": \"" + phoneNum + "\",\n" +
                "\"code\": \"" + verifyCode + "\",\n" +
                "\"imeis\": \"" + NumberUtils.getIMEI(activity) + "\"\n" +
                "}";
        ZHttp.postJsonWithHeader(HttpUtil.BASE_URL + "/app/login", headParams, params, new ZResponse<LoginReply>(activity) {
            @Override
            public void onSuccess(Response response, LoginReply resObj) {
                Log.d("fjm", "onSuccess: " + resObj.data.token);
                SpUtil.putBoolean("hasLogin", true);
                SpUtil.putString("token", resObj.data.token);
                SpUtil.putString("phoneNum", phoneNum);
                PickCashApplication.mToken = resObj.data.token;
                PickCashApplication.mPhoneNum = phoneNum;
                if (listener != null) {
                    if (resObj.data.registerApp == null || resObj.data.registerApp.isEmpty()) {
                        listener.onSuccess(false);
                    } else {
                        listener.onSuccess(true);
                    }
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("login:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });

    }

    public interface GetVoiceSupportStateListener {
        void onSuccess(boolean state);

        void onError(int code, String errorMsg);
    }

    public interface GetVerifyCodeListener {
        void onSuccess();

        void onError(int code, String errorMsg);
    }

    public interface LoginListener {
        void onSuccess(boolean isNeedShowDialog);

        void onError(int code, String errorMsg);
    }
}
