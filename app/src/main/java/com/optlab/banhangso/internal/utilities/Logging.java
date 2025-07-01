package com.optlab.banhangso.internal.utilities;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.optlab.banhangso.BuildConfig;
import lombok.experimental.UtilityClass;
import timber.log.Timber;

@UtilityClass
public class Logging {
  public static void configTimber() {
    // Plant the appropriate Timber tree based on build type
    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTreeWithTag());
    } else {
      Timber.plant(new ReleaseTree());
    }
  }

  private static class DebugTreeWithTag extends Timber.DebugTree {
    @Override
    protected String createStackElementTag(@NonNull StackTraceElement element) {
      return super.createStackElementTag(element) + ": " + element.getLineNumber();
    }
  }

  static class ReleaseTree extends Timber.Tree {

    private static final int MAX_LOG_LENGTH = 4000;

    @Override
    @SuppressWarnings("deprecation")
    protected boolean isLoggable(int priority) {
      // Allow all logs (including DEBUG) in release builds to troubleshoot the issue
      // Once fixed, you can restore the original filtering logic
      return true; // priority != Log.VERBOSE && priority != Log.DEBUG && priority !=
      // Log.INFO;
    }

    @Override
    protected void log(
        int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {
      if (isLoggable(priority)) {
        if (priority == Log.ERROR && t != null) {
          // CRASH LIBRARY
        }

        if (message.length() < MAX_LOG_LENGTH) {
          printLog(priority, tag, message);
        } else {
          for (int i = 0, length = message.length(); i < length; i++) {
            int newLine = message.indexOf("\n", i);
            newLine = newLine != -1 ? newLine : length;
            do {
              int end = Math.min(newLine, i + MAX_LOG_LENGTH);
              String part = message.substring(i, end);
              printLog(priority, tag, message);
              i = end;
            } while (i < newLine);
          }
        }
      }
    }

    @SuppressLint("LogNotTimber")
    private void printLog(int priority, @Nullable String tag, @NonNull String message) {
      if (priority == Log.ASSERT) {
        Log.wtf(tag, message);
      } else {
        Log.println(priority, tag, message);
      }
    }
  }
}
