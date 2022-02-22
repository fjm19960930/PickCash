package com.example.pickcash.util;

import com.zcolin.frame.http.ZReply;

public class BaseReply implements ZReply{
    @Override
    public boolean isSuccess() {
        return code == 200;
    }

    @Override
    public int getReplyCode() {
        return code;
    }

    @Override
    public String getErrorMessage() {
        return code + message;
    }

    public int code;
    public String message;
}
