package com.azki.reservation.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RedisClosestDateTimeFinder {

    // تابع برای تبدیل تاریخ و زمان به ثانیه از UNIX epoch (1970-01-01 00:00:00)
    public long dateTimeToSeconds(String dateTimeStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(dateTimeStr);
        return date.getTime() / 1000;  // تبدیل میلی‌ثانیه به ثانیه
    }

    // ذخیره تاریخ و زمان‌ها در Redis به صورت sorted set
    public void storeDateTimesInRedis(Jedis jedis, String[] dateTimes) throws ParseException {
        for (String dateTime : dateTimes) {
            long seconds = dateTimeToSeconds(dateTime);
            jedis.zadd("dateTimes", seconds, dateTime);  // "dateTimes" نام sorted set است
        }
    }

    // پیدا کردن نزدیک‌ترین تاریخ و زمان در Redis
    public String findClosestDateTime(Jedis jedis, String targetDateTime) throws ParseException {
        long targetSeconds = dateTimeToSeconds(targetDateTime);

        // پیدا کردن نزدیک‌ترین تاریخ و زمان
        String closestDateTime = null;
        double diff = Double.MAX_VALUE;

        // جستجو در محدوده تاریخ و زمان‌های موجود در Redis
        for (String dateTime : jedis.zrange("dateTimes", 0, -1)) {
            long dateTimeSeconds = dateTimeToSeconds(dateTime);
            double currentDiff = Math.abs(dateTimeSeconds - targetSeconds);
            if (currentDiff < diff) {
                diff = currentDiff;
                closestDateTime = dateTime;
            }
        }
        return closestDateTime;
    }

//    public static void main(String[] args) throws ParseException {
//        // اتصال به Redis
//        try (Jedis jedis = new Jedis("localhost", 6379)) {
//            // آرایه تاریخ و زمان‌ها
//            String[] dateTimes = {
//                    "2025-02-09 10:30:00",
//                    "2025-02-09 14:45:00",
//                    "2025-02-09 08:15:00",
//                    "2025-02-09 18:00:00"
//            };
//
//            // ذخیره تاریخ و زمان‌ها در Redis
//            storeDateTimesInRedis(jedis, dateTimes);
//
//            // تاریخ و زمان ورودی برای جستجو
//            String targetDateTime = "2025-02-09 12:00:00";
//
//            // پیدا کردن نزدیک‌ترین تاریخ و زمان
//            String closestDateTime = findClosestDateTime(jedis, targetDateTime);
//            System.out.println("The closest date and time to " + targetDateTime + " is " + closestDateTime);
//        }
//    }
}
