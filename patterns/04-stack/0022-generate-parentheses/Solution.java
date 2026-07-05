import java.util.*;

class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> results = new ArrayList<>();
        backtrack(results, new StringBuilder(), 0, 0, n);
        return results;
    }

    private void backtrack(List<String> results, StringBuilder current,
                           int open, int close, int n) {
        if (current.length() == 2 * n) {
            results.add(current.toString());
            return;
        }
        if (open < n) {
            current.append('(');
            backtrack(results, current, open + 1, close, n);
            current.deleteCharAt(current.length() - 1);
        }
        if (close < open) {
            current.append(')');
            backtrack(results, current, open, close + 1, n);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
