package com.example.pickcash.main.home.loan.verify.mgr;

import android.app.Activity;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.main.home.loan.verify.mgr.entity.BankIfscReply;
import com.example.pickcash.main.home.loan.verify.mgr.entity.CardMessageReply;
import com.example.pickcash.main.home.loan.verify.mgr.entity.PersonalInfoApply;
import com.example.pickcash.main.home.loan.verify.mgr.entity.PersonalInfoFieldsReply;
import com.example.pickcash.util.BaseReply;
import com.example.pickcash.util.BaseResponse;
import com.example.pickcash.util.HttpUtil;
import com.zcolin.frame.http.ZHttp;
import com.zcolin.frame.http.response.ZResponse;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class VerifyMgr {
    private static final LinkedHashMap<String, String> ifscParams = new LinkedHashMap<>();
    public static void getPersonalInfoFields(GetPersonalInfoFieldsListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        ZHttp.postWithHeader(HttpUtil.BASE_URL + "/app/fields", headParams, params, new BaseResponse<PersonalInfoFieldsReply>() {
            @Override
            public void onSuccess(Response response, PersonalInfoFieldsReply resObj) {
                if (listener != null) {
                    listener.onSuccess(resObj.data.contactCount, resObj.data.fields);
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("getPersonalInfoFields:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void submitPersonalInfo(PersonalInfoApply apply, SubmitBaseInfoListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        StringBuilder params = new StringBuilder("{\n" +
                "\"email\": \"" + apply.email + "\",\n" +
                "\"workType\": \"" + apply.workType + "\",\n" +
                "\"languages\": \"" + apply.languages + "\",\n" +
                "\"marry\": \"" + apply.marry + "\",\n" +
                "\"monthlySalary\": " + apply.monthlySalary + ",\n");
        if (apply.fields != null && apply.fields.size() > 0) {
            params.append("\"fields\": [\n");
            for (int i = 0; i < apply.fields.size(); i++) {
                params.append("{\n" + "\"fieldName\": \"").append(apply.fields.get(i).fieldName).append("\",\n").append("\"values\": \"").append(apply.fields.get(i).values).append("\"\n");
                if (i == apply.fields.size() - 1) {
                    params.append("}\n");
                } else {
                    params.append("},\n");
                }
            }
            params.append("],\n");
        }
        if (apply.contacts != null && apply.contacts.size() > 0) {
            params.append("\"contacts\": [\n");
            for (int i = 0; i < apply.contacts.size(); i++) {
                params.append("{\n" + "\"relation\": \"").append(apply.contacts.get(i).relation).append("\",\n").append("\"name\": \"").append(apply.contacts.get(i).name).append("\",\n").append("\"phone\": \"").append(apply.contacts.get(i).phone).append("\"\n");
                if (i == apply.contacts.size() - 1) {
                    params.append("}\n");
                } else {
                    params.append("},\n");
                }
            }
            params.append("]\n");
        }
        params.append("}");
        ZHttp.postJsonWithHeader(HttpUtil.BASE_URL + "/app/basedata", headParams, params.toString(), new BaseResponse<BaseReply>() {
            @Override
            public void onSuccess(Response response, BaseReply resObj) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("submitPersonalInfo:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void getIfsc(String type, String value, GetIfscListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        StringBuilder params = new StringBuilder("{\n");
        if (type != null && !type.isEmpty()) {
            ifscParams.put(type, value);
            if (type.equals("state")) {
                ifscParams.remove("city");
                ifscParams.remove("bank");
                ifscParams.remove("branch");
            } else if (type.equals("city")) {
                ifscParams.remove("bank");
                ifscParams.remove("branch");
            } else if (type.equals("bank")) {
                ifscParams.remove("branch");
            }
            for (String key : ifscParams.keySet()) {
                params.append("\"").append(key).append("\": \"").append(ifscParams.get(key)).append("\",\n");
            }
        } else {
            ifscParams.clear();
        }
        params.append("}");
        ZHttp.postJsonWithHeader(HttpUtil.BASE_URL + "/app/bank/ifsc/get", headParams, params.toString(), new BaseResponse<BankIfscReply>() {
            @Override
            public void onSuccess(Response response, BankIfscReply resObj) {
                if (listener != null && resObj.data != null && resObj.data.size() > 0) {
                    String[] items = new String[resObj.data.size()];
                    for (int i = 0; i < resObj.data.size(); i++) {
                        items[i] = resObj.data.get(i);
                    }
                    listener.onSuccess(items);
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("getIfsc:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void submitBankCardInfo(String ptm, String upi, String bankAccount, String ifsc, SubmitBankCardInfoListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        String params = "{\n" + "\"" + "bankCode" + "\": \"" + ifsc + "\",\n" +
                "\"" + "bankAccount" + "\": \"" + bankAccount + "\",\n" +
                "\"" + "upi" + "\": \"" + upi + "\",\n" +
                "\"" + "ptm" + "\": \"" + ptm + "\"\n" +
                "}";
        ZHttp.postJsonWithHeader(HttpUtil.BASE_URL + "/app/bank/addupdate", headParams, params, new BaseResponse<BaseReply>() {
            @Override
            public void onSuccess(Response response, BaseReply resObj) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("submitBankCardInfo:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void submitFaceId(Activity activity, String id, SubmitFaceIdListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        ZHttp.postStringWithHeader(HttpUtil.BASE_URL + "/app/liveness", headParams, id, null, new ZResponse<BaseReply>(activity){
            @Override
            public void onSuccess(Response response, BaseReply resObj) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(int code, String error) {
                HttpUtil.reportLog("submitFaceId:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void getCardMessage(Activity activity, String type, File file, GetCardMessageListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", file);
        ZHttp.uploadFileWithHeader(HttpUtil.BASE_URL + "/app/upcard", headParams, params, fileParams, new BaseResponse<CardMessageReply>(activity){
            @Override
            public void onSuccess(Response response, CardMessageReply resObj) {
                super.onSuccess(response, resObj);
                if (listener != null) {
                    listener.onSuccess(resObj.data);
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                HttpUtil.reportLog("getCardMessage:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public static void changeCardMessage(Activity activity, String type, CardMessageReply.CardMessageData data, ChangeCardMessageListener listener) {
        LinkedHashMap<String, String> headParams = new LinkedHashMap<>();
        headParams.put("tm", HttpUtil.TM);
        headParams.put("token", PickCashApplication.mToken);
        headParams.put("channel", HttpUtil.CHANNEL);
        headParams.put("Accept-Language", HttpUtil.ACCEPT_LANGUAGE);
        String params = "{\n" + "\"" + "type" + "\": \"" + type + "\",\n" +
                "\"" + "names" + "\": \"" + data.names + "\",\n" +
                "\"" + "cardNumberId" + "\": \"" + data.cardNumberId + "\",\n" +
                "\"" + "sex" + "\": \"" + data.sex + "\",\n" +
                "\"" + "birthday" + "\": \"" + data.birthday + "\",\n" +
                "\"" + "father" + "\": \"" + data.father + "\",\n" +
                "\"" + "address" + "\": \"" + data.address + "\"\n" +
                "}";
        ZHttp.postJsonWithHeader(HttpUtil.BASE_URL + "/app/uploadself", headParams, params, new BaseResponse<BaseReply>(activity){
            @Override
            public void onSuccess(Response response, BaseReply resObj) {
                super.onSuccess(response, resObj);
                if (listener != null) {
                    listener.onSuccess(type);
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                HttpUtil.reportLog("changeCardMessage:" + error);
                if (listener != null) {
                    listener.onError(code, error);
                }
            }
        });
    }

    public interface GetPersonalInfoFieldsListener {
        void onSuccess(int contactCount, List<PersonalInfoFieldsReply.Fields> list);

        void onError(int code, String errorMsg);
    }

    public interface SubmitBaseInfoListener {
        void onSuccess();

        void onError(int code, String errorMsg);
    }

    public interface GetIfscListener {
        void onSuccess(String[] items);

        void onError(int code, String errorMsg);
    }

    public interface SubmitBankCardInfoListener {
        void onSuccess();

        void onError(int code, String errorMsg);
    }

    public interface SubmitFaceIdListener{
        void onSuccess();

        void onError(int code, String errorMsg);
    }

    public interface GetCardMessageListener {
        void onSuccess(CardMessageReply.CardMessageData data);

        void onError(int code, String errorMsg);
    }

    public interface ChangeCardMessageListener {
        void onSuccess(String type);

        void onError(int code, String errorMsg);
    }

}
