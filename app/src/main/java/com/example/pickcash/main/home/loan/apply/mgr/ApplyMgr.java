package com.example.pickcash.main.home.loan.apply.mgr;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.main.home.loan.verify.mgr.VerifyMgr;
import com.example.pickcash.main.home.loan.verify.mgr.entity.PersonalInfoFieldsReply;
import com.example.pickcash.util.BaseReply;
import com.example.pickcash.util.BaseResponse;
import com.example.pickcash.util.HttpUtil;
import com.zcolin.frame.http.ZHttp;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Response;

public class ApplyMgr {
    public static void getLoanApplyData(GetLoanApplyListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/preview", headParams, params, new BaseResponse<ApplyReply>() {
            @Override
            public void onSuccess(Response response, ApplyReply resObj) {
                if (listener != null) {
                    listener.onSuccess(resObj.data);
                }
            }

            @Override
            public void onError(int code, String error) {
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void loanApply(LoanApplyListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/apply", headParams, params, new BaseResponse<BaseReply>() {
            @Override
            public void onSuccess(Response response, BaseReply resObj) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(int code, String error) {
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public interface GetLoanApplyListener{
        void onSuccess(ApplyReply.ApplyData applyData);

        void onError(int code, String errorMsg);
    }

    public interface LoanApplyListener{
        void onSuccess();

        void onError(int code, String errorMsg);
    }
}
