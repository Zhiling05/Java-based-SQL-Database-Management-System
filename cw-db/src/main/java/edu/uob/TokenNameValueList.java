package edu.uob;

public class TokenNameValueList {
    public static boolean isValidNameValueList(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        String[] pairs = str.split("\\s*,\\s*");

        for (String pair : pairs) {
            if (!TokenNameValuePair.isValidNameValuePair(pair)) {
                return false;
            }
        }

        return true;
    }
}
