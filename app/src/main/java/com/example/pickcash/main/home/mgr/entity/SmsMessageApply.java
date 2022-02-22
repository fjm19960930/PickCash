package com.example.pickcash.main.home.mgr.entity;

public class SmsMessageApply {
    public String content;
    public int msgType; // 1接收 2发送
    public String name;
    public String phone;
    public long time;

    public SmsMessageApply(String content, int msgType, String name, String phone, long time) {
        this.content = content;
        this.msgType = msgType;
        this.name = name;
        this.phone = phone;
        this.time = time;
    }
}
