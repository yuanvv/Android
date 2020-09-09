package com.pachain.android.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolPackage {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return  false;
        }
    }

    public static String getDateNow() {
        Date currentTime = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(currentTime);
    }

    public static String ConvertToStringNow() {
        Date currentTime = new java.util.Date();
        if (currentTime.getMonth() == 4) {
            DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
            return df.format(currentTime);
        } else {
            DateFormat df = new SimpleDateFormat("MMM. d, yyyy", Locale.ENGLISH);
            return df.format(currentTime);
        }
    }

    public static String ConvertToStringByTime(String timestamp) {
        Date currentTime = new Date(Long.valueOf(timestamp));
        if (currentTime.getMonth() == 4) {
            DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
            return df.format(currentTime);
        } else {
            DateFormat df = new SimpleDateFormat("MMM. d, yyyy", Locale.ENGLISH);
            return df.format(currentTime);
        }
    }

    public static String ConvertToSimpleStringByTime(String timestamp) {
        Date time = new Date(Long.valueOf(timestamp));
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(time);
    }

    public static String ConvertToStringByDate(String strDate) {
        if (strDate.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(strDate);
                if (date.getMonth() == 4) {
                    DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                    return df.format(date);
                } else {
                    DateFormat df = new SimpleDateFormat("MMM. d, yyyy", Locale.ENGLISH);
                    return df.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return strDate;
            }
        }
        else {
            return "";
        }
    }

    public static String ConvertToCommonStringByDate(String strDate) {
        if (strDate.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
            Date date = null;
            try {
                date = sdf.parse(strDate);

                sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return strDate;
            }
        }
        else {
            return "";
        }
    }

    public static boolean ComparedDateWithNow(String strDate) {
        if (strDate.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(strDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(calendar.DATE, 1);
                date = calendar.getTime();

                Date currentTime = new java.util.Date();
                if (currentTime.getTime() < date.getTime()) {
                    return true;
                } else {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static String decimalFormat(int data) {
        DecimalFormat df = new DecimalFormat();
        String style = "###,###,###,###,###,###";
        df.applyPattern(style);
        return df.format(data);
    }

    public static String doubleFormat(double data) {
        int intData = (int) data;
        String returnData = "";
        if (data > intData) {
            DecimalFormat df = new DecimalFormat("######0.00");
            returnData = df.format(data);
        } else {
            returnData = String.valueOf(intData);
        }
        return returnData;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isEmail(String email) {
        email = email.toLowerCase();
        String str = "^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
