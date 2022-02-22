package com.example.pickcash.main.home.loan.verify.mgr.entity;

import java.util.List;

public class PersonalInfoApply {
    public String email = "";
    public String workType = "";
    public String languages = "";
    public String culturalLevel = "";
    public String religion = "";
    public String marry = "";
    public int monthlySalary;
    public List<Field> fields;
    public List<Contact> contacts;

    public static class Field {
        public String fieldName = "";
        public String values = "";
    }

    public static class Contact {
        public String relation = "";
        public String name = "";
        public String phone = "";
    }

}
