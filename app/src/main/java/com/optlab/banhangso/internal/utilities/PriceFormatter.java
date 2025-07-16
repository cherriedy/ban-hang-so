package com.optlab.banhangso.internal.utilities;

import static com.optlab.banhangso.internal.Config.DEFAULT_CURRENCY_CODE;
import static com.optlab.banhangso.internal.Config.VIETNAM_LOCALE;

import androidx.annotation.NonNull;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PriceFormatter {

  private static final DecimalFormat DECIMAL_FORMAT;
  private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS;
  private static final String VIETNAM_CURRENCY_SYMBOL;

  static {
    // Create a DecimalFormatSymbols instance for Vietnam locale
    DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(new Locale("vi", "VN"));

    // Set the decimal and grouping separators for Vietnam
    DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator(',');

    // Use dot as the grouping separator for thousands
    DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator('.');

    // Create a DecimalFormat instance with the Vietnam locale
    DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(VIETNAM_LOCALE);

    // Set the decimal format symbols to the Vietnam locale symbols
    DECIMAL_FORMAT.setDecimalFormatSymbols(DECIMAL_FORMAT_SYMBOLS);

    // Set the maximum fraction digits to 0 for whole numbers
    DECIMAL_FORMAT.setGroupingUsed(true);

    // Get the currency symbol for Vietnam (VND)
    Currency vnd = Currency.getInstance(DEFAULT_CURRENCY_CODE);

    // Set the Vietnam currency symbol (đ) for display
    VIETNAM_CURRENCY_SYMBOL = vnd.getSymbol(VIETNAM_LOCALE);
  }

  public static synchronized DecimalFormat getInstance() {
    return DECIMAL_FORMAT;
  }

  /**
   * Format price with Vietnam currency suffix (đ)
   *
   * @param price The price to format
   * @return Formatted price with Vietnam currency suffix
   */
  @NonNull public static String withSuffix(double price) {
    return DECIMAL_FORMAT.format(price) + " " + VIETNAM_CURRENCY_SYMBOL;
  }
}
