package com.example.pickcash.main.home.loan.verify.mgr.entity;

import com.example.pickcash.util.BaseReply;

public class CardMessageReply extends BaseReply {
    public CardMessageData data;
    public static class CardMessageData {
        public CardMessageData() { }

        public String names = "";
        public String cardNumberId = "";
        public String sex = "";
        public String birthday = "";
        public String father = "";
        public String address = "";
    }
}
