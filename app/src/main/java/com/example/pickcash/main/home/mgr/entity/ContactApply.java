package com.example.pickcash.main.home.mgr.entity;

public class ContactApply {
    public String contactName;
    public int contactTimes;
    public long createTime;
    public long lastContactTime;
    public String phoneNumber;
    public long updateTime;

    public ContactApply(String contactName, int contactTimes, long createTime, long lastContactTime, String phoneNumber, long updateTime) {
        this.contactName = contactName;
        this.contactTimes = contactTimes;
        this.createTime = createTime;
        this.lastContactTime = lastContactTime;
        this.phoneNumber = phoneNumber;
        this.updateTime = updateTime;
    }
}
