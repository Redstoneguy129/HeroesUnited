package xyz.heroesunited.heroesunited.util;

import java.util.Calendar;
import java.util.Date;

public class HUCalendarHelper {

    public static boolean isAprilFoolsDay() {
        return getCalendar().get(Calendar.MONTH) == Calendar.APRIL && getCalendar().get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static boolean isSnowTime() {
        boolean decemberHolidays = getCalendar().get(Calendar.MONTH) == Calendar.DECEMBER && getCalendar().get(Calendar.DAY_OF_MONTH) > 24;
        boolean januaryHolidays = getCalendar().get(Calendar.MONTH) == Calendar.JANUARY && getCalendar().get(Calendar.DAY_OF_MONTH) < 15;
        return decemberHolidays || januaryHolidays;
    }

    public static boolean isHalloween() {
        return getCalendar().get(Calendar.MONTH) == Calendar.OCTOBER && getCalendar().get(Calendar.DAY_OF_MONTH) == 31;
    }

    private static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

}
