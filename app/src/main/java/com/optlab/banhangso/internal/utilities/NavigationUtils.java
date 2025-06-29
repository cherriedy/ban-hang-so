package com.optlab.banhangso.internal.utilities;

import androidx.navigation.NavOptions;
import com.optlab.banhangso.R;

public class NavigationUtils {

  /** Returns NavOptions with standard slide animations */
  public static NavOptions getNavOptions() {
    return new NavOptions.Builder()
        .setEnterAnim(R.anim.anim_slide_in_right)
        .setExitAnim(R.anim.anim_slide_out_left)
        .setPopEnterAnim(R.anim.anim_slide_in_left)
        .setPopExitAnim(R.anim.anim_slide_out_right)
        .build();
  }

  /** Returns NavOptions with standard slide animations and popUpTo */
  public static NavOptions getNavOptions(int destinationId, boolean inclusive) {
    return new NavOptions.Builder()
        .setPopUpTo(destinationId, inclusive)
        .setEnterAnim(R.anim.anim_slide_in_right)
        .setExitAnim(R.anim.anim_slide_out_left)
        .setPopEnterAnim(R.anim.anim_slide_in_left)
        .setPopExitAnim(R.anim.anim_slide_out_right)
        .build();
  }
}
