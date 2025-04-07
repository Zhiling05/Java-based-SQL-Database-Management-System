package edu.uob;

public class TokenSimpleCondition extends TokenCondition {
    private String attributeName;
    private TokenComparator comparator;
    private String value;

    public TokenSimpleCondition(String attruibuteName, TokenComparator comparator, String value) {
        this.attributeName = attruibuteName;
        this.comparator = comparator;
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public TokenComparator getComparator() {
        return comparator;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return attributeName + " " + comparator.getSymbol() + " " + value;
    }
}
