package edu.uob;

public class TokenBooleanLiteral {
    public static boolean isValidBooleanLiteral(String str) {
        return "TRUE".equalsIgnoreCase(str) || "FALSE".equalsIgnoreCase(str);
    }
}
