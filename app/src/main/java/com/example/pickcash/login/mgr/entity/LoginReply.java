package com.example.pickcash.login.mgr.entity;

import com.example.pickcash.util.BaseReply;

public class LoginReply extends BaseReply {
    public Data data;

    public static class Data {
        public String token;
        public String registerApp;
    }
}
