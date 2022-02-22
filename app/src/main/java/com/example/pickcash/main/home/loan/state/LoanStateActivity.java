package com.example.pickcash.main.home.loan.state;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pickcash.R;
import com.example.pickcash.main.home.mgr.HomeMgr;
import com.zcolin.frame.app.BaseFrameActivity;

public class LoanStateActivity extends BaseFrameActivity {

    private LinearLayout root;
    private ImageView icon;
    private TextView title;
    private TextView content;
    private TextView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_state);

        String state = getIntent().getStringExtra("state");

        findViewById(R.id.loan_state_back_btn).setOnClickListener(v -> finish());

        root = (LinearLayout) findViewById(R.id.loan_state_root);
        icon = (ImageView) findViewById(R.id.loan_state_icon);
        title = (TextView) findViewById(R.id.loan_state_title);
        content = (TextView) findViewById(R.id.loan_state_tip);
        btn = (TextView) findViewById(R.id.loan_state_btn);
        refresh(state);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeMgr.getLoanState(mActivity, new HomeMgr.GetLoanStateListener() {
                    @Override
                    public void onSuccess(String state) {
                        switch (state) {
                            case "AUDITING":
                                refresh("check_processing");
                                break;
                            case "OUTING":
                                refresh("lend_processing");
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(int code, String errorMsg) {

                    }
                });
            }
        });
    }

    private void refresh(String state) {
        switch (state) {
            case "check_processing":
                root.setBackgroundColor(Color.parseColor("#F8C5E2"));
                icon.setBackgroundResource(R.mipmap.ic_apply_process);
                title.setText("Processing");
                content.setText("The platform is reviewing your information, \n" +
                        "please wait on this page, \n" +
                        "this will increase your pass rate");
                btn.setBackgroundResource(R.drawable.loan_state_apply_processing_btn);
                break;
            case "check_failed":
                root.setBackgroundColor(Color.parseColor("#FADDA6"));
                icon.setBackgroundResource(R.mipmap.ic_apply_process);
                title.setText("Application failed");
                content.setText("Sorry for not being able to review your materials\n" +
                        " effectively at the moment, \n" +
                        "we will continue to improve the service, \n" +
                        "welcome to apply again in two months");
                btn.setBackgroundResource(R.drawable.loan_state_apply_failed_btn);
                break;
            case "lend_processing":
                root.setBackgroundColor(Color.parseColor("#FADC66"));
                icon.setBackgroundResource(R.mipmap.ic_apply_process);
                title.setText("Processing");
                content.setText("The platform is processing this transaction\n" +
                        "After the process is complete, \n" +
                        "you will receive the SMS");
                btn.setBackgroundResource(R.drawable.loan_state_pay_processing_btn);
                break;
            case "lend_failed":
                root.setBackgroundColor(Color.parseColor("#A9E3DF"));
                icon.setBackgroundResource(R.mipmap.ic_lend_failed);
                title.setText("Payout failed");
                content.setText("Sorry, the platform cannot process this\n" +
                        "transaction due to a problem with your\n" +
                        "receiving account!");
                btn.setBackgroundResource(R.drawable.loan_state_pay_failed_btn);
                break;
            default:
                break;
        }
    }
}