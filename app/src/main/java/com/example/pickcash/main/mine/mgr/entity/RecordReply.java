package com.example.pickcash.main.mine.mgr.entity;

import com.example.pickcash.util.BaseReply;

import java.util.List;

public class RecordReply extends BaseReply {
    public Data data;
    public static class Data {
        String total;
        public List<RecordData> list;
    }
    public static class RecordData {
        public String orderNum;
        public String applyTime;
        public int loanAmount;
        public int loanDay;
        public String orderStatus;
        public int arrivalAmount;
        public int loanSuccesDate;
        public int expireDate;
        public int repaymentDate;
        public int currDate;
        public int repaymentAmount;
    }
}
