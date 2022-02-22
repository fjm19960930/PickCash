package com.example.pickcash.main.home.loan.verify;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.liveness.LivenessActivity;
import com.example.pickcash.PickCashApplication;
import com.example.pickcash.R;
import com.example.pickcash.main.home.loan.verify.mgr.VerifyMgr;
import com.example.pickcash.main.home.loan.verify.mgr.entity.CardMessageReply;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.frame.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import ai.advance.liveness.lib.LivenessResult;
import ai.advance.sdk.iqa.IQAActivity;
import ai.advance.sdk.quality.lib.ImageQualityResult;
import ai.advance.sdk.quality.lib.enums.CardType;

public class VerifyActivity extends BaseFrameActivity {
    private static final int VERIFY_CARD_ERROR = 0;
    private static final int VERIFY_FACE_ERROR = 1;
    private static final int TOTAL_STEP = 4;
    private static final String P_CARD = "PCARD";
    private static final String A_FRONT = "AFRONT";
    private static final String A_BACK = "ABACK";

    private Dialog errorDialog;
    private Dialog confirmDialog;
    private AlertDialog exitDialog;

    private TextView nowStep;
    private TextView tip;
    private TextView stepTip;
    private TextView stepTip1;
    private TextView stepTip2;
    private ImageView cardBtn1;
    private ImageView cardBtn2;
    private TextView title;
    private ImageView errorImg;
    private TextView errorTip;

    private EditText confirmName;
    private EditText confirmId;
    private EditText confirmBirth;
    private EditText confirmFatherName;
    private EditText confirmGender;
    private EditText confirmAddress;
    private LinearLayout fatherNameLayout;
    private LinearLayout genderLayout;
    private LinearLayout frontLayout;
    private LinearLayout backLayout;
    private TextView confirmBtn;
    private TextView remakeBtn;

    private CardMessageReply.CardMessageData mCardData = new CardMessageReply.CardMessageData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        findViewById(R.id.verify_back_btn).setOnClickListener(v -> {
            if (exitDialog != null && tip != null) {
                exitDialog.show();
                tip.setText("You have " + (TOTAL_STEP - Integer.parseInt(String.valueOf(nowStep.getText())) + 1) + " steps to get the loan. Are you sure you want to leave?");
            }
        });

        title = (TextView) findViewById(R.id.verify_title);
        title.setText("PAN");
        nowStep = (TextView) findViewById(R.id.verify_step);
        TextView totalStep = (TextView) findViewById(R.id.verify_step_total);
        totalStep.setText("/" + TOTAL_STEP);
        stepTip = (TextView) findViewById(R.id.verify_step_tip);
        stepTip.setText("Please go through the facial-recognition process.");
        stepTip1 = (TextView) findViewById(R.id.verify_step_tip1);
        stepTip1.setText("1.Face authentication");
        stepTip2 = (TextView) findViewById(R.id.verify_step_tip2);
        stepTip2.setText("2.Front of Pan Card");
        TextView problemBtn = (TextView) findViewById(R.id.verify_problem_btn);
        problemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cardBtn1 = (ImageView) findViewById(R.id.verify_card1_btn);
        cardBtn1.setBackgroundResource(R.mipmap.face);
        cardBtn1.setEnabled(true);
        cardBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(String.valueOf(nowStep.getText())) == 1) {
                    startActivityForResult(new Intent(mActivity, LivenessActivity.class), PickCashApplication.FACE_VERIFY_REQUEST_CODE);
                } else if (Integer.parseInt(String.valueOf(nowStep.getText())) == 3) {
                    verifyCard(PickCashApplication.FACE_VERIFY_AADHAAR_FRONT, CardType.AADHAAR_FRONT.name());
                }
            }
        });
        cardBtn2 = (ImageView) findViewById(R.id.verify_card2_btn);
        cardBtn2.setBackgroundResource(R.mipmap.pan);
        cardBtn2.setEnabled(false);
        cardBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(String.valueOf(nowStep.getText())) == 2) {
                    verifyCard(PickCashApplication.FACE_VERIFY_PAN_CARD, CardType.PAN.name());
                } else if (Integer.parseInt(String.valueOf(nowStep.getText())) == 4) {
                    verifyCard(PickCashApplication.FACE_VERIFY_AADHAAR_BACK, CardType.AADHAAR_BACK.name());
                }
            }
        });
        TextView tip = (TextView) findViewById(R.id.verify_tip);
        tip.setVisibility(View.VISIBLE);
        tip.setText("1. Ensure that all the documents uploaded are clear and not blurred.\n2. Incomplete information may prevent you from passing the cetification successfufid");
        TextView nextBtn = (TextView) findViewById(R.id.verify_next_btn);
        nextBtn.setOnClickListener(v -> {
            int nStep = Integer.parseInt(String.valueOf(nowStep.getText()));
            Toast.makeText(mActivity, "Please complete step " + nStep, Toast.LENGTH_SHORT).show();
        });
        initErrorDialog();
        initConfirmDialog();
        initExitDialog();
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (errorDialog != null) {
            errorDialog.dismiss();
        }
        if (confirmDialog != null) {
            confirmDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PickCashApplication.FACE_VERIFY_REQUEST_CODE:
                if (LivenessResult.isSuccess()) {
                    VerifyMgr.submitFaceId(mActivity, LivenessResult.getLivenessId(), new VerifyMgr.SubmitFaceIdListener() {
                        @Override
                        public void onSuccess() {
                            toNext();
                            Toast.makeText(mActivity, "Success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int code, String errorMsg) {

                        }
                    });
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    showErrorDialog(VERIFY_FACE_ERROR);
                }
                break;
            case PickCashApplication.FACE_VERIFY_PAN_CARD:
                if (ImageQualityResult.isSuccess()) {// 检测成功
                    String imageQualityId = ImageQualityResult.getImageQualityId();// 图像id
                    CardType cardType = ImageQualityResult.getCardType();// 卡片类型
                    Bitmap bitmap = ImageQualityResult.getBitmap();// 预览框里的图像
                    File imageFile = saveFile(bitmap, "panCard.jpg");
                    VerifyMgr.getCardMessage(mActivity, P_CARD, imageFile, new VerifyMgr.GetCardMessageListener() {
                        @Override
                        public void onSuccess(CardMessageReply.CardMessageData data) {
                            showConfirmDialog(PickCashApplication.FACE_VERIFY_PAN_CARD, data);
                            Toast.makeText(mActivity, "Success PAN_CARD", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int code, String errorMsg) {

                        }
                    });
                } else {// 检测失败
                    String errorCode = ImageQualityResult.getErrorCode();// 失败错误码
                    String errorMsg = ImageQualityResult.getErrorMsg();// 失败原因，可能为空(例如用户点击返回键放弃，则只有错误码，没有msg)
                    Log.e("fjm", "errorCode：" + errorCode + "，errorMsg:" + errorMsg);
                    showErrorDialog(VERIFY_CARD_ERROR);
                }
                break;
            case PickCashApplication.FACE_VERIFY_AADHAAR_FRONT:
                if (ImageQualityResult.isSuccess()) {// 检测成功
                    String imageQualityId = ImageQualityResult.getImageQualityId();// 图像id
                    CardType cardType = ImageQualityResult.getCardType();// 卡片类型
                    Bitmap bitmap = ImageQualityResult.getBitmap();// 预览框里的图像
                    File imageFile = saveFile(bitmap, "ACardFront.jpg");
                    VerifyMgr.getCardMessage(mActivity, A_FRONT, imageFile, new VerifyMgr.GetCardMessageListener() {
                        @Override
                        public void onSuccess(CardMessageReply.CardMessageData data) {
                            showConfirmDialog(PickCashApplication.FACE_VERIFY_AADHAAR_FRONT, data);
                            Toast.makeText(mActivity, "Success AADHAAR_FRONT", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int code, String errorMsg) {

                        }
                    });
                } else {// 检测失败
                    String errorCode = ImageQualityResult.getErrorCode();// 失败错误码
                    String errorMsg = ImageQualityResult.getErrorMsg();// 失败原因，可能为空(例如用户点击返回键放弃，则只有错误码，没有msg)
                    Log.e("fjm", "errorCode：" + errorCode + "，errorMsg:" + errorMsg);
                    showErrorDialog(VERIFY_CARD_ERROR);
                }
                break;
            case PickCashApplication.FACE_VERIFY_AADHAAR_BACK:
                if (ImageQualityResult.isSuccess()) {// 检测成功
                    String imageQualityId = ImageQualityResult.getImageQualityId();// 图像id
                    CardType cardType = ImageQualityResult.getCardType();// 卡片类型
                    Bitmap bitmap = ImageQualityResult.getBitmap();// 预览框里的图像
                    File imageFile = saveFile(bitmap, "ACardBack.jpg");
                    VerifyMgr.getCardMessage(mActivity, A_BACK, imageFile, new VerifyMgr.GetCardMessageListener() {
                        @Override
                        public void onSuccess(CardMessageReply.CardMessageData data) {
                            showConfirmDialog(PickCashApplication.FACE_VERIFY_AADHAAR_BACK, data);
                            Toast.makeText(mActivity, "Success AADHAAR_BACK", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int code, String errorMsg) {

                        }
                    });
                } else {// 检测失败
                    String errorCode = ImageQualityResult.getErrorCode();// 失败错误码
                    String errorMsg = ImageQualityResult.getErrorMsg();// 失败原因，可能为空(例如用户点击返回键放弃，则只有错误码，没有msg)
                    Log.e("fjm", "errorCode：" + errorCode + "，errorMsg:" + errorMsg);
                    showErrorDialog(VERIFY_CARD_ERROR);
                }
                break;
        }
    }

    private void toNext() {
        int step = Integer.parseInt(String.valueOf(nowStep.getText())) + 1;
        if (step <= TOTAL_STEP) {
            nowStep.setText("" + step);
            if (step == 2) {
                tip.setVisibility(View.VISIBLE);
                stepTip.setText("Please upload the front image of Pan Card.");
                cardBtn1.setEnabled(false);
                cardBtn2.setEnabled(true);
            } else if (step == 3) {
                tip.setVisibility(View.GONE);
                title.setText("Identity Authentication");
                stepTip.setText("Please upload Aadhaa card image for the front side.");
                stepTip1.setText("3.Front of Aadhaa Card");
                stepTip2.setText("4.Back of Aadhaa Card");
                cardBtn1.setEnabled(true);
                cardBtn2.setEnabled(false);
                cardBtn1.setBackgroundResource(R.mipmap.icon_acard_front);
                cardBtn2.setBackgroundResource(R.mipmap.icon_acard_back);
            } else if (step == 4) {
                stepTip.setText("Please upload Aadhaa card image for the back side.");
                cardBtn1.setEnabled(false);
                cardBtn2.setEnabled(true);
            }
        } else {
            startActivity(new Intent(mActivity, PersonalInfoActivity.class));
        }
    }

    private void verifyCard(int code, String cardType) {
        Intent intent = new Intent(mActivity, IQAActivity.class);
        intent.putExtra("cardType", cardType);// 默认值为 AUTO
        intent.putExtra("maxRetryTimes", 1);//默认重试次数为1次
        intent.putExtra("soundPlayEnable", true);//默认播放声音
        startActivityForResult(intent, code);
    }

    private void initErrorDialog() {
        errorDialog = new Dialog(this, R.style.DialogTheme);
        View view = View.inflate(this, R.layout.verify_error_dialog_layout, null);
        errorDialog.setContentView(view);

        Window window = errorDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        errorDialog.findViewById(R.id.verify_error_dialog_close).setOnClickListener(v -> errorDialog.dismiss());
        errorImg = (ImageView) errorDialog.findViewById(R.id.verify_error_dialog_img);
        errorTip = (TextView) errorDialog.findViewById(R.id.verify_error_dialog_tip);
    }

    private void showErrorDialog(int errorType) {
        if (errorType == VERIFY_CARD_ERROR) {
            errorImg.setBackgroundResource(R.mipmap.pic_verify_card_error);
            errorTip.setText("");
            errorTip.setVisibility(View.GONE);
        } else {
            errorImg.setBackgroundResource(R.mipmap.pic_verify_face_error);
            errorTip.setText("1.Good light on your face \n2.Hold phone in front of you");
            errorTip.setVisibility(View.VISIBLE);
        }
        errorDialog.show();
    }

    private void initConfirmDialog() {
        confirmDialog = new Dialog(this, R.style.DialogTheme);
        View view = View.inflate(this, R.layout.verify_confirm_dialog_layout, null);
        confirmDialog.setContentView(view);

        Window window = confirmDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmName = findViewById(R.id.confirm_name);
        confirmId = findViewById(R.id.confirm_id);
        confirmBirth = findViewById(R.id.confirm_birth);
        confirmFatherName = findViewById(R.id.confirm_father_name);
        confirmGender = findViewById(R.id.confirm_gender);
        confirmAddress = findViewById(R.id.confirm_address);
        fatherNameLayout = findViewById(R.id.confirm_father_name_layout);
        genderLayout = findViewById(R.id.confirm_gender_layout);
        frontLayout = findViewById(R.id.confirm_a_card_front_layout);
        backLayout = findViewById(R.id.confirm_a_card_back_layout);
        confirmBtn = findViewById(R.id.confirm_btn);
        remakeBtn = findViewById(R.id.confirm_remake_btn);
    }

    private void showConfirmDialog(int typeCode, CardMessageReply.CardMessageData data) {
        if (typeCode == PickCashApplication.FACE_VERIFY_PAN_CARD) {
            frontLayout.setVisibility(View.VISIBLE);
            backLayout.setVisibility(View.GONE);
            fatherNameLayout.setVisibility(View.VISIBLE);
            genderLayout.setVisibility(View.GONE);
            confirmName.setText(data.names);mCardData.names = data.names;
            confirmId.setText(data.cardNumberId);mCardData.cardNumberId = data.cardNumberId;
            confirmBirth.setText(data.birthday);mCardData.birthday = data.birthday;
            confirmFatherName.setText(data.father);mCardData.father = data.father;
        } else if (typeCode == PickCashApplication.FACE_VERIFY_AADHAAR_FRONT) {
            frontLayout.setVisibility(View.VISIBLE);
            backLayout.setVisibility(View.GONE);
            fatherNameLayout.setVisibility(View.GONE);
            genderLayout.setVisibility(View.VISIBLE);
            confirmName.setText(data.names);
            confirmId.setText(data.cardNumberId);
            confirmBirth.setText(data.birthday);
            confirmGender.setText(data.sex);mCardData.sex = data.sex;
        } else if (typeCode == PickCashApplication.FACE_VERIFY_AADHAAR_BACK) {
            frontLayout.setVisibility(View.GONE);
            backLayout.setVisibility(View.VISIBLE);
            confirmAddress.setText(data.address);mCardData.address = data.address;
        }
        remakeBtn.setOnClickListener(v -> {
            switch (typeCode) {
                case PickCashApplication.FACE_VERIFY_PAN_CARD:
                    verifyCard(PickCashApplication.FACE_VERIFY_PAN_CARD, CardType.PAN.name());
                    break;
                case PickCashApplication.FACE_VERIFY_AADHAAR_FRONT:
                    verifyCard(PickCashApplication.FACE_VERIFY_AADHAAR_FRONT, CardType.AADHAAR_FRONT.name());
                    break;
                case PickCashApplication.FACE_VERIFY_AADHAAR_BACK:
                    verifyCard(PickCashApplication.FACE_VERIFY_AADHAAR_BACK, CardType.AADHAAR_BACK.name());
                    break;
            }
            confirmDialog.cancel();
        });
        confirmBtn.setOnClickListener(v -> {
            String type = "";
            if (typeCode == PickCashApplication.FACE_VERIFY_PAN_CARD) {
                type = P_CARD;
                mCardData.names = confirmName.getText().toString();
                mCardData.cardNumberId = confirmId.getText().toString();
                mCardData.birthday = confirmBirth.getText().toString();
                mCardData.father = confirmFatherName.getText().toString();
            } else if (typeCode == PickCashApplication.FACE_VERIFY_AADHAAR_FRONT) {
                type = A_FRONT;
                mCardData.names = confirmName.getText().toString();
                mCardData.cardNumberId = confirmId.getText().toString();
                mCardData.birthday = confirmBirth.getText().toString();
                mCardData.sex = confirmGender.getText().toString();
            } else if (typeCode == PickCashApplication.FACE_VERIFY_AADHAAR_BACK) {
                type = A_BACK;
                mCardData.address = confirmAddress.getText().toString();
            }
            VerifyMgr.changeCardMessage(mActivity, type, mCardData, new VerifyMgr.ChangeCardMessageListener() {
                @Override
                public void onSuccess(String type) {
                    toNext();
                }

                @Override
                public void onError(int code, String errorMsg) {

                }
            });
            confirmDialog.cancel();
        });
        confirmDialog.show();
    }

    private void initExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_exit_verify_layout, null);
        ImageView btnClose = view.findViewById(R.id.verify_exit_dialog_close);
        btnClose.setOnClickListener(v -> {
            exitDialog.cancel();
            finish();
        });
        TextView btnCancel = view.findViewById(R.id.verify_exit_dialog_cancel);
        btnCancel.setOnClickListener(v -> {
            exitDialog.cancel();
        });
        tip = view.findViewById(R.id.verify_exit_tip);

        builder.setView(view);
        exitDialog = builder.create();
        exitDialog.setCancelable(false);
        exitDialog.setCanceledOnTouchOutside(false);

        Window window = exitDialog.getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private File saveFile(Bitmap bm, String fileName) {//将Bitmap类型的图片转化成file类型，便于上传到服务器
        try {
            String path = Environment.getExternalStorageDirectory() + "/card";
            File dirFile = new File(path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            File myCaptureFile = new File(path + "/" + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            return myCaptureFile;
        } catch (Exception e) {
            return null;
        }

    }
}