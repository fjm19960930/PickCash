package com.example.pickcash.main.mine.mgr;

import android.app.Activity;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.main.mine.mgr.entity.RecordReply;
import com.example.pickcash.main.mine.mgr.entity.RepayLinkReply;
import com.example.pickcash.main.mine.record.LoanRecordEntity;
import com.example.pickcash.util.BaseReply;
import com.example.pickcash.util.BaseResponse;
import com.example.pickcash.util.HttpUtil;
import com.example.pickcash.util.NumberUtils;
import com.zcolin.frame.http.ZHttp;
import com.zcolin.frame.util.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Response;

public class MineMgr {
    public static void getLoanRecord(Activity activity, GetLoanRecordListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/orderlist", headParams, params, new BaseResponse<RecordReply>(activity) {
            @Override
            public void onSuccess(Response response, RecordReply resObj) {
                ArrayList<LoanRecordEntity> recordData = new ArrayList<>();
                if (resObj.data.list != null && !resObj.data.list.isEmpty()) {
                    for (int i = 0; i < resObj.data.list.size(); i++) {
                        String date = "";
                        String state = "";
                        String money = "";
                        boolean repayBtn;
                        String receiptDate = "";
                    if (resObj.data.list.get(i).orderStatus.equals("INWAIT")) {//未还款
                        long dayOffset = NumberUtils.getDateOffset(resObj.data.list.get(i).expireDate, resObj.data.list.get(i).currDate);
                        if (dayOffset > 0) {
                            date = "Due Date : (" + dayOffset + " days overdue)" + NumberUtils.dateFormat(resObj.data.list.get(i).expireDate);
                        } else {
                            date = "Due Date : (" + Math.abs(dayOffset) + " days left)" + NumberUtils.dateFormat(resObj.data.list.get(i).expireDate);
                        }
                        state = "IN REPAYMENT";
                        money = NumberUtils.moneyFormat(resObj.data.list.get(i).loanAmount);
                        repayBtn = true;
                        receiptDate = "Receipt Date : " + NumberUtils.dateFormat(resObj.data.list.get(i).loanSuccesDate);
                    } else {//已还款
                        long dayOffset = NumberUtils.getDateOffset(resObj.data.list.get(i).expireDate, resObj.data.list.get(i).repaymentDate);
                        if (dayOffset > 0) {
                            date = "Settlement Date : (" + dayOffset + " days overdue)" + NumberUtils.dateFormat(resObj.data.list.get(i).expireDate);
                        } else {
                            date = "Settlement Date : (" + Math.abs(dayOffset) + " days left)" + NumberUtils.dateFormat(resObj.data.list.get(i).expireDate);
                        }
                        state = "REPAID";
                        money = NumberUtils.moneyFormat(resObj.data.list.get(i).loanAmount);
                        repayBtn = false;
                        receiptDate = "Receipt Date : " + NumberUtils.dateFormat(resObj.data.list.get(i).repaymentDate);
                    }
                    LoanRecordEntity entity = new LoanRecordEntity(date, state, money, repayBtn, receiptDate);
                        recordData.add(entity);
                    }
                }
                if (listener != null) {
                    listener.onSuccess(recordData);
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                HttpUtil.reportLog("getLoanRecord:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void getRepayLink(GetRepayLinkListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/repayment/link", headParams, params, new BaseResponse<RepayLinkReply>() {
            @Override
            public void onSuccess(Response response, RepayLinkReply resObj) {
                if (listener != null) {
                    listener.onSuccess(resObj.data);
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                HttpUtil.reportLog("getRepayLink:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void logOut(Activity activity, LogOutListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/out", headParams, params, new BaseResponse<BaseReply>(activity) {
            @Override
            public void onSuccess(Response response, BaseReply resObj) {
                SpUtil.putBoolean("hasLogin", false);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                HttpUtil.reportLog("logOut:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public interface GetLoanRecordListener {
        void onSuccess(ArrayList<LoanRecordEntity> data);

        void onError(int code, String errorMsg);
    }

    public interface LogOutListener {
        void onSuccess();

        void onError(int code, String errorMsg);
    }

    public interface GetRepayLinkListener {
        void onSuccess(String link);

        void onError(int code, String errorMsg);
    }
}
