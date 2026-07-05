import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Case 1: LeetCode example 1 -- chain of growing words.
        {
            String[] words = {"w", "wo", "wor", "worl", "world"};
            total++; String r = s.longestWord(words);
                     if (r.equals("world")) passed++;
                     else System.out.println("FAIL: example1 -> " + r);
        }

        // Case 2: LeetCode example 2 -- lexicographic tie-break.
        {
            String[] words = {"a", "banana", "app", "appl", "ap", "apply", "apple"};
            total++; String r = s.longestWord(words);
                     if (r.equals("apple")) passed++;
                     else System.out.println("FAIL: example2 tie-break -> " + r);
        }

        // Case 3: single character only.
        {
            String[] words = {"a"};
            total++; String r = s.longestWord(words);
                     if (r.equals("a")) passed++;
                     else System.out.println("FAIL: single char -> " + r);
        }

        // Case 4: longer word blocked because a prefix is missing.
        {
            // "world" is in the list but "worl" is not, so "world" is invalid;
            // "wor" is the longest fully-built word.
            String[] words = {"a", "w", "wo", "wor", "world"};
            total++; String r = s.longestWord(words);
                     if (r.equals("wor")) passed++;
                     else System.out.println("FAIL: missing prefix blocks longer word -> " + r);
        }

        // Case 5: two equally long valid chains, pick lex-smaller.
        {
            // Both "ab" and "ac" are reachable (a -> ab, a -> ac); tie, pick "ab".
            String[] words = {"a", "ab", "ac"};
            total++; String r = s.longestWord(words);
                     if (r.equals("ab")) passed++;
                     else System.out.println("FAIL: lex tie at length 2 -> " + r);
        }

        // Case 6: scattered words with no chain longer than 1.
        {
            String[] words = {"m", "n", "o", "p"};
            total++; String r = s.longestWord(words);
                     if (r.equals("m")) passed++;
                     else System.out.println("FAIL: scattered singletons -> " + r);
        }

        // Case 7: ensure longer chain beats a longer isolated word.
        {
            // "antenna" exists as a literal but "antenn" is missing, so "antenna" is invalid.
            // "ant" is buildable: a -> an -> ant.
            String[] words = {"a", "an", "ant", "antenna", "z"};
            total++; String r = s.longestWord(words);
                     if (r.equals("ant")) passed++;
                     else System.out.println("FAIL: long literal without chain -> " + r);
        }

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
