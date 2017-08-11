package com.yunkyun.piececollector.object;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by YunKyun on 2017-08-10.
 */

public class HistoryDivider implements Comparable<HistoryDivider> {
    private int year;
    private int month;
    private int day;

    public HistoryDivider(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int compareTo(HistoryDivider o) {
        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, day);
        Calendar c2 = Calendar.getInstance();
        c2.set(o.getYear(), o.getMonth(), o.getDay());
        return c1.compareTo(c2);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
