package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class TokenAttributeList {
    private List<TokenAttributeName> attributes;

    public TokenAttributeList(List<TokenAttributeName> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("The attribute list cannot be empty.");
        }
        this.attributes = attributes;
    }

    public List<TokenAttributeName> getAttributes() {
        return attributes;
    }

    public static TokenAttributeList parse(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("No attributes provided");
        }
        String[] parts = str.split("\\s*,\\s*");
        List<TokenAttributeName> list = new ArrayList<>();
        for (String part : parts) {
            if (!TokenAttributeName.isValid(part)) {
                throw new IllegalArgumentException("Invalid attribute name: " + part);
            }
            TokenAttributeName t = new TokenAttributeName(part);
            for(TokenAttributeName existed : list) {
                if(existed.getName().equalsIgnoreCase(t.getName())){
                    throw new IllegalArgumentException("Duplicate attribute names are not allowed.");
                }
            }
            list.add(new TokenAttributeName(part));
        }
        return new TokenAttributeList(list);
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
        StringBuilder sb = new StringBuilder();
        for (TokenAttributeName attr : attributes) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(attr.getName());
        }
        return sb.toString();
    }
}
