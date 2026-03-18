package org.example.hominganimal.infrastructure.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class TimeUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    /**
     * LocalDateTime → String
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * LocalDateTime → String (自定义格式)
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * String → LocalDateTime
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间格式错误，正确格式应为：" + DEFAULT_PATTERN, e);
        }
    }

    /**
     * String → LocalDateTime (安全解析，解析失败返回null)
     */
    public static LocalDateTime parseSafe(String dateTimeStr) {
        try {
            return parse(dateTimeStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间字符串
     */
    public static String now() {
        return format(LocalDateTime.now());
    }

    /**
     * 验证时间字符串格式是否正确
     */
    public static boolean isValid(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(dateTimeStr.trim(), DEFAULT_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}