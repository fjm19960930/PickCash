package com.example.pickcash.login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.login.mgr.PickCashLoginMgr;
import com.example.pickcash.main.mine.mgr.MineMgr;
import com.example.pickcash.main.mine.mgr.entity.ConfigReply;
import com.example.pickcash.util.NumberUtils;
import com.example.pickcash.main.PickCashMainActivity;
import com.example.pickcash.R;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.frame.permission.PermissionHelper;
import com.zcolin.frame.permission.PermissionsResultAction;
import com.zcolin.frame.util.SpUtil;

public class PickCashLoginActivity extends BaseFrameActivity {

    private LinearLayout loginPermissionLayout;
    private ObjectAnimator permissionLayoutAnimator;
    private TextView tvSendVoiceOtp;
    private TextView tvSendOtp;
    private TextView tvOtp60;
    private TimeCount timeCount;
    private boolean isNeedShowVoiceSend = false;
    private String mVerifyCode = "";
    private String mPhoneNum = "";
    private final PickCashLoginMgr.GetVerifyCodeListener getVerifyCodeListener = new PickCashLoginMgr.GetVerifyCodeListener() {
        @Override
        public void onSuccess() {
            tvOtp60.setVisibility(View.VISIBLE);
            tvSendOtp.setVisibility(View.GONE);
            if (isNeedShowVoiceSend) {
                tvSendVoiceOtp.setVisibility(View.GONE);
            }
            timeCount.start();
//            if (verifyCode == null || verifyCode.isEmpty()) {
//                mVerifyCode = "888888";
//            } else {
//                mVerifyCode = verifyCode;
//            }
        }

        @Override
        public void onError(int code, String errorMsg) {
            Toast.makeText(mActivity, "Send failed, please try again!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_cash_login);

        timeCount = new TimeCount(60000, 1000);

        //Permission
        TextView permissionTip = (TextView) findViewById(R.id.login_permission_tip);
        permissionTip.setMovementMethod(LinkMovementMethod.getInstance());
        TextView btnPermissionOK = (TextView) findViewById(R.id.login_permission_ok);
        loginPermissionLayout = (LinearLayout) findViewById(R.id.login_permission_layout);
        btnPermissionOK.setOnClickListener(v -> permissionApply());

        //PhoneNumber
        EditText etPhoneNumber = (EditText) findViewById(R.id.login_phone_number_et);
        SpannableString ssPhoneNumber = new SpannableString("Phone Number");
        AbsoluteSizeSpan assPhoneNumber = new AbsoluteSizeSpan(14,true);
        ssPhoneNumber.setSpan(assPhoneNumber, 0, ssPhoneNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssPhoneNumber.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ssPhoneNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhoneNumber.setHint(new SpannedString(ssPhoneNumber));

        //VerifyCode
        EditText etOtp = (EditText) findViewById(R.id.login_otp_et);
        SpannableString ssOtp = new SpannableString("OTP");
        AbsoluteSizeSpan assOtp = new AbsoluteSizeSpan(14,true);
        ssOtp.setSpan(assOtp, 0, ssOtp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssOtp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ssOtp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etOtp.setHint(new SpannedString(ssOtp));

        //sendVerifyCode
        tvSendVoiceOtp = (TextView) findViewById(R.id.login_otp_voice_send);
        tvSendVoiceOtp.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvSendOtp = (TextView) findViewById(R.id.login_otp_send);
        tvSendOtp.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvSendVoiceOtp.setOnClickListener(v -> {
            if (etPhoneNumber.getText().length() == 10 && NumberUtils.isNumeric(etPhoneNumber.getText().toString())) {
                PickCashLoginMgr.getVerifyCode(etPhoneNumber.getText().toString(), 2, getVerifyCodeListener);
                mPhoneNum = etPhoneNumber.getText().toString();
            } else {
                Toast.makeText(mActivity, "Please enter the correct Mobile No.", Toast.LENGTH_SHORT).show();
            }
        });
        tvSendOtp.setOnClickListener(v -> {
            if (etPhoneNumber.getText().length() == 10 && NumberUtils.isNumeric(etPhoneNumber.getText().toString())) {
                PickCashLoginMgr.getVerifyCode(etPhoneNumber.getText().toString(), 1, getVerifyCodeListener);
                mPhoneNum = etPhoneNumber.getText().toString();
            } else {
                Toast.makeText(mActivity, "Please enter the correct Mobile No.", Toast.LENGTH_SHORT).show();
            }
        });
        tvOtp60 = (TextView) findViewById(R.id.login_otp_60s);
        tvOtp60.setVisibility(View.GONE);
        PickCashLoginMgr.getVoiceSupportState(new PickCashLoginMgr.GetVoiceSupportStateListener() {
            @Override
            public void onSuccess(boolean state) {
                isNeedShowVoiceSend = state;
                if (state) {
                    tvSendVoiceOtp.setVisibility(View.VISIBLE);
                } else {
                    tvSendVoiceOtp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int code, String errorMsg) { }
        });

        //Login
        TextView btnLogin = (TextView) findViewById(R.id.login_btn);
        TextView btnLoginCannot = (TextView) findViewById(R.id.login_btn_cannot);
        btnLogin.setOnClickListener(v -> {
            if (etOtp.getText().length() == 6 && etPhoneNumber.getText().length() == 10
                    && NumberUtils.isNumeric(etOtp.getText().toString())
                    && NumberUtils.isNumeric(etPhoneNumber.getText().toString())){
                if (mPhoneNum.equals(etPhoneNumber.getText().toString())) {
//                    if (mVerifyCode.equals(etOtp.getText().toString())) {
                        PickCashLoginMgr.login(mActivity, mPhoneNum, etOtp.getText().toString(), new PickCashLoginMgr.LoginListener() {
                            @Override
                            public void onSuccess(boolean isNeedShowDialog) {
                                Intent intent = new Intent(mActivity, PickCashMainActivity.class);
                                intent.putExtra("isNeedShowDialog", isNeedShowDialog);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(int code, String errorMsg) {
                                Toast.makeText(mActivity, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        });
//                    } else {
//                        Toast.makeText(mActivity, "verification code is incorrect.", Toast.LENGTH_SHORT).show();
//                    }
                    MineMgr.getConfigData(new MineMgr.GetConfigDataListener() {
                        @Override
                        public void onSuccess(ConfigReply.ConfigData data) {
                            PickCashApplication.mTestPhoneNum = data.testPhone;
                        }

                        @Override
                        public void onError(int code, String errorMsg) {

                        }
                    });
                } else {
                    Toast.makeText(mActivity, "phone number or verification code is incorrect.", Toast.LENGTH_SHORT).show();
                }
            } else if (etPhoneNumber.getText().length() != 10 || !NumberUtils.isNumeric(etPhoneNumber.getText().toString())) {
                Toast.makeText(mActivity, "phone number is incorrect.", Toast.LENGTH_SHORT).show();
            } else if (etOtp.getText().length() != 6 || !NumberUtils.isNumeric(etOtp.getText().toString())) {
                Toast.makeText(mActivity, "verification code is incorrect.", Toast.LENGTH_SHORT).show();
            }
        });
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && etOtp.getText().length() > 0) {
                    btnLogin.setVisibility(View.VISIBLE);
                    btnLoginCannot.setVisibility(View.GONE);
                } else {
                    btnLogin.setVisibility(View.GONE);
                    btnLoginCannot.setVisibility(View.VISIBLE);
                }
            }
        });
        etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && etPhoneNumber.getText().length() > 0) {
                    btnLogin.setVisibility(View.VISIBLE);
                    btnLoginCannot.setVisibility(View.GONE);
                } else {
                    btnLogin.setVisibility(View.GONE);
                    btnLoginCannot.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 初始化权限请求
     */
    private void permissionApply() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA};
        PermissionHelper.requestPermission(this, permissions, new PermissionsResultAction() {
            @Override
            public void onGranted() {//权限申请成功
                if (loginPermissionLayout != null) {
                    loginPermissionLayoutRemoveAnimation();
                }
            }

            @Override
            public void onDenied(String permission) {//权限申请失败
                if (loginPermissionLayout != null) {
                    loginPermissionLayout.setVisibility(View.VISIBLE);
                }
                Log.e("fjm", permission == null ? "" : permission);
            }
        });
    }

    private void loginPermissionLayoutRemoveAnimation() {
        permissionLayoutAnimator = ObjectAnimator.ofFloat(loginPermissionLayout, "alpha", 1f, 0f);
        permissionLayoutAnimator.setDuration(200);
        permissionLayoutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginPermissionLayout.setVisibility(View.GONE);
            }
        });
        permissionLayoutAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (permissionLayoutAnimator != null) {
            permissionLayoutAnimator.cancel();
            permissionLayoutAnimator = null;
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvOtp60.setText("Can be resend after " + millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            tvOtp60.setVisibility(View.GONE);
            tvSendOtp.setVisibility(View.VISIBLE);
            if (isNeedShowVoiceSend) {
                tvSendVoiceOtp.setVisibility(View.VISIBLE);
            }
        }
    }
}