public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.countSubstrings("abc");
                 if (r1 == 3) passed++; else System.out.println("FAIL: abc -> " + r1);

        total++; int r2 = s.countSubstrings("aaa");
                 if (r2 == 6) passed++; else System.out.println("FAIL: aaa -> " + r2);

        total++; int r3 = s.countSubstrings("a");
                 if (r3 == 1) passed++; else System.out.println("FAIL: single char -> " + r3);

        total++; int r4 = s.countSubstrings("");
                 if (r4 == 0) passed++; else System.out.println("FAIL: empty -> " + r4);

        total++; int r5 = s.countSubstrings("aa");
                 if (r5 == 3) passed++; else System.out.println("FAIL: aa -> " + r5);

        total++; int r6 = s.countSubstrings("aba");
                 if (r6 == 4) passed++; else System.out.println("FAIL: aba -> " + r6);

        total++; int r7 = s.countSubstrings("abba");
                 if (r7 == 6) passed++; else System.out.println("FAIL: abba -> " + r7);

        total++; int r8 = s.countSubstrings("abcba");
                 if (r8 == 7) passed++; else System.out.println("FAIL: abcba -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
