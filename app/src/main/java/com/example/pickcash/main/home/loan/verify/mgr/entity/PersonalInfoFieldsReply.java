package com.example.pickcash.main.home.loan.verify.mgr.entity;

import com.example.pickcash.util.BaseReply;

import java.util.List;

public class PersonalInfoFieldsReply extends BaseReply {
    public Data data;

    public static class Data {
        public int contactCount;
        public List<Fields> fields;
    }

    public static class Fields {
        public String fieldName;//传输时使用的名称
        public String frontName;//显示的名称
        public String backName;
        public int required;//是否必填1=必填,0=非必填
    }
}
