package com.example.pickcash.main.home.mgr.entity;

import com.example.pickcash.util.BaseReply;

import java.util.List;

public class SdkKeyReply extends BaseReply {
    public SdkData data;

    public class SdkData {
        public List<String> keys;
        public String type;
    }
}
