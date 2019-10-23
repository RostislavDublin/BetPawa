package rdublin.utils;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

public final class CurrencyUtils {
    public static final String USD_CODE = "USD";
    public static final Currency USD = Currency.getInstance(CurrencyUtils.USD_CODE);
    public static final String EUR_CODE = "EUR";
    public static final Currency EUR = Currency.getInstance(EUR_CODE);
    public static final String GBP_CODE = "GBP";
    public static final Currency GBP = Currency.getInstance(GBP_CODE);
    public static final Set<String> KNOWN_CURRENCY_CODES;

    static {
        KNOWN_CURRENCY_CODES = new HashSet(Arrays.asList(USD_CODE, EUR_CODE, GBP_CODE));
    }

}
