package com.optlab.banhangso.internal.gson;

import androidx.annotation.NonNull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

public class DateTimeDeserializer implements JsonDeserializer<Date> {

  /**
   * List of date formats to try when deserializing a date string.
   *
   * <p>This list includes various formats that cover different time representations, including:
   *
   * <ul>
   *   <li>ISO 8601 formats with varying levels of precision (up to microseconds)
   *   <li>Formats with 'Z' suffix indicating UTC time
   *   <li>Formats without 'Z' suffix, which are interpreted as local time
   * </ul>
   */
  private static final List<String> DATE_FORMATS =
      List.of(
          "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
          "yyyy-MM-dd'T'HH:mm:ss.SSS",
          "yyyy-MM-dd'T'HH:mm:ss",
          "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
          "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
          "yyyy-MM-dd'T'HH:mm:ss'Z'");

  @Override
  public Date deserialize(
      @NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    String dateTimeString = json.getAsString();

    for (String format : DATE_FORMATS) {
      try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.parse(dateTimeString);
      } catch (Exception e) {
        Timber.e(
            "Failed to parse date '%s' with format '%s': %s",
            dateTimeString, format, e.getMessage());
      }
    }

    throw new JsonParseException("Unable to parse date: " + dateTimeString);
  }
}
