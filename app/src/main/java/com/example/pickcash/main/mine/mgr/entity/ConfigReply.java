package com.example.pickcash.main.mine.mgr.entity;

import com.example.pickcash.util.BaseReply;

public class ConfigReply extends BaseReply {
    public ConfigData data;
    public class ConfigData {
        public String kfPhone;
        public String minVersion;
        public String repaymentLink;
        public String kfEmail;
        public String testPhone;
        public String version;
    }
}
