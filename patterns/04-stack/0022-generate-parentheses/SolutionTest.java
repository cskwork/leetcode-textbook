import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Order does not matter, so compare as sets.
        total++; Set<String> r1 = new HashSet<>(s.generateParenthesis(1));
                 Set<String> e1 = new HashSet<>(Arrays.asList("()"));
                 if (r1.equals(e1)) passed++; else System.out.println("FAIL: n=1 -> " + r1);

        total++; Set<String> r2 = new HashSet<>(s.generateParenthesis(2));
                 Set<String> e2 = new HashSet<>(Arrays.asList("(())", "()()"));
                 if (r2.equals(e2)) passed++; else System.out.println("FAIL: n=2 -> " + r2);

        total++; Set<String> r3 = new HashSet<>(s.generateParenthesis(3));
                 Set<String> e3 = new HashSet<>(Arrays.asList(
                         "((()))", "(()())", "(())()", "()(())", "()()()"));
                 if (r3.equals(e3)) passed++; else System.out.println("FAIL: n=3 -> " + r3);

        // Counts must equal the nth Catalan number (1, 1, 2, 5, 14, 42, ...).
        total++; int c4 = s.generateParenthesis(4).size();
                 if (c4 == 14) passed++; else System.out.println("FAIL: |n=4| = " + c4 + " (want 14)");

        total++; int c5 = s.generateParenthesis(5).size();
                 if (c5 == 42) passed++; else System.out.println("FAIL: |n=5| = " + c5 + " (want 42)");

        // Edge case: zero pairs -> exactly one string, the empty string.
        total++; List<String> r0 = s.generateParenthesis(0);
                 if (r0.size() == 1 && r0.get(0).isEmpty()) passed++;
                 else System.out.println("FAIL: n=0 -> " + r0);

        // Sanity: every generated string for n=4 is itself well-formed.
        total++; boolean allValid = true;
                 for (String w : s.generateParenthesis(4)) {
                     if (!wellFormed(w)) { allValid = false; break; }
                 }
                 if (allValid) passed++; else System.out.println("FAIL: some n=4 string was not well-formed");

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static boolean wellFormed(String w) {
        int depth = 0;
        for (char c : w.toCharArray()) {
            if (c == '(') depth++;
            else if (c == ')') depth--;
            if (depth < 0) return false;
        }
        return depth == 0;
    }
}
