public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r = s.characterReplacement("ABAB", 2);
                 if (r == 4) passed++; else System.out.println("FAIL: example 1 \"ABAB\" k=2 -> " + r);

        total++; r = s.characterReplacement("AABABBA", 1);
                 if (r == 4) passed++; else System.out.println("FAIL: example 2 \"AABABBA\" k=1 -> " + r);

        total++; r = s.characterReplacement("AAAA", 0);
                 if (r == 4) passed++; else System.out.println("FAIL: \"AAAA\" k=0 -> " + r);

        total++; r = s.characterReplacement("", 2);
                 if (r == 0) passed++; else System.out.println("FAIL: empty string -> " + r);

        total++; r = s.characterReplacement("A", 0);
                 if (r == 1) passed++; else System.out.println("FAIL: single char \"A\" k=0 -> " + r);

        total++; r = s.characterReplacement("ABBB", 1);
                 if (r == 4) passed++; else System.out.println("FAIL: \"ABBB\" k=1 -> " + r);

        total++; r = s.characterReplacement("ABABABA", 0);
                 if (r == 1) passed++; else System.out.println("FAIL: \"ABABABA\" k=0 -> " + r);

        total++; r = s.characterReplacement("BAAA", 0);
                 if (r == 3) passed++; else System.out.println("FAIL: \"BAAA\" k=0 -> " + r);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
