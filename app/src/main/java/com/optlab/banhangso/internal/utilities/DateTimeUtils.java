package com.optlab.banhangso.internal.utilities;

import static com.optlab.banhangso.internal.Config.DEFAULT_TIMEZONE;
import static com.optlab.banhangso.internal.Config.VIETNAM_LOCALE;
import static com.optlab.banhangso.internal.Config.YEAR_MONTH_FORMAT;

import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.Config;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtils {

  public static final String DISPLAY_DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

  @NonNull public static String forDisplay(@NonNull Date date) {
    return convertToDisplayDateTime(
        LocalDateTime.ofInstant(date.toInstant(), ZoneId.of(DEFAULT_TIMEZONE)));
  }

  @NonNull public static String forDisplay(@NonNull String dateTime) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Config.DATETIME_FORMAT);
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
    return convertToDisplayDateTime(localDateTime);
  }

  @NonNull public static String getToday() {
    return getTodayDate().format(DateTimeFormatter.ISO_DATE);
  }

  @NonNull public static String getYesterday() {
    return getTodayDate().minusDays(1).format(DateTimeFormatter.ISO_DATE);
  }

  @NonNull public static String getThisMonth() {
    return getTodayDate().format(DateTimeFormatter.ofPattern(YEAR_MONTH_FORMAT));
  }

  @NonNull public static String getLastMonth() {
    return getTodayDate().minusMonths(1).format(DateTimeFormatter.ofPattern(YEAR_MONTH_FORMAT));
  }

  @NonNull public static String getThisYear() {
    return String.valueOf(getTodayDate().getYear());
  }

  @NonNull private static LocalDate getTodayDate() {
    return LocalDate.now(ZoneId.of(DEFAULT_TIMEZONE));
  }

  @NonNull private static String convertToDisplayDateTime(@NonNull LocalDateTime localDateTime) {
    String dayName = localDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, VIETNAM_LOCALE);
    String formattedDateTime =
        localDateTime.format(DateTimeFormatter.ofPattern(DISPLAY_DATETIME_FORMAT));
    return String.format(VIETNAM_LOCALE, "%s, %s", dayName, formattedDateTime);
  }
}
