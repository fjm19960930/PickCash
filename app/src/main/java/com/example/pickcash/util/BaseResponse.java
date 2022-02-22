package com.example.pickcash.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.login.PickCashLoginActivity;
import com.zcolin.frame.http.ZReply;
import com.zcolin.frame.http.response.ZResponse;
import com.zcolin.frame.util.LogUtil;

import okhttp3.Response;

public class BaseResponse<T extends ZReply> extends ZResponse<T> {
    public BaseResponse() {
        super();
    }

    public BaseResponse(Activity activity) {
        super(activity);
    }

    @Override
    public void onSuccess(Response response, T resObj) {

    }

    @Override
    public void onError(int code, String error) {
        LogUtil.w("HttpResponse:", error);
//        if (code == 401 || code == 405) {
//            Toast.makeText(PickCashApplication.APP_CONTEXT, "Token is invalid, Please log in again", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(PickCashApplication.APP_CONTEXT, PickCashLoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            PickCashApplication.APP_CONTEXT.startActivity(intent);
//        } else {
//            Toast.makeText(PickCashApplication.APP_CONTEXT, error, Toast.LENGTH_SHORT).show();
//        }
    }
}
