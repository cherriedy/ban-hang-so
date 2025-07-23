package com.optlab.banhangso.features.shared.utilities;

import android.view.View;
import androidx.annotation.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ViewUtils {

  public static void setAnimatedVisibility(@NonNull View view, boolean visibility) {
    if (visibility) {
      view.animate()
          .alpha(0f)
          .setDuration(0)
          .withEndAction(() -> view.setVisibility(View.GONE))
          .start();
    } else {
      view.setAlpha(0f);
      view.setVisibility(View.VISIBLE);
      view.animate().alpha(1f).setDuration(200).start();
    }
  }
}
