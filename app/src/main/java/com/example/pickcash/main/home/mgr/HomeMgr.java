package com.example.pickcash.main.home.mgr;

import android.app.Activity;
import android.widget.Toast;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.main.home.mgr.entity.LoanStateReply;
import com.example.pickcash.main.home.mgr.entity.SdkKeyReply;
import com.example.pickcash.main.home.mgr.entity.SubmitStateReply;
import com.example.pickcash.main.mine.mgr.entity.RecordReply;
import com.example.pickcash.util.BaseReply;
import com.example.pickcash.util.BaseResponse;
import com.example.pickcash.util.HttpUtil;
import com.example.pickcash.util.NumberUtils;
import com.example.pickcash.util.SystemUtils;
import com.zcolin.frame.http.ZHttp;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.Market;
import ai.advance.sdk.quality.lib.GuardianImageQualitySDK;
import okhttp3.Response;

public class HomeMgr {

    public static void getLoanState(Activity activity, GetLoanStateListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/states", headParams, params, new BaseResponse<LoanStateReply>(activity) {
            @Override
            public void onSuccess(Response response, LoanStateReply resObj) {
                if (listener != null) {
                    listener.onSuccess(resObj.data);
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void getOrderList(GetOrderListListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/orderlist", headParams, params, new BaseResponse<RecordReply>() {
            @Override
            public void onSuccess(Response response, RecordReply resObj) {
                if (resObj.data.list != null && !resObj.data.list.isEmpty() && listener != null) {
                    for (int i = 0; i < resObj.data.list.size(); i++) {
                        if (resObj.data.list.get(i).orderStatus.equals("INWAIT")) {
                            listener.onSuccess(false, NumberUtils.dateFormat(resObj.data.list.get(i).expireDate),
                                    NumberUtils.moneyFormat(resObj.data.list.get(i).loanAmount));
                            return;
                        }
                    }
                }
                if (listener != null) {
                    listener.onSuccess(true, "", "");
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void getSdkKey() {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/getsdkkey", headParams, params, new BaseResponse<SdkKeyReply>() {
            @Override
            public void onSuccess(Response response, SdkKeyReply resObj) {
                if (resObj.data.keys != null && resObj.data.keys.size() > 0) {
                    GuardianLivenessDetectionSDK.init(PickCashApplication.mApplication,
                            resObj.data.keys.get(0) == null ? "" : resObj.data.keys.get(0),
                            resObj.data.keys.get(1) == null ? "" : resObj.data.keys.get(1),
                            Market.India);
                    GuardianImageQualitySDK.init(PickCashApplication.mApplication,
                            resObj.data.keys.get(0) == null ? "" : resObj.data.keys.get(0),
                            resObj.data.keys.get(1) == null ? "" : resObj.data.keys.get(1),
                            Market.India);
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
            }
        });
    }

    public static void submitData(String type, File file) {
        if (file == null) {
            return;
        }
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", file);
        ZHttp.uploadFileWithHeader(HttpUtil.BASE_URL + "/app/uprisk", headParams, params, fileParams, new BaseResponse<BaseReply>() {
            @Override
            public void onSuccess(Response response, BaseReply resObj) {

            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
            }
        });

    }

    public static void getSubmitState(Activity activity) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/getrisk", headParams, params, new BaseResponse<SubmitStateReply>() {
            @Override
            public void onSuccess(Response response, SubmitStateReply resObj) {
                Runnable runnable = () -> {
                    if (resObj.data.msm == 0) {
                        HomeMgr.submitData("1", SystemUtils.getSmsMessage(activity));
                    }
                    if (resObj.data.txl == 0) {
                        HomeMgr.submitData("2", SystemUtils.getContact(activity));
                    }
                    if (resObj.data.app == 0) {
                        HomeMgr.submitData("3", SystemUtils.getApp(activity));
                    }
                    if (resObj.data.alb == 0) {
                        HomeMgr.submitData("4", SystemUtils.getImage(activity));
                    }
                };
                ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
                singleThreadExecutor.execute(runnable);
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
            }
        });
    }

    public interface GetOrderListListener {
        void onSuccess(boolean isCanLoan, String loanDate, String loanMoney);

        void onError(int code, String errorMsg);
    }

    public interface GetLoanStateListener {
        void onSuccess(String state);

        void onError(int code, String errorMsg);
    }
}
