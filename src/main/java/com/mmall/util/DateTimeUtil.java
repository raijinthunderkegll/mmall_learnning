package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String dateToString(Date date,String format){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static String dateToString(Date date){
        return dateToString(date,STANDARD_FORMAT);
    }

    public static Date stringToDate(String dateString,String format){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateString);
        return dateTime.toDate();
    }

    public static Date stringToDate(String dateString){
        return stringToDate(dateString,STANDARD_FORMAT);
    }
}
