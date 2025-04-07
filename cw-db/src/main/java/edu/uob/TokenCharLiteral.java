package edu.uob;

public class TokenCharLiteral {
    private static final String VALID_SYMBOLS = "!#$%&()*+,-./:;>=<?@[\\]^_`{}~";

    public static boolean isValidCharLiteral(char ch) {
        if (ch == ' ') {
            return true;
        }

        if (Character.isLetter(ch)) {
            return true;
        }

        if (Character.isDigit(ch)) {
            return true;
        }

        if (VALID_SYMBOLS.indexOf(ch) != -1) {
            return true;
        }

        return false;
    }
}
