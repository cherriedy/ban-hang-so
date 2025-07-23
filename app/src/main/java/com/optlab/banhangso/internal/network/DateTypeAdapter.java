package com.optlab.banhangso.internal.network;

import static com.optlab.banhangso.internal.Config.DATETIME_FORMAT;
import static com.optlab.banhangso.internal.Config.DATE_FORMAT;

import androidx.annotation.NonNull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.optlab.banhangso.internal.Config;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import timber.log.Timber;

public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

  private final SimpleDateFormat dateTimeFormat =
      new SimpleDateFormat(DATETIME_FORMAT, Config.VIETNAM_LOCALE);

  private final SimpleDateFormat dateFormat =
      new SimpleDateFormat(DATE_FORMAT, Config.VIETNAM_LOCALE);

  private final List<SimpleDateFormat> formats = List.of(dateTimeFormat, dateFormat);

  @Override
  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    String dateString = dateTimeFormat.format(src);
    return new JsonPrimitive(dateString);
  }

  @Override
  public Date deserialize(
      @NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    String dateString = json.getAsString(); // Get the date string from JSON.

    for (var format : formats) {
      try {
        return format.parse(dateString);
      } catch (ParseException ignored) {
        // If parsing with dateTimeFormat fails, try the next format.
      }
    }
    Timber.e("Failed to parse date: %s", dateString);
    throw new JsonParseException("Unable to parse date: " + dateString);
  }
}
