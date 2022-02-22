package com.example.pickcash.main.home.mgr.entity;

public class AppInfoApply {
    public String appType = "";// system/normal
    public String flags = "";
    public long installTime = 0;
    public String name = "";
    public String packageName = "";
    public String packagePath = "";
    public long updateTime = 0;
    public String versionCode = "";
    public String versionName = "";

    public AppInfoApply() { }

    public AppInfoApply(String appType, String flags, long installTime, String name, String packageName, String packagePath, long updateTime, String versionCode, String versionName) {
        this.appType = appType;
        this.flags = flags;
        this.installTime = installTime;
        this.name = name;
        this.packageName = packageName;
        this.packagePath = packagePath;
        this.updateTime = updateTime;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }
}
