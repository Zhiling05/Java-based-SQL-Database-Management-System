package edu.uob;

public class TokenStringLiteral {
    public static boolean isValidStringLiteral(String str) {
        if (str.isEmpty()) {
            return true;
        }

        for (char ch : str.toCharArray()) {
            if (!TokenCharLiteral.isValidCharLiteral(ch)) {
                return false;
            }
        }

        return true;
    }
}
