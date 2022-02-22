package com.example.pickcash.main.home.loan.apply;

import android.content.Intent;
import android.os.Bundle;

import com.example.pickcash.R;
import com.example.pickcash.main.home.loan.state.LoanStateActivity;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.frame.util.SpUtil;

public class LoanApplyTestActivity extends BaseFrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_apply_test);

        findViewById(R.id.loan_apply_test_back_btn).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.loan_apply_test_btn).setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, LoanStateActivity.class);
            intent.putExtra("state", "check_processing");
            startActivity(intent);
            SpUtil.putBoolean("test_loan_state", true);
        });
    }
}