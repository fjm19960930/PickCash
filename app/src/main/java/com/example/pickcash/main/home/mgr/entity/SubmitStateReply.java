package com.example.pickcash.main.home.mgr.entity;

import com.example.pickcash.util.BaseReply;

public class SubmitStateReply extends BaseReply {
    public StateData data;
    public static class StateData{
        public int msm;//短信是否上传0=未上传、1=上传
        public int txl;//通讯录否上传0=未上传、1=上传
        public int app;//安装的app否上传0=未上传、1=上传
        public int alb;//相册数据否上传0=未上传、1=上传
    }
}
