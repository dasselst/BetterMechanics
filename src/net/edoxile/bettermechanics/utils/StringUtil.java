package net.edoxile.bettermechanics.utils;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class StringUtil {

    public static String stripBrackets(String string) {
        if (string.length() > 2 && string.charAt(0) == '[' && string.charAt(string.length() - 1) == ']') {
            return string.substring(1, string.length() - 2);
        } else {
            return string;
        }
    }

    public static String merge(String[] data, int offset) {
        String str = "";
        for (int i = offset; i < data.length; i++) {
            str += data[i];
            if ((i + 1) != data.length)
                str += " ";
        }
        return str;
    }
}
