package edu.uob;

public class TokenCompoundCondition extends TokenCondition{
    private TokenCondition left;
    private TokenCondition right;
    private TokenBoolOperator operator;

    public TokenCompoundCondition(TokenCondition left, TokenBoolOperator operator, TokenCondition right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + operator.toString() + " " + right.toString() + ")";
    }

    public TokenCondition getLeft() {
        return left;
    }

    public TokenCondition getRight() {
        return right;
    }

    public TokenBoolOperator getOperator() {
        return operator;
    }
}
