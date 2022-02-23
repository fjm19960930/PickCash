package com.example.pickcash.util;

import com.zcolin.frame.http.ZReply;

public class BaseLogReply implements ZReply{
    @Override
    public boolean isSuccess() {
        return code == 204 || code == 200 || code == 0;
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
