package com.optlab.banhangso.internal;

import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Config {

  public static final long DEFAULT_TIMEOUT = 30L;

  public static final int ITEMS_PER_PAGE = 10;
  public static final int MAX_IMAGE_UPLOADS = 10;

  /**
   * The date-time format used for deserialization. This format is expected to match the ISO 8601
   * format with microseconds. For example: "2023-10-01T12:34:56.123456"
   */
  public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

  /**
   * The date format used for deserialization. This format is expected to match the ISO 8601 date
   * format. For example: "2023-10-01"
   */
  public static final String DATE_FORMAT = "yyyy-MM-dd";

  public static final String YEAR_MONTH_FORMAT = "yyyy-MM";

  public static final String OWNER = "OWNER";
  public static final String STAFF = "STAFF";

  public static final String DEFAULT_LANGUAGE = "vi";
  public static final String DEFAULT_COUNTRY = "VN";
  public static final String DEFAULT_CURRENCY_CODE = "VND";
  public static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
  public static final Locale VIETNAM_LOCALE = new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
}
