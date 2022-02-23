package com.example.pickcash.main.mine.us;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickcash.R;
import com.zcolin.frame.app.BaseFrameActivity;

public class AboutUsActivity extends BaseFrameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        findViewById(R.id.us_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout checkVersion = findViewById(R.id.us_check_version);
        checkVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "Already the latest version", Toast.LENGTH_SHORT).show();
            }
        });
        TextView version = findViewById(R.id.us_version);
        version.setText(getIntent().getStringExtra("version"));
        ImageView redPoint = findViewById(R.id.us_red_point);
    }
}