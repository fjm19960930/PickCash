package com.example.pickcash.util;

import static android.content.Context.TELEPHONY_SERVICE;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static Boolean isEmail(String str) {
        try {
            boolean isEmail = false;
            String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            if (str.matches(expr)) {
                isEmail = true;
            }
            return isEmail;
        } catch (Exception e) {
            return false;
        }
    }

    public static String moneyFormat(int money) {
        try {
            String str = String.valueOf(money);
            StringBuilder builder = new StringBuilder(str);
            str = builder.reverse().toString();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                if (i % 3 == 0) {
                    //防越界&保留最高位
                    if (i + 3 > str.length()) {
                        stringBuilder.append(str.substring(i));
                        break;
                    }
                    stringBuilder.append(str.substring(i, i + 3) + ",");
                }
            }
            str = stringBuilder.reverse().toString();
            //消除字符串长度为3的倍数时多出的','
            if (str.charAt(0) == ',') {
                str = str.substring(1);
            }
            return "₹" + str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getDateOffset(int day1, int day2) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            long m = simpleDateFormat.parse(String.valueOf(day2)).getTime() - simpleDateFormat.parse(String.valueOf(day1)).getTime();
            return m / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String dateFormat(int date) {
        try {
            String str = String.valueOf(date);
            String year = str.substring(0, 4);
            String month = str.substring(4, 6);
            String day = str.substring(6);
            switch (month) {
                case "01":
                    month = "JAN";
                    break;
                case "02":
                    month = "FEB";
                    break;
                case "03":
                    month = "MAR";
                    break;
                case "04":
                    month = "APR";
                    break;
                case "05":
                    month = "MAY";
                    break;
                case "06":
                    month = "JUN";
                    break;
                case "07":
                    month = "JUL";
                    break;
                case "08":
                    month = "AUG";
                    break;
                case "09":
                    month = "SEP";
                    break;
                case "10":
                    month = "OCT";
                    break;
                case "11":
                    month = "NOV";
                    break;
                case "12":
                    month = "DEC";
                    break;
                default:
                    break;
            }
            return month + " " + day + "," + year;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIMEI(Context context) {
        String imei = "";
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imei = tm.getDeviceId();
                } else {
                    Method method = tm.getClass().getMethod("getImei");
                    imei = (String) method.invoke(tm);
                }
            } else {
                imei = Settings.System.getString(
                        context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }
}
