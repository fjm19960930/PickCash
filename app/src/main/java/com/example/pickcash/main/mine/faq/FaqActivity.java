package com.example.pickcash.main.mine.faq;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.R;
import com.zcolin.frame.app.BaseFrameActivity;

import java.util.ArrayList;

public class FaqActivity extends BaseFrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        findViewById(R.id.faq_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ArrayList<FaqEntity> data = new ArrayList<>();
        data.add(new FaqEntity("1.How to apply for a loan?", "All it takes is 10 minutes to fill out the information required for the loan, submit it to the system, and the system notifiesyou of the result within 24 hours."));
        data.add(new FaqEntity("1.How to apply for a loan?", "All it takes is 10 minutes to fill out the information required for the loan, submit it to the system, and the system notifiesyou of the result within 24 hours."));
        data.add(new FaqEntity("1.How to apply for a loan?", "All it takes is 10 minutes to fill out the information required for the loan, submit it to the system, and the system notifiesyou of the result within 24 hours."));
        RecyclerView faqRecyclerView = findViewById(R.id.faq_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        faqRecyclerView.setLayoutManager(linearLayoutManager);
        FaqAdapter adapter = new FaqAdapter(data);
        faqRecyclerView.setAdapter(adapter);
    }
}