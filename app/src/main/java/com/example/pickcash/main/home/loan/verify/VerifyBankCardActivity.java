package com.example.pickcash.main.home.loan.verify;

import static com.example.pickcash.PickCashApplication.IFSC_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickcash.R;
import com.example.pickcash.main.PickCashMainActivity;
import com.example.pickcash.main.home.loan.apply.LoanApplyActivity;
import com.example.pickcash.main.home.loan.verify.mgr.VerifyMgr;
import com.zcolin.frame.app.BaseFrameActivity;

public class VerifyBankCardActivity extends BaseFrameActivity {

    private EditText ifscEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_bank_card);

        findViewById(R.id.bank_card_back_btn).setOnClickListener(v -> {
            startActivity(new Intent(mActivity, PickCashMainActivity.class));
            finish();
        });

        EditText pIdEdit = findViewById(R.id.bank_card_p_id_enter);
        EditText uIdEdit = findViewById(R.id.bank_card_u_id_enter);
        EditText accountEdit  = findViewById(R.id.bank_card_account_enter);
        ifscEdit = findViewById(R.id.bank_card_ifsc_enter);
        TextView getIfscBtn = findViewById(R.id.bank_card_get_ifsc_btn);
        getIfscBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, VerifyGetIfscActivity.class);
                startActivityForResult(i, IFSC_REQUEST_CODE);
            }
        });
        TextView submitBtn = findViewById(R.id.bank_card_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pIdEdit.getText().length() == 0) {
                    Toast.makeText(mActivity, "Please complete the Paytm ID information", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uIdEdit.getText().length() == 0) {
                    Toast.makeText(mActivity, "Please complete the UPI ID information", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (accountEdit.getText().length() == 0) {
                    Toast.makeText(mActivity, "Please complete the Bank account information", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ifscEdit.getText().length() == 0) {
                    Toast.makeText(mActivity, "Please complete the IFSC information", Toast.LENGTH_SHORT).show();
                    return;
                }
                VerifyMgr.submitBankCardInfo(pIdEdit.getText().toString(), uIdEdit.getText().toString(),
                        accountEdit.getText().toString(), ifscEdit.getText().toString(),
                        new VerifyMgr.SubmitBankCardInfoListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(mActivity, LoanApplyActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(int code, String errorMsg) {

                    }
                });
            }
        });
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (IFSC_REQUEST_CODE) :
                if (resultCode == Activity.RESULT_OK && ifscEdit != null) {
                    ifscEdit.setText(data.getStringExtra("ifsc"));
                }
                break;
            default:
                break;
        }
    }
}