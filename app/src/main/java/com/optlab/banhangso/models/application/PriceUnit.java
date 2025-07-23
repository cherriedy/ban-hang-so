package com.optlab.banhangso.models.application;

import androidx.annotation.StringRes;
import com.optlab.banhangso.R;
import lombok.Getter;

@Getter
public enum PriceUnit {
  NONE(1, "", -1),
  THOUSAND(1_000, "K", R.string.price_unit_thousand),
  MILLION(1_000_000, "M", R.string.price_unit_million),
  BILLION(1_000_000_000, "B", R.string.price_unit_billion);

  private final int value;
  private final String suffix;
  @StringRes private final int nameStringRes;

  PriceUnit(int value, String suffix, int nameStringRes) {
    this.value = value;
    this.suffix = suffix;
    this.nameStringRes = nameStringRes;
  }

  public static PriceUnit fromString(String unit) {
    if (unit == null) return NONE;
    return switch (unit.toLowerCase()) {
      case "thousand" -> THOUSAND;
      case "million" -> MILLION;
      case "billion" -> BILLION;
      default -> NONE;
    };
  }
}
