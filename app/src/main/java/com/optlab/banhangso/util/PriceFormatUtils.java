package com.optlab.banhangso.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class PriceFormatUtils {

    private static final DecimalFormat DECIMAL_FORMAT;
    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS;

    static {
        DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.US);
        DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator(',');
        DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator('.');
        DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        DECIMAL_FORMAT.setDecimalFormatSymbols(DECIMAL_FORMAT_SYMBOLS);
        DECIMAL_FORMAT.setGroupingUsed(true);
    }

    private PriceFormatUtils() {
        throw new UnsupportedOperationException("Utility Class");
    }

    public static synchronized DecimalFormat getInstance() {
        return DECIMAL_FORMAT;
    }
}
