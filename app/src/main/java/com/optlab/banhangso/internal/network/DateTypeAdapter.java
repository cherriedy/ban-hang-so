package com.optlab.banhangso.internal.network;

import static com.optlab.banhangso.internal.Config.DATETIME_FORMAT;

import androidx.annotation.NonNull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import timber.log.Timber;

public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

  private final SimpleDateFormat dateTimeFormat =
      new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());

  @Override
  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    String dateString = dateTimeFormat.format(src);
    return new JsonPrimitive(dateString);
  }

  @Override
  public Date deserialize(
      @NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    String dateString = json.getAsString();

    try {
      return dateTimeFormat.parse(dateString);
    } catch (ParseException e) {
      Timber.d("Failed to parse as datetime format, trying ISO format");
      throw new JsonParseException("Unable to parse date: " + dateString);
    }
  }
}
