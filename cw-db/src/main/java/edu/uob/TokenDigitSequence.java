package edu.uob;

public class TokenDigitSequence {
    public static boolean isValidDigitSequence(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        return str.matches("\\d+");
    }
}
