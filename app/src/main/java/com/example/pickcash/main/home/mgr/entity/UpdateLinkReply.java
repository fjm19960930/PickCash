package com.example.pickcash.main.home.mgr.entity;

import com.example.pickcash.util.BaseReply;

public class UpdateLinkReply extends BaseReply {
    public UpdateLink data;
    public class UpdateLink {
        public String link;
        public String shortLink;
    }
}
