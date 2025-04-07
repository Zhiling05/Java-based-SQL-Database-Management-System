package edu.uob;

public class TokenValue {
    public static boolean isValidValue(String str) {

        if (str == null || str.isEmpty()) {
            return false;
        }

        if(str.startsWith("'") && str.endsWith("'")) {
            String innerStr = str.substring(1, str.length() - 1);
            return TokenStringLiteral.isValidStringLiteral(innerStr);
        }

        if((str.startsWith("'") && !str.endsWith("'")) || !str.startsWith("'") && str.endsWith("'")) {
            return false;
        }


        if (str.equalsIgnoreCase("NULL")) {
            return true;
        }

        if (TokenBooleanLiteral.isValidBooleanLiteral(str)) {
            return true;
        }

        if (TokenFloatLiteral.isValidFloatLiteral(str)) {
            return true;
        }

        if (TokenIntegerLiteral.isValidIntegerLiteral(str)) {
            return true;
        }

        return false;
    }
}
