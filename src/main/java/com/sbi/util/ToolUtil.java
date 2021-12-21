package com.sbi.util;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToolUtil {

    public static String strDateAdd(String strDate,int days) throws ParseException {
        Date date = DateUtils.parseDate(strDate,new String[]{"yyyyMMdd"});
        Date date1 = DateUtils.addDays(date, days);
        return DateFormatUtils.format(date1,"yyyyMMdd");
    }

    public static int strDateDecrement(String strDate1,String strDate2) throws ParseException {
        Date date1 = DateUtils.parseDate(strDate1,new String[]{"yyyyMMdd"});
        Date date2 = DateUtils.parseDate(strDate2,new String[]{"yyyyMMdd"});

        return (int) ((date1.getTime() - date2.getTime()) / (1000*3600*24));
    }

    public static boolean strDateAfter(String strDate1,String strDate2) throws ParseException {
        Date date1 = DateUtils.parseDate(strDate1,new String[]{"yyyyMMdd"});
        Date date2 = DateUtils.parseDate(strDate2,new String[]{"yyyyMMdd"});

        if (date1.after(date2)){
            return true;
        }else {
            return false;
        }
    }

    public static boolean strDateBefore(String strDate1,String strDate2) throws ParseException {
        Date date1 = DateUtils.parseDate(strDate1,new String[]{"yyyyMMdd"});
        Date date2 = DateUtils.parseDate(strDate2,new String[]{"yyyyMMdd"});

        if (date1.before(date2)){
            return true;
        }else {
            return false;
        }
    }


    public static <T> List<List<T>> splitList(List<T> source, int n) {

        if (null == source || source.size() == 0 || n <= 0)
            return null;
        List<List<T>> result = new ArrayList<List<T>>();

        int sourceSize = source.size();
        int size = (source.size() / n) + 1;
        for (int i = 0; i < size; i++) {
            List<T> subset = new ArrayList<T>();
            for (int j = i * n; j < (i + 1) * n; j++) {
                if (j < sourceSize) {
                    subset.add(source.get(j));
                }
            }
            result.add(subset);
        }
        return result;
    }
}
