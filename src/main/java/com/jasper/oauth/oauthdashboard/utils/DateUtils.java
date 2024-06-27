package com.jasper.oauth.oauthdashboard.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for date operations
 */
@Slf4j
public class DateUtils {
  private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static String getDateString(LocalDateTime date) {
    return date.format(java.time.format.DateTimeFormatter.ofPattern(DATE_FORMAT));
  }

  public static LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }

  public static LocalDateTime formatDateTime(LocalDateTime date) {
    String formattedDate = date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    return LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
  }
}
