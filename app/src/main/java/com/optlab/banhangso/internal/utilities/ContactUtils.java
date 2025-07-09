package com.optlab.banhangso.internal.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import timber.log.Timber;

@UtilityClass
public class ContactUtils {

  public static final String KEY_NAME = "KEY_NAME";
  public static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";
  public static final String KEY_PHONE = "KEY_PHONE";
  public static final String KEY_EMAIL = "KEY_EMAIL";
  public static final String KEY_DOB = "KEY_DOB";
  public static final String KEY_ADDRESS = "KEY_ADDRESS";

  @NonNull public static Map<String, String> getDetails(@NonNull Context context, @NonNull Uri contactUri) {
    final String[] contactColumns =
        new String[] {
          BaseColumns._ID,
          ContactsContract.Contacts.DISPLAY_NAME,
          ContactsContract.Contacts.PHOTO_URI
        };

    Map<String, String> details =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
            ? HashMap.newHashMap(4)
            : new HashMap<>(4);

    try (Cursor cursor =
        context.getContentResolver().query(contactUri, contactColumns, null, null, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        details.put(
            KEY_NAME,
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));

        details.put(
            KEY_IMAGE_URI,
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)));

        // Get the ID of the contact to fetch phone, email, and other details
        String contactId = cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID));
        getPhone(context, contactId, details); // Fetch phone number
        getEmail(context, contactId, details); // Fetch email address
        getDob(context, contactId, details); // Fetch date of birth
        getAddress(context, contactId, details); // Fetch address
      }
    }

    return details;
  }

  private static void getAddress(
      @NonNull Context context, @NonNull String contactId, @NonNull Map<String, String> details) {
    final Uri addressUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
    final String[] addressColumn =
        new String[] {ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS};
    final String addressQuery =
        ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?";

    try (Cursor cursor =
        context
            .getContentResolver()
            .query(addressUri, addressColumn, addressQuery, new String[] {contactId}, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        String address = cursor.getString(cursor.getColumnIndexOrThrow(addressColumn[0]));
        if (address != null) {
          details.put(KEY_ADDRESS, address);
        }
      }
    }
  }

  private static void getDob(
      @NonNull Context context, @NonNull String contactId, @NonNull Map<String, String> details) {
    final String[] dobColumn = new String[] {ContactsContract.CommonDataKinds.Event.START_DATE};

    final String dobQuery =
        ContactsContract.CommonDataKinds.Event.CONTACT_ID
            + " = ? AND "
            + ContactsContract.Data.MIMETYPE
            + " = ? AND "
            + ContactsContract.CommonDataKinds.Event.TYPE
            + " = ?";

    final String[] queryArgs =
        new String[] {
          contactId,
          ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
          String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
        };

    final Uri dobUri = ContactsContract.Data.CONTENT_URI;

    try (Cursor cursor =
        context.getContentResolver().query(dobUri, dobColumn, dobQuery, queryArgs, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        String dob = cursor.getString(cursor.getColumnIndexOrThrow(dobColumn[0]));
        // Only add the date of birth if it is in the correct format (yyyy-MM-dd)
        if (dob != null && dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
          details.put(KEY_DOB, dob);
        } else {
          Timber.d("Invalid date of birth format: %s", dob);
        }
      }
    }
  }

  private static void getEmail(
      @NonNull Context context, @NonNull String contactId, @NonNull Map<String, String> details) {
    final String[] emailColumn = new String[] {ContactsContract.CommonDataKinds.Email.ADDRESS};
    final Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    final String emailQuery = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";

    try (Cursor cursor =
        context
            .getContentResolver()
            .query(emailUri, emailColumn, emailQuery, new String[] {contactId}, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        String email = cursor.getString(cursor.getColumnIndexOrThrow(emailColumn[0]));
        if (email != null) {
          details.put(KEY_EMAIL, email);
        }
      }
    }
  }

  private static void getPhone(
      @NonNull Context context, @NonNull String contactId, @NonNull Map<String, String> details) {
    final String[] phoneColumn = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
    final Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    final String phoneQuery = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

    try (Cursor cursor =
        context
            .getContentResolver()
            .query(phoneUri, phoneColumn, phoneQuery, new String[] {contactId}, null)) {
      if (cursor != null && cursor.moveToFirst()) {
        String phone =
            cursor.getString(cursor.getColumnIndexOrThrow(phoneColumn[0])).replaceAll("\\s+", "");
        details.put(KEY_PHONE, phone);
      }
    }
  }
}
