package com.optlab.banhangso.internal;

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

  public static final String OWNER = "owner";

  public static final String STAFF = "staff";
}
