package edu.uob;

public class TokenPlainText {
    public static boolean isValidPlainText(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        return str.matches("[a-zA-Z0-9]+");
    }
}
