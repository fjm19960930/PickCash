package com.example.pickcash.main.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.R;
import com.example.pickcash.main.home.loan.state.LoanStateActivity;
import com.example.pickcash.main.home.loan.verify.PersonalInfoTestActivity;
import com.example.pickcash.main.home.loan.verify.VerifyActivity;
import com.example.pickcash.main.home.loan.verify.VerifyBankCardActivity;
import com.example.pickcash.main.home.mgr.HomeMgr;
import com.example.pickcash.main.mine.kefu.KefuActivity;
import com.example.pickcash.main.mine.mgr.MineMgr;
import com.example.pickcash.main.mine.record.LoanItemDetailActivity;
import com.zcolin.frame.util.SpUtil;

public class HomeFragment extends Fragment {

    private LinearLayout applyLayout;
    private LinearLayout repayLayout;
    private TextView applyBtn;
    private TextView repayBtn;
    private TextView payDate;
    private TextView payMoney;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.main_page_kefu_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), KefuActivity.class);
                startActivity(intent);
            }
        });
        applyLayout = view.findViewById(R.id.home_apply_layout);
        applyBtn = view.findViewById(R.id.home_apply_btn);
        repayLayout = view.findViewById(R.id.home_repay_layout);
        repayBtn = view.findViewById(R.id.home_repay_btn);
        payDate = view.findViewById(R.id.home_need_pay_date);
        payMoney = view.findViewById(R.id.home_need_pay_money);

        //测试页面
        if (PickCashApplication.mTestPhoneNum.equals(PickCashApplication.mPhoneNum)) {
            applyLayout.setVisibility(View.VISIBLE);
            repayLayout.setVisibility(View.GONE);
            applyBtn.setText("APPLY");
            applyBtn.setOnClickListener(v -> {
                if (SpUtil.getBoolean("test_loan_state", false)) {
                    Intent intent = new Intent(requireActivity(), LoanStateActivity.class);
                    intent.putExtra("state", "check_processing");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(requireActivity(), PersonalInfoTestActivity.class);
                    startActivity(intent);
                }
            });
            return view;
        }

        //正常流程
        HomeMgr.getLoanState(requireActivity(), new HomeMgr.GetLoanStateListener() {
            @Override
            public void onSuccess(String state) {
                switch (state) {
                    case "INWAIT":
                        getOrderList();
                        break;
                    case "WAITAPPLY":
                        applyLayout.setVisibility(View.VISIBLE);
                        repayLayout.setVisibility(View.GONE);
                        applyBtn.setText("APPLY");
                        applyBtn.setOnClickListener(v -> {
                            Intent intent = new Intent(requireActivity(), VerifyActivity.class);
                            startActivity(intent);
                        });
                        break;
                    case "BANAPPLY":
                        applyLayout.setVisibility(View.VISIBLE);
                        repayLayout.setVisibility(View.GONE);
                        applyBtn.setText("BAN APPLY");
                        applyBtn.setOnClickListener(v -> { });
                        break;
                    case "AUDITING":
                        applyLayout.setVisibility(View.VISIBLE);
                        repayLayout.setVisibility(View.GONE);
                        applyBtn.setText("APPLY");
                        applyBtn.setOnClickListener(v -> {
                            Intent intentAUDITING = new Intent(requireActivity(), LoanStateActivity.class);
                            intentAUDITING.putExtra("state", "check_processing");
                            startActivity(intentAUDITING);
                        });
                        break;
                    case "BANKERROR":
                        applyLayout.setVisibility(View.VISIBLE);
                        repayLayout.setVisibility(View.GONE);
                        applyBtn.setText("APPLY");
                        applyBtn.setOnClickListener(v -> {
                            Intent intentBANKERROR = new Intent(requireActivity(), VerifyBankCardActivity.class);
                            startActivity(intentBANKERROR);
                        });
                        break;
                    case "OUTING":
                        applyLayout.setVisibility(View.VISIBLE);
                        repayLayout.setVisibility(View.GONE);
                        applyBtn.setText("APPLY");
                        applyBtn.setOnClickListener(v -> {
                            Intent intentOUTING = new Intent(requireActivity(), LoanStateActivity.class);
                            intentOUTING.putExtra("state", "lend_processing");
                            startActivity(intentOUTING);
                        });
                        break;
                }
            }

            @Override
            public void onError(int code, String errorMsg) {

            }
        });
        return view;
    }

    private void getOrderList() {
        HomeMgr.getOrderList(new HomeMgr.GetOrderListListener() {
            @Override
            public void onSuccess(boolean isCanLoan, String loanDate, String loanMoney) {
                if (isCanLoan) {
                    applyLayout.setVisibility(View.VISIBLE);
                    repayLayout.setVisibility(View.GONE);
                    applyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(requireActivity(), VerifyActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    applyLayout.setVisibility(View.GONE);
                    repayLayout.setVisibility(View.VISIBLE);
                    payDate.setText("Repayment date " + loanDate);
                    payMoney.setText(loanMoney.replace("₹", "") + "₹");
                    repayBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MineMgr.getRepayLink(new MineMgr.GetRepayLinkListener() {
                                @Override
                                public void onSuccess(String link) {
                                    Intent intent = new Intent(requireActivity(), LoanItemDetailActivity.class);
                                    intent.putExtra("link", link);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(int code, String errorMsg) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String errorMsg) {

            }
        });
    }
}