package com.example.pickcash;

import android.app.Application;

import com.appsflyer.AppsFlyerLib;
import com.example.pickcash.util.CrashHandler;
import com.zcolin.frame.app.BaseApp;

public class PickCashApplication extends BaseApp {

    public static String mToken = "";

    public static String mPhoneNum = "";
    public static String mTestPhoneNum = "";

    public static String mKfPhone = "";
    public static String mKfEmail = "";
    public static String mVersion = "";

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

        String afDevKey = "";
        AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
        appsflyer.setDebugLog(true);
        appsflyer.setMinTimeBetweenSessions(0);
        appsflyer.init(afDevKey, null, this);
        appsflyer.start(this);

        CrashHandler.getInstance().init(this);
    }
}
