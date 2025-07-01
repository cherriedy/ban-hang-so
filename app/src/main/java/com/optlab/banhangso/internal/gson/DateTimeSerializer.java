package com.optlab.banhangso.internal.gson;

import androidx.annotation.NonNull;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeSerializer implements JsonSerializer<Date> {

  /**
   * Default format for serializing dates to JSON strings.
   *
   * <p>Uses ISO 8601 format with milliseconds and UTC timezone (Z suffix) for consistent and
   * standardized date representation.
   */
  private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  @Override
  public JsonElement serialize(
      @NonNull Date src, Type typeOfSrc, JsonSerializationContext context) {

    SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    String formattedDate = dateFormat.format(src);
    return new JsonPrimitive(formattedDate);
  }
}
