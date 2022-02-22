package com.example.pickcash.main.mine.kefu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pickcash.R;
import com.zcolin.frame.app.BaseFrameActivity;

public class KefuActivity extends BaseFrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kefu);
        findViewById(R.id.kefu_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView phoneNum = findViewById(R.id.kefu_phone_num);
        phoneNum.setText(getIntent().getStringExtra("phone"));
        TextView mailbox = findViewById(R.id.kefu_mailbox);
        mailbox.setText(getIntent().getStringExtra("email"));

        findViewById(R.id.kefu_phone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum.getText()));
                startActivity(dialIntent);
            }
        });
        findViewById(R.id.kefu_mailbox_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:" + mailbox.getText()));
                startActivity(data);
            }
        });
    }
}