package edu.uob;

public class TokenIntegerLiteral {
    public static boolean isValidIntegerLiteral(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        return TokenDigitSequence.isValidDigitSequence(str) || TokenSymbolDigitSequence.isValidSymbolDigitSequence(str);
    }
}
