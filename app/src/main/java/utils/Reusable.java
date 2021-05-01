package utils;

import java.text.NumberFormat;
import java.util.Locale;

public class Reusable {

    public static String getFormattedAmount(long amount){
        return NumberFormat.getIntegerInstance(Locale.US).format(amount);
    }
}
