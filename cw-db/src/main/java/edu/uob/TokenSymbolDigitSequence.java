package edu.uob;

public class TokenSymbolDigitSequence {
    public static boolean isValidSymbolDigitSequence(String str) {
        if (str == null || str.length() < 2) {
            return false;
        }

        char firstChar = str.charAt(0);
        if (firstChar != '+' && firstChar != '-') {
            return false;
        }

        String digitPart = str.substring(1);
        return TokenDigitSequence.isValidDigitSequence(digitPart);
    }
}
