import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Order does not matter, so compare as sets.
        total++; Set<String> r1 = new HashSet<>(s.letterCombinations("23"));
                  Set<String> e1 = new HashSet<>(Arrays.asList(
                          "ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"));
                  if (r1.equals(e1)) passed++; else System.out.println("FAIL: \"23\" -> " + r1);

        total++; List<String> r0 = s.letterCombinations("");
                  if (r0.isEmpty()) passed++; else System.out.println("FAIL: \"\" -> " + r0);

        total++; Set<String> r2 = new HashSet<>(s.letterCombinations("2"));
                  Set<String> e2 = new HashSet<>(Arrays.asList("a", "b", "c"));
                  if (r2.equals(e2)) passed++; else System.out.println("FAIL: \"2\" -> " + r2);

        // Digits that map to 4 letters (7 and 9) -> 4^n output size.
        total++; int c79 = s.letterCombinations("79").size();
                  if (c79 == 16) passed++; else System.out.println("FAIL: |\"79\"| = " + c79 + " (want 16)");

        // Three-digit combination count: 3 * 3 * 3 = 27.
        total++; int c234 = s.letterCombinations("234").size();
                  if (c234 == 27) passed++; else System.out.println("FAIL: |\"234\"| = " + c234 + " (want 27)");

        // Every output string must have length == digits.length().
        total++; boolean allLen = true;
                  for (String w : s.letterCombinations("2345")) {
                      if (w.length() != 4) { allLen = false; break; }
                  }
                  if (allLen) passed++; else System.out.println("FAIL: some output of \"2345\" had wrong length");

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
