package com.example.pickcash.main.home.loan.verify;

import static com.example.pickcash.PickCashApplication.CONTACT_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickcash.R;
import com.example.pickcash.main.home.loan.apply.LoanApplyTestActivity;
import com.example.pickcash.main.home.loan.verify.view.PersonalContactItemView;
import com.example.pickcash.main.home.loan.verify.view.PersonalInfoItemView;
import com.zcolin.frame.app.BaseFrameActivity;

public class PersonalInfoTestActivity extends BaseFrameActivity {

    private PersonalContactItemView.ContactClickListener mListener = position -> {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, CONTACT_REQUEST_CODE);
    };
    private PersonalContactItemView contactItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_test);

        findViewById(R.id.personal_test_back_btn).setOnClickListener(v -> {
            finish();
        });

        LinearLayout basicLayout = findViewById(R.id.personal_test_basic_layout);
        PersonalInfoItemView piiv1 = new PersonalInfoItemView(mActivity, "Aadhaar_name", false, null, true);
        basicLayout.addView(piiv1);
        PersonalInfoItemView piiv2 = new PersonalInfoItemView(mActivity, "Aadhaar_number", false, null, true);
        basicLayout.addView(piiv2);
        PersonalInfoItemView piiv3 = new PersonalInfoItemView(mActivity, "Pan_number", false, null, true);
        basicLayout.addView(piiv3);
        PersonalInfoItemView piiv4 = new PersonalInfoItemView(mActivity, "Pan_name", false, null, true);
        basicLayout.addView(piiv4);
        PersonalInfoItemView piiv5 = new PersonalInfoItemView(mActivity, "Pin_code", false, null, true);
        basicLayout.addView(piiv5);
        PersonalInfoItemView piiv6 = new PersonalInfoItemView(mActivity, "WhatsApp", false, null, true);
        basicLayout.addView(piiv6);
        PersonalInfoItemView piiv7 = new PersonalInfoItemView(mActivity, "Company_name", false, null, true);
        basicLayout.addView(piiv7);
        PersonalInfoItemView piiv8 = new PersonalInfoItemView(mActivity, "Religion", false, null, true);
        basicLayout.addView(piiv8);

        LinearLayout contactLayout = findViewById(R.id.personal_test_contact_layout);
        contactItemView = new PersonalContactItemView(mActivity, 0, mListener);
        contactLayout.addView(contactItemView);

        TextView nextBtn = findViewById(R.id.personal_test_next);
        nextBtn.setOnClickListener(v -> {
            if (!piiv1.hasFilled() || !piiv2.hasFilled() || !piiv3.hasFilled() || !piiv4.hasFilled() || !piiv5.hasFilled() ||
                    !piiv6.hasFilled() || !piiv7.hasFilled() || !piiv8.hasFilled() || !contactItemView.hasFilled()) {
                Toast.makeText(mActivity, "Please complete the information", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, LoanApplyTestActivity.class);
            startActivity(intent);
        });
    }


    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (CONTACT_REQUEST_CODE) :
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data == null) {
                            return;
                        }
                        Uri result = data.getData();
                        String contactId = result.getLastPathSegment();
                        String name = "";
                        String phone = "";

                        //得到名字
                        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
                        Cursor cursor = mActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                                projection,
                                ContactsContract.Contacts._ID + " = ?",
                                new String[]{contactId}, // where values
                                ContactsContract.Contacts.DISPLAY_NAME);
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                            if (index >= 0) {
                                name = cursor.getString(index);
                            }
                        }

                        //得到电话
                        projection = new String[]{ContactsContract.Contacts.NAME_RAW_CONTACT_ID, ContactsContract.Contacts.HAS_PHONE_NUMBER};
                        cursor = mActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, // where sentence
                                null,
                                null);
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            if (index >= 0) {
                                phone = cursor.getString(index);
                            }
                        }
                        contactItemView.setPhoneNum(name, phone);
                    } catch (Exception e) {

                    }
                }
                break;
            default:
                break;
        }
    }
}