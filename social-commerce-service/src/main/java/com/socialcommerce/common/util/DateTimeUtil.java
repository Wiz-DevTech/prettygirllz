package com.socialcommerce.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations
 * Provides consistent date formatting and timezone handling
 */
public class DateTimeUtil {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter READABLE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtil() {
        // Utility class
    }

    /**
     * Get current UTC timestamp
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    /**
     * Convert LocalDateTime to ISO string
     */
    public static String toIsoString(LocalDateTime dateTime) {
        ZonedDateTime zdt = dateTime.atZone(DEFAULT_ZONE);
        return ISO_FORMATTER.format(zdt);
    }

    /**
     * Parse ISO string to LocalDateTime
     */
    public static LocalDateTime fromIsoString(String isoString) {
        ZonedDateTime zdt = ZonedDateTime.parse(isoString, ISO_FORMATTER);
        return zdt.withZoneSameInstant(DEFAULT_ZONE).toLocalDateTime();
    }

    /**
     * Format datetime to readable string
     */
    public static String toReadableString(LocalDateTime dateTime) {
        return READABLE_FORMATTER.format(dateTime);
    }

    /**
     * Get start of day for given date
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Get end of day for given date
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    /**
     * Calculate age from birthdate
     */
    public static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Check if datetime is within last N hours
     */
    public static boolean isWithinLastHours(LocalDateTime dateTime, int hours) {
        LocalDateTime threshold = nowUtc().minusHours(hours);
        return dateTime.isAfter(threshold);
    }

    /**
     * Get time difference in human-readable format
     */
    public static String getTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = nowUtc();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        
        if (minutes < 1) return "just now";
        if (minutes < 60) return minutes + " minutes ago";
        
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) return hours + " hours ago";
        
        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 30) return days + " days ago";
        
        long months = ChronoUnit.MONTHS.between(dateTime, now);
        if (months < 12) return months + " months ago";
        
        long years = ChronoUnit.YEARS.between(dateTime, now);
        return years + " years ago";
    }

    /**
     * Convert between timezones
     */
    public static LocalDateTime convertTimezone(LocalDateTime dateTime, 
                                              ZoneId fromZone, 
                                              ZoneId toZone) {
        ZonedDateTime zonedDateTime = dateTime.atZone(fromZone);
        return zonedDateTime.withZoneSameInstant(toZone).toLocalDateTime();
    }

    /**
     * Get unix timestamp
     */
    public static long toUnixTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toEpochSecond();
    }

    /**
     * From unix timestamp
     */
    public static LocalDateTime fromUnixTimestamp(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
    }
}
