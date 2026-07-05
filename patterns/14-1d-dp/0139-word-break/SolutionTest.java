import java.util.List;
import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        boolean r1 = s.wordBreak("leetcode", Arrays.asList("leet", "code"));
        if (r1) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        boolean r2 = s.wordBreak("applepenapple", Arrays.asList("apple", "pen"));
        if (r2) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        total++;
        boolean r3 = s.wordBreak("catsandog", Arrays.asList("cats", "dog", "sand", "and", "cat"));
        if (!r3) passed++;
        else System.out.println("FAIL: LC example 3 (should be false) -> " + r3);

        // Edge: empty string is trivially segmentable.
        total++;
        boolean r4 = s.wordBreak("", Arrays.asList("a", "b"));
        if (r4) passed++;
        else System.out.println("FAIL: empty string -> " + r4);

        // Edge: single-character string present in dict.
        total++;
        boolean r5 = s.wordBreak("a", Arrays.asList("a"));
        if (r5) passed++;
        else System.out.println("FAIL: single char present -> " + r5);

        // Word reuse: "aaaaaaa" = "a"*7 with dict {"a", "aa"}.
        total++;
        boolean r6 = s.wordBreak("aaaaaaa", Arrays.asList("a", "aa"));
        if (r6) passed++;
        else System.out.println("FAIL: reuse -> " + r6);

        // Cannot segment: the last char is unreachable.
        total++;
        boolean r7 = s.wordBreak("ab", Arrays.asList("a"));
        if (!r7) passed++;
        else System.out.println("FAIL: unreachable tail (should be false) -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
