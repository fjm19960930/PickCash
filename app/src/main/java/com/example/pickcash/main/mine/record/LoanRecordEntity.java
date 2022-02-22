package com.example.pickcash.main.mine.record;

public class LoanRecordEntity {
    public String date;
    public String state;
    public String money;
    public boolean repayBtn;
    public String receiptDate;

    public LoanRecordEntity(String date, String state, String money, boolean repayBtn, String receiptDate) {
        this.date = date;
        this.state = state;
        this.money = money;
        this.repayBtn = repayBtn;
        this.receiptDate = receiptDate;
    }
}
