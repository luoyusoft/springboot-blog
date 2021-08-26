package com.jinhx.blog.common.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUtils
 *
 * @author jinhx
 * @since 2018-10-07
 */
public class DateUtils {

    /**
     * 对日期的分钟进行加/减
     * @param date
     * @param minutes
     * @return
     */
    public static long addDateMinutes(Date date, int minutes){
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        return calendar.getTime().getTime();
    }

    /**
     * 格式化日期
     * @param date date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取当前时间
     */
    public static String getNowDateTimeString() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * 获取当前时间
     */
    public static Long getNowTimeLong() {
        return Instant.now().toEpochMilli();
    }

    /**
     * 获取当前时间
     */
    public static String getNowDateString(String format) {
        return DateTimeFormatter.ofPattern(format).format(LocalDate.now());
    }

    /**
     * 将Long类型的时间戳转换成String 类型的时间格式，时间格式为：yyyy-MM-dd HH:mm:ss
     */
    public static String convertTimeToString(Long time){
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static Long convertTimeToLong(String time) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse("2018-05-29 13:52:50", ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 取本月第一天
     */
    public static LocalDate firstDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 取本月第N天
     */
    public static LocalDate dayOfThisMonth(int n) {
        LocalDate today = LocalDate.now();
        return today.withDayOfMonth(n);
    }

    /**
     * 取本月最后一天
     */
    public static LocalDate lastDayOfThisMonth() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 取本月第一天的开始时间
     */
    public static LocalDateTime startOfThisMonth() {
        return LocalDateTime.of(firstDayOfThisMonth(), LocalTime.MIN);
    }

    /**
     * 取本月最后一天的结束时间
     */
    public static LocalDateTime endOfThisMonth() {
        return LocalDateTime.of(lastDayOfThisMonth(), LocalTime.MAX);
    }

    /**
     * 获取当前时间距离一天结束的剩余秒数
     */
    public static int getRemainSecondsOneDay(){
        // 从一个Instant和区域ID获得LocalDateTime实例
        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        // 获取第第二天零点时刻的实例
        LocalDateTime toromorrowTime = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault())
                .plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        // ChronoUnit日期枚举类,between方法计算两个时间对象之间的时间量
        return (int) ChronoUnit.SECONDS.between(localDateTime, toromorrowTime);
    }

    /**
     * 获取当前时间距离一天结束的剩余毫秒数
     */
    public static int getRemainMilliSecondsOneDay(){
        return getRemainSecondsOneDay() * 1000;
    }

}
