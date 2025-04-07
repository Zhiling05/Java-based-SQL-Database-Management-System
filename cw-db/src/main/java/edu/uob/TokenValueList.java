package edu.uob;

public class TokenValueList {
    public static boolean isValidValueList(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        String[] values = str.split("\\s*,\\s*");
        for (String value : values) {
            if (!TokenValue.isValidValue(value)) {
                return false;
            }
        }
        return true;
    }
}
