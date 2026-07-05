public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.minDistance("horse", "ros");
                 if (r1 == 3) passed++; else System.out.println("FAIL: horse/ros -> " + r1);

        total++; int r2 = s.minDistance("intention", "execution");
                 if (r2 == 5) passed++; else System.out.println("FAIL: intention/execution -> " + r2);

        total++; int r3 = s.minDistance("", "");
                 if (r3 == 0) passed++; else System.out.println("FAIL: empty/empty -> " + r3);

        total++; int r4 = s.minDistance("abc", "");
                 if (r4 == 3) passed++; else System.out.println("FAIL: abc/empty -> " + r4);

        total++; int r5 = s.minDistance("", "abc");
                 if (r5 == 3) passed++; else System.out.println("FAIL: empty/abc -> " + r5);

        total++; int r6 = s.minDistance("abc", "abc");
                 if (r6 == 0) passed++; else System.out.println("FAIL: identical -> " + r6);

        total++; int r7 = s.minDistance("a", "b");
                 if (r7 == 1) passed++; else System.out.println("FAIL: a/b (replace) -> " + r7);

        total++; int r8 = s.minDistance("ab", "a");
                 if (r8 == 1) passed++; else System.out.println("FAIL: ab/a (delete) -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
