package edu.uob;

public class TokenNameValuePair {
    private final String attribute;
    private final String value;

    public TokenNameValuePair(String str) {
        if(str == null || str.trim().isEmpty())   throw new IllegalArgumentException("NameValuePair statement format error.");

        String[] parts = str.split("\\s*=\\s*");
        if(parts.length != 2)    throw new IllegalArgumentException("NameValuePair statement format error " + str);
        String left = parts[0];
        String right = parts[1];

        try {
            new TokenAttributeName(left);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid attribute " + left + ": " + e.getMessage());
        }
        if(!TokenValue.isValidValue(right)) {
            throw new IllegalArgumentException("Invalid value: " + right);
        }

        if(right.length() >= 2 && right.startsWith("'") && right.endsWith("'")) {
            right = right.substring(1, right.length() - 1);
        }

        this.attribute = left.toLowerCase();
        this.value = right;
    }

    public static boolean isValidNameValuePair(String str) {
        try {
            new TokenNameValuePair(str);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return attribute + " = " + value;
    }
}
