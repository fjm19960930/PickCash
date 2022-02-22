package com.example.pickcash;

import android.app.Application;
import android.content.Context;

import com.zcolin.frame.app.BaseApp;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.Market;
import ai.advance.sdk.quality.lib.GuardianImageQualitySDK;

public class PickCashApplication extends BaseApp {

    public static String mToken = "";

    public static String mPhoneNum = "1111111111";
    public static String mTestPhoneNum = "1111111111";

    public static boolean mHasGetContactPermission = false;

    public static final int CONTACT_REQUEST_CODE = 1001;
    public static final int IFSC_REQUEST_CODE = 1002;
    public static final int FACE_VERIFY_REQUEST_CODE = 1003;
    public static final int FACE_VERIFY_PAN_CARD = 1004;
    public static final int FACE_VERIFY_AADHAAR_FRONT = 1005;
    public static final int FACE_VERIFY_AADHAAR_BACK = 1006;

    public static Application mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
