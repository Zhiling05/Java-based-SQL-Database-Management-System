package edu.uob;

public class TokenFloatLiteral {
    public static boolean isValidFloatLiteral(String str) {
        if (str == null || str.length() < 3) {
            return false;
        }

        int dotIndex = str.indexOf('.');
        if (dotIndex == -1 || dotIndex != str.lastIndexOf('.')) {
            return false;
        }

        String leftPart = str.substring(0, dotIndex);
        String rightPart = str.substring(dotIndex + 1);

        if (!TokenDigitSequence.isValidDigitSequence(rightPart)) {
            return false;
        }

        return TokenSymbolDigitSequence.isValidSymbolDigitSequence(leftPart) || TokenDigitSequence.isValidDigitSequence(leftPart);
    }
}
