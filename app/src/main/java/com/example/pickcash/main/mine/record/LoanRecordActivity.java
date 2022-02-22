package com.example.pickcash.main.mine.record;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.R;
import com.example.pickcash.main.mine.faq.FaqAdapter;
import com.example.pickcash.main.mine.faq.FaqEntity;
import com.example.pickcash.main.mine.mgr.MineMgr;
import com.zcolin.frame.app.BaseFrameActivity;

import java.util.ArrayList;

public class LoanRecordActivity extends BaseFrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_record);
        LinearLayout noLayout = findViewById(R.id.record_no_bills_layout);
        RecyclerView recyclerView = findViewById(R.id.record_rv);

        findViewById(R.id.record_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MineMgr.getLoanRecord(mActivity, new MineMgr.GetLoanRecordListener() {
            @Override
            public void onSuccess(ArrayList<LoanRecordEntity> data) {
                if (data.isEmpty()) {
                    noLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    LoanRecordAdapter adapter = new LoanRecordAdapter(mActivity, data);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onError(int code, String errorMsg) {
                noLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}