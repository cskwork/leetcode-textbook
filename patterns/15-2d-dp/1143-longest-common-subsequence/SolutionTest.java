public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.longestCommonSubsequence("abcde", "ace");
                 if (r1 == 3) passed++; else System.out.println("FAIL: abcde/ace -> " + r1);

        total++; int r2 = s.longestCommonSubsequence("abc", "abc");
                 if (r2 == 3) passed++; else System.out.println("FAIL: abc/abc -> " + r2);

        total++; int r3 = s.longestCommonSubsequence("abc", "def");
                 if (r3 == 0) passed++; else System.out.println("FAIL: abc/def -> " + r3);

        total++; int r4 = s.longestCommonSubsequence("", "");
                 if (r4 == 0) passed++; else System.out.println("FAIL: empty/empty -> " + r4);

        total++; int r5 = s.longestCommonSubsequence("a", "a");
                 if (r5 == 1) passed++; else System.out.println("FAIL: a/a -> " + r5);

        total++; int r6 = s.longestCommonSubsequence("a", "b");
                 if (r6 == 0) passed++; else System.out.println("FAIL: a/b -> " + r6);

        total++; int r7 = s.longestCommonSubsequence("abcdef", "abcdef");
                 if (r7 == 6) passed++; else System.out.println("FAIL: identical length 6 -> " + r7);

        total++; int r8 = s.longestCommonSubsequence("bsbininm", "b")
                 ;
                 if (r8 == 1) passed++; else System.out.println("FAIL: single char common -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
