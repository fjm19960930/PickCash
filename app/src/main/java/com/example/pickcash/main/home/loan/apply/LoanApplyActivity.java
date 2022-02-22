package com.example.pickcash.main.home.loan.apply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pickcash.R;
import com.example.pickcash.main.home.loan.apply.mgr.ApplyMgr;
import com.example.pickcash.main.home.loan.apply.mgr.ApplyReply;
import com.example.pickcash.main.home.loan.state.LoanStateActivity;
import com.example.pickcash.util.NumberUtils;
import com.zcolin.frame.app.BaseFrameActivity;

public class LoanApplyActivity extends BaseFrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_apply);

        findViewById(R.id.loan_apply_back_btn).setOnClickListener(v -> {
            finish();
        });
        TextView amount = findViewById(R.id.loan_apply_amount);
        TextView tenure = findViewById(R.id.loan_apply_tenure);
        TextView serviceFee = findViewById(R.id.loan_apply_service_fee);
        TextView gst = findViewById(R.id.loan_apply_gst);
        TextView amountAccount = findViewById(R.id.loan_apply_amount_account);
        TextView rateAmount = findViewById(R.id.loan_apply_rate_amount);
        TextView totalRepay = findViewById(R.id.loan_apply_total_repay);
        TextView expiryDate = findViewById(R.id.loan_apply_expiry_date);
        TextView paytmId = findViewById(R.id.loan_apply_paytm_id);
        TextView upiId = findViewById(R.id.loan_apply_upi_id);
        TextView ifsc =  findViewById(R.id.loan_apply_ifsc);
        TextView receivingAccount = findViewById(R.id.loan_apply_receiving_account);
        ApplyMgr.getLoanApplyData(new ApplyMgr.GetLoanApplyListener() {
            @Override
            public void onSuccess(ApplyReply.ApplyData applyData) {
                amount.setText(NumberUtils.moneyFormat(applyData.loanAmount));
                tenure.setText(applyData.loanDay + " days");
                serviceFee.setText(NumberUtils.moneyFormat(applyData.serviceAmount));
                gst.setText(NumberUtils.moneyFormat(applyData.gstAmount));
                amountAccount.setText(NumberUtils.moneyFormat(applyData.arrivalAmount));
                rateAmount.setText(NumberUtils.moneyFormat(applyData.rateAmount));
                totalRepay.setText(NumberUtils.moneyFormat(applyData.expireAmount));
                expiryDate.setText(NumberUtils.dateFormat(applyData.expireDate));
                paytmId.setText(applyData.bankPtm);
                upiId.setText(applyData.bankUpi);
                ifsc.setText(applyData.bankCode);
                receivingAccount.setText(applyData.bankAccount);
            }

            @Override
            public void onError(int code, String errorMsg) {

            }
        });
        findViewById(R.id.loan_apply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyMgr.loanApply(new ApplyMgr.LoanApplyListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(mActivity, LoanStateActivity.class);
                        intent.putExtra("state", "check_processing");
                        startActivity(intent);
                    }

                    @Override
                    public void onError(int code, String errorMsg) {
                        Intent intent = new Intent(mActivity, LoanStateActivity.class);
                        intent.putExtra("state", "check_failed");
                        startActivity(intent);
                    }
                });
            }
        });
    }
}