package edu.uob;

public class ConditionParser {
    public static TokenCondition parseCondition(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition format error.");
        }
        input = input.trim();

        while (input.startsWith("(") && input.endsWith(")") && isEnclosedInParentheses(input)) {
            input = input.substring(1, input.length() - 1).trim();
        }

        int count = 0;
        int pos = -1;
        String operatorFound = null;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '(') {
                count++;
            } else if (ch == ')') {
                count--;
            }
            if (count == 0) {
                if (i + 5 <= input.length() && input.substring(i, i + 5).equalsIgnoreCase(" AND ")) {
                    pos = i;
                    operatorFound = "AND";
                    break;
                }
                if (i + 4 <= input.length() && input.substring(i, i + 4).equalsIgnoreCase(" OR ")) {
                    pos = i;
                    operatorFound = "OR";
                    break;
                }
            }
        }

        if (operatorFound != null && pos != -1) {
            String leftStr = input.substring(0, pos).trim();
            int opLength = operatorFound.equalsIgnoreCase("AND") ? 5 : 4;
            String rightStr = input.substring(pos + opLength).trim();
            TokenCondition leftCond = parseCondition(leftStr);
            TokenCondition rightCond = parseCondition(rightStr);
            return new TokenCompoundCondition(leftCond, TokenBoolOperator.fromString(operatorFound), rightCond);
        }

        return parseSimpleCondition(input);
    }

    private static boolean isEnclosedInParentheses(String input) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '(') count++;
            else if (ch == ')') count--;
            if (count == 0 && i < input.length() - 1) {
                return false;
            }
        }
        return count == 0;
    }

    // Parsing SimpleCondition：[AttributeName] <Comparator> [Value]
    private static TokenCondition parseSimpleCondition(String input) {
        String[] comparators = {"==", ">=", "<=", "!=", ">", "<", "LIKE"};
        int index = -1;
        String foundComparator = null;
        String input1 = input.toUpperCase();
        for (String comp : comparators) {
            index = input1.indexOf(comp);
            if (index != -1) {
                foundComparator = comp;
                break;
            }
        }
        if (foundComparator == null) {
            throw new IllegalArgumentException("Cannot find valid comparator in condition: " + input + ".");
        }

        String left = input.substring(0, index).trim();
        String right = input.substring(index + foundComparator.length()).trim();

        if(!TokenAttributeName.isValid(left))   throw new IllegalArgumentException("Invalid attribute name: " + left);
        if(!TokenValue.isValidValue(right))   throw new IllegalArgumentException("Invalid value: " + right);
        TokenComparator comp = TokenComparator.fromString(foundComparator);
        return new TokenSimpleCondition(left, comp, right);
    }

}
