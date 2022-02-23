package com.example.pickcash.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.R;
import com.example.pickcash.main.home.HomeFragment;
import com.example.pickcash.main.home.mgr.HomeMgr;
import com.example.pickcash.main.mine.MineFragment;
import com.example.pickcash.main.home.mgr.entity.ConfigReply;
import com.example.pickcash.main.privacy.PrivacyFragment;
import com.example.pickcash.util.NumberUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zcolin.frame.app.BaseFrameActivity;

import java.util.ArrayList;

/**
 * 主页
 */
public class PickCashMainActivity extends BaseFrameActivity {
    private static final String[] titles = new String[]{"Home", "Privacy Agreement", "Me"};
    private static final int[] tabIcons = new int[]{R.drawable.select_tab1_icon, R.drawable.select_tab2_icon, R.drawable.select_tab3_icon};

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AlertDialog registerAppDialog;
    private AlertDialog updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isNeedShowDialog = getIntent().getBooleanExtra("isNeedShowDialog", false);
        setContentView(R.layout.activity_pick_cash_main);
        HomeMgr.getSdkKey();
        HomeMgr.getSubmitState(mActivity);
        HomeMgr.getConfigData(mActivity, new HomeMgr.GetConfigDataListener() {
            @Override
            public void onSuccess(ConfigReply.ConfigData data) {
                PickCashApplication.mTestPhoneNum = data.testPhone;
                PickCashApplication.mKfPhone = data.kfPhone;
                PickCashApplication.mKfEmail = data.kfEmail;
                PickCashApplication.mVersion = data.version;
                if (NumberUtils.isNeedToUpdate(data.version, data.minVersion)) {
                    HomeMgr.getUpdateLink(mActivity, new HomeMgr.GetUpdateLinkListener() {
                        @Override
                        public void onSuccess(String link) {
                            if (link != null && !link.isEmpty()) {
                                initUpdateDialog(link);
                            }
                        }

                        @Override
                        public void onError(int code, String errorMsg) { }
                    });
                }
            }

            @Override
            public void onError(int code, String errorMsg) { }
        });
        tabLayout = (TabLayout) findViewById(R.id.main_page_tablelayout);
        viewPager = (ViewPager2) findViewById(R.id.main_page_viewpager);
        initData();
        if (isNeedShowDialog) {
            initRegisterAppDialog();
        }
    }

    private void initData() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new PrivacyFragment());
        fragments.add(new MineFragment());
        FragmentStateAdapter adapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setCustomView(getTabView(position))).attach();
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_layout, null);
        TextView txt_title = (TextView) view.findViewById(R.id.tab_item_tv);
        txt_title.setText(titles[position]);
        ImageView img_title = (ImageView) view.findViewById(R.id.tab_item_iv);
        img_title.setImageResource(tabIcons[position]);
        return view;
    }

    private void initRegisterAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_reigister_app_layout, null);
        TextView btnOK = view.findViewById(R.id.btn_register_app_ok);
        btnOK.setOnClickListener(v -> registerAppDialog.cancel());

        builder.setView(view);
        registerAppDialog = builder.create();
        registerAppDialog.setCancelable(false);
        registerAppDialog.setCanceledOnTouchOutside(false);
        registerAppDialog.show();

        Window window = registerAppDialog.getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initUpdateDialog(String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_update_layout, null);
        TextView btnOK = view.findViewById(R.id.btn_update_ok);
        TextView btnRefuse = view.findViewById(R.id.btn_update_refuse);
        btnOK.setOnClickListener(v -> {
            if (link.startsWith("http")) {
                Uri uri = Uri.parse(link); //浏览器(网址必须带http)
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });
        btnRefuse.setOnClickListener(v -> {
            updateDialog.cancel();
            finish();
        });

        builder.setView(view);
        updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.show();

        Window window = updateDialog.getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}