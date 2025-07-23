package com.optlab.banhangso.features.shared.utilities;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import com.optlab.banhangso.R;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import java.net.ConnectException;
import java.util.List;
import lombok.experimental.UtilityClass;
import timber.log.Timber;

@UtilityClass
public class LoadStateUtils {

  public static boolean isLoading(@NonNull CombinedLoadStates loadStates) {
    return loadStates.getSource().getRefresh() instanceof LoadState.Loading
        || loadStates.getSource().getAppend() instanceof LoadState.Loading
        || loadStates.getSource().getPrepend() instanceof LoadState.Loading;
  }

  public static void handleLoadStateError(
      @NonNull Context context, @NonNull CombinedLoadStates loadStates) {
    List<LoadState> states =
        List.of(
            loadStates.getSource().getRefresh(),
            loadStates.getSource().getAppend(),
            loadStates.getSource().getPrepend());

    LoadState.Error error =
        states.stream()
            .filter(state -> state instanceof LoadState.Error)
            .map(state -> (LoadState.Error) state)
            .findFirst()
            .orElse(null);

    if (error != null) {
      int messageResId = -1;
      Throwable throwable = error.getError();
      if (throwable instanceof ApiResponseException apiResponseException) {
        Timber.e(
            apiResponseException,
            "There was an error loading transactions: %s",
            apiResponseException.getMessage());

        messageResId = R.string.error_unknown;
      } else if (throwable instanceof ConnectException connectException) {
        Timber.e(
            connectException,
            "There was a connection error loading transactions: %s",
            connectException.getMessage());

        messageResId = R.string.error_network;
      } else if (throwable instanceof IllegalStateException) {
        Timber.e(
            throwable,
            "There was an illegal state error loading transactions: %s",
            throwable.getMessage());

        messageResId = R.string.error_unknown;
      }

      if (messageResId != -1) {
        Toast.makeText(context, context.getString(messageResId), Toast.LENGTH_SHORT).show();
      }
    }
  }
}
