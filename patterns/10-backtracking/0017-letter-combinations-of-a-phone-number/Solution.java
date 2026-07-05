import java.util.*;

class Solution {
    public List<String> letterCombinations(String digits) {
        List<String> results = new ArrayList<>();
        if (digits.isEmpty()) return results;

        String[] map = {"abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        backtrack(results, new StringBuilder(), 0, digits, map);
        return results;
    }

    private void backtrack(List<String> results, StringBuilder prefix,
                           int index, String digits, String[] map) {
        if (index == digits.length()) {
            results.add(prefix.toString());             // snapshot the builder
            return;
        }
        String letters = map[digits.charAt(index) - '2'];  // choices for this digit
        for (int i = 0; i < letters.length(); i++) {
            prefix.append(letters.charAt(i));             // CHOOSE
            backtrack(results, prefix, index + 1, digits, map);  // EXPLORE next digit
            prefix.deleteCharAt(prefix.length() - 1);     // UN-CHOOSE
        }
    }
}
