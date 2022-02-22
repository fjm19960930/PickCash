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
import com.example.pickcash.main.PickCashMainActivity;
import com.example.pickcash.main.home.loan.verify.mgr.VerifyMgr;
import com.example.pickcash.main.home.loan.verify.mgr.entity.PersonalInfoApply;
import com.example.pickcash.main.home.loan.verify.mgr.entity.PersonalInfoFieldsReply;
import com.example.pickcash.main.home.loan.verify.view.PersonalContactItemView;
import com.example.pickcash.main.home.loan.verify.view.PersonalInfoItemView;
import com.example.pickcash.util.NumberUtils;
import com.zcolin.frame.app.BaseFrameActivity;

import java.util.ArrayList;
import java.util.List;

public class PersonalInfoActivity extends BaseFrameActivity {

    private ArrayList<PersonalInfoItemView> infoItemViews = new ArrayList<>();
    private ArrayList<PersonalInfoItemView> extraInfoItemViews = new ArrayList<>();
    private ArrayList<String> extraFieldNames = new ArrayList<>();
    private ArrayList<PersonalContactItemView> contactItemViews = new ArrayList<>();
    private ArrayList<String> phoneNumbers = new ArrayList<>();
    private int contactPosition;

    private PersonalContactItemView.ContactClickListener mListener = position -> {
        contactPosition = position;
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, CONTACT_REQUEST_CODE);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        findViewById(R.id.personal_back_btn).setOnClickListener(v -> {
            startActivity(new Intent(mActivity, PickCashMainActivity.class));
            finish();
        });

        LinearLayout basicLayout = findViewById(R.id.personal_basic_layout);
        PersonalInfoItemView piiv1 = new PersonalInfoItemView(mActivity, "Email", false, null, true);
        basicLayout.addView(piiv1);
        infoItemViews.add(piiv1);
        PersonalInfoItemView piiv2 = new PersonalInfoItemView(mActivity, "Language", false, null, true);
        basicLayout.addView(piiv2);
        infoItemViews.add(piiv2);
        PersonalInfoItemView piiv3 = new PersonalInfoItemView(mActivity, "Marital status", true, new String[]{"Single", "Married", "Divorced", "Widowed"}, true);
        basicLayout.addView(piiv3);
        infoItemViews.add(piiv3);

        LinearLayout workPositionLayout = findViewById(R.id.personal_work_layout);

        LinearLayout contactLayout = findViewById(R.id.personal_contact_layout);

        TextView nextBtn = findViewById(R.id.personal_next);
        nextBtn.setOnClickListener(v -> {
            if (!infoItemViews.get(0).isEmail()) {
                Toast.makeText(mActivity, "Please enter your valid E-mail account", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < infoItemViews.size(); i++) {
                if (!infoItemViews.get(i).hasFilled()) {
                    Toast.makeText(mActivity, "Please complete the " + infoItemViews.get(i).getTitle() + " information", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            for (int i = 0; i < contactItemViews.size(); i++) {
                if (!contactItemViews.get(i).hasFilled()) {
                    Toast.makeText(mActivity, "Please complete the contact information", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            PersonalInfoApply apply = new PersonalInfoApply();
            for (int i = 0; i < infoItemViews.size(); i++) {
                PersonalInfoItemView infoItemView = infoItemViews.get(i);
                switch (infoItemView.getTitle()) {
                    case "Email":
                        apply.email = infoItemView.getEnterStr();
                        break;
                    case "Language":
                        apply.languages = infoItemView.getEnterStr();
                        break;
                    case "Marital status":
                        apply.marry = infoItemView.getChooseBtn().getText().toString();
                        break;
                    case "Monthly income":
                        if (!NumberUtils.isNumeric(infoItemView.getEnterStr())) {
                            Toast.makeText(mActivity, "Please enter the correct monthly income", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        apply.monthlySalary = Integer.parseInt(infoItemView.getEnterStr());
                        break;
                    case "Work type":
                        apply.workType = infoItemView.getChooseBtn().getText().toString();
                        break;
                    default:
                        break;
                }
            }
            ArrayList<PersonalInfoApply.Field> fieldList = new ArrayList<>();
            for (int i = 0; i < extraInfoItemViews.size(); i++) {
                PersonalInfoApply.Field field = new PersonalInfoApply.Field();
                field.fieldName = extraFieldNames.get(i);
                field.values = extraInfoItemViews.get(i).getEnterStr();
                fieldList.add(field);
            }
            apply.fields = fieldList;
            ArrayList<PersonalInfoApply.Contact> contactsList = new ArrayList<>();
            for (int i = 0; i < contactItemViews.size(); i++) {
                PersonalInfoApply.Contact contact = new PersonalInfoApply.Contact();
                contact.relation = contactItemViews.get(i).getRelationChoose().getText().toString();
                contact.name = contactItemViews.get(i).getName();
                contact.phone = contactItemViews.get(i).getPhone();
                contactsList.add(contact);
            }
            apply.contacts = contactsList;

            VerifyMgr.submitPersonalInfo(apply, new VerifyMgr.SubmitBaseInfoListener() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(mActivity, VerifyBankCardActivity.class);
                    startActivity(intent);
//                    Toast.makeText(mActivity, "success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int code, String errorMsg) {

                }
            });
        });

        VerifyMgr.getPersonalInfoFields(new VerifyMgr.GetPersonalInfoFieldsListener() {
            @Override
            public void onSuccess(int contactCount, List<PersonalInfoFieldsReply.Fields> list) {
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        PersonalInfoItemView infoItemView = new PersonalInfoItemView(mActivity, list.get(i).frontName, false, null, list.get(i).required == 1);
                        infoItemViews.add(infoItemView);
                        extraInfoItemViews.add(infoItemView);
                        extraFieldNames.add(list.get(i).fieldName);
                        basicLayout.addView(infoItemView);
                    }
                }
                PersonalInfoItemView piiv4 = new PersonalInfoItemView(mActivity, "Monthly income", false, null, true);
                workPositionLayout.addView(piiv4);
                infoItemViews.add(piiv4);
                PersonalInfoItemView piiv5 = new PersonalInfoItemView(mActivity, "Work type", true, new String[]{"Employee", "SelfEmployed", "NoWork"}, true);
                workPositionLayout.addView(piiv5);
                infoItemViews.add(piiv5);
                if (contactCount > 0) {
                    for (int i = 0; i < contactCount; i++) {
                        PersonalContactItemView contactItemView = new PersonalContactItemView(mActivity, i, mListener);
                        contactItemViews.add(contactItemView);
                        contactLayout.addView(contactItemView);
                    }
                }
            }

            @Override
            public void onError(int code, String errorMsg) {

            }
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
                        if (!phoneNumbers.isEmpty()) {
                            for (int i = 0; i < phoneNumbers.size(); i++) {
                                if (phone.equals(phoneNumbers.get(i))) {
                                    Toast.makeText(mActivity, "This emergency contact No had been used before, please choose a new one", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        phoneNumbers.add(phone);
                        contactItemViews.get(contactPosition).setPhoneNum(name, phone);
                    } catch (Exception e) {

                    }
                }
                break;
            default:
                break;
        }
    }
}