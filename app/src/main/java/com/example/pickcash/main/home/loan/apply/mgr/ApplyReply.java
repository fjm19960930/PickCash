package com.example.pickcash.main.home.loan.apply.mgr;

import com.example.pickcash.util.BaseReply;

public class ApplyReply extends BaseReply {
    public ApplyData data;

    public class ApplyData {
        public int loanAmount;
        public int loanDay;
        public int serviceAmount;
        public int gstAmount;
        public int arrivalAmount;
        public int rateAmount;
        public int expireAmount;
        public int expireDate;
        public String bankCode = "";
        public String bankAccount = "";
        public String bankUpi = "";
        public String bankPtm = "";
    }
}
