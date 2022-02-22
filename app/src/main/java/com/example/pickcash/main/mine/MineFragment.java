package com.example.pickcash.main.mine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.R;
import com.example.pickcash.login.PickCashLoginActivity;
import com.example.pickcash.main.mine.faq.FaqActivity;
import com.example.pickcash.main.mine.kefu.KefuActivity;
import com.example.pickcash.main.mine.mgr.MineMgr;
import com.example.pickcash.main.mine.mgr.entity.ConfigReply;
import com.example.pickcash.main.mine.record.LoanRecordActivity;
import com.example.pickcash.main.mine.us.AboutUsActivity;

public class MineFragment extends Fragment {
    private AlertDialog logOutDialog;
    private String mKfPhone;
    private String mKfEmail;
    private String mVersion;

    public MineFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MineMgr.getConfigData(new MineMgr.GetConfigDataListener() {
            @Override
            public void onSuccess(ConfigReply.ConfigData data) {
                mKfPhone = data.kfPhone;
                mKfEmail = data.kfEmail;
                mVersion = data.version;
            }

            @Override
            public void onError(int code, String errorMsg) { }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        TextView phoneNum = view.findViewById(R.id.mine_phone_num);
        String num = PickCashApplication.mPhoneNum.substring(0, 2) + "******" + PickCashApplication.mPhoneNum.substring(8, 10);
        phoneNum.setText(num);

        LinearLayout mineOutBtn = view.findViewById(R.id.mine_out_btn);
        mineOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog.show();
            }
        });
        LinearLayout mineFaqBtn = view.findViewById(R.id.mine_faq_btn);
        mineFaqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), FaqActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout mineUsBtn = view.findViewById(R.id.mine_us_btn);
        mineUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), AboutUsActivity.class);
                intent.putExtra("version", mVersion);
                startActivity(intent);
            }
        });
        LinearLayout mineKefuBtn = view.findViewById(R.id.mine_customer_btn);
        mineKefuBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), KefuActivity.class);
            intent.putExtra("phone", mKfPhone);
            intent.putExtra("email", mKfEmail);
            startActivity(intent);
        });
        LinearLayout mineRecordBtn = view.findViewById(R.id.mine_record_btn);
        mineRecordBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), LoanRecordActivity.class);
            startActivity(intent);
        });

        initLogoutDialog();
        return view;
    }

    private void initLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_log_out_layout, null);
        TextView btnCancel = view.findViewById(R.id.mine_log_out_cancel);
        btnCancel.setOnClickListener(v -> logOutDialog.cancel());
        TextView btnOK = view.findViewById(R.id.mine_log_out_confirm);
        btnOK.setOnClickListener(v -> MineMgr.logOut(requireActivity(), new MineMgr.LogOutListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(requireActivity(), PickCashLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(int code, String errorMsg) { }
        }));
        builder.setView(view);
        logOutDialog = builder.create();
        logOutDialog.setCancelable(false);
        logOutDialog.setCanceledOnTouchOutside(false);

        Window window = logOutDialog.getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}