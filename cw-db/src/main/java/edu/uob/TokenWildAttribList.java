package edu.uob;

public class TokenWildAttribList {
    private TokenAttributeList attributeList;
    private boolean isWildcard;

    public TokenWildAttribList(TokenAttributeList attributeList) {
        this.attributeList = attributeList;
        this.isWildcard = false;
    }

    public TokenWildAttribList(boolean isWildcard) {
        if (!isWildcard) {
            throw new IllegalArgumentException("isWildcard format error.");
        }
        this.isWildcard = true;
        this.attributeList = null;
    }

    public boolean isWildcard() {
        return isWildcard;
    }

    public TokenAttributeList getAttributeList() {
        return attributeList;
    }

    public static TokenWildAttribList parse(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("WildAttributeList part is missing.");
        }
        str = str.trim();
        if (str.equals("*")) {
            return new TokenWildAttribList(true);
        } else {
            TokenAttributeList list = TokenAttributeList.parse(str);
            return new TokenWildAttribList(list);
        }
    }

    public static boolean isValid(String str) {
        try {
            parse(str);
            return true;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        if (isWildcard) {
            return "*";
        } else {
            return attributeList.toString();
        }
    }
}
