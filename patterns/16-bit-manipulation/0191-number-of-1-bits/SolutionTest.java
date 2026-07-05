public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.hammingWeight(11);
                  if (r1 == 3) passed++; else System.out.println("FAIL: example 11 -> " + r1);

        total++; int r2 = s.hammingWeight(128);
                  if (r2 == 1) passed++; else System.out.println("FAIL: example 128 -> " + r2);

        total++; int r3 = s.hammingWeight(0);
                  if (r3 == 0) passed++; else System.out.println("FAIL: zero -> " + r3);

        total++; int r4 = s.hammingWeight(-1);
                  if (r4 == 32) passed++; else System.out.println("FAIL: all-ones -1 -> " + r4);

        total++; int r5 = s.hammingWeight(Integer.MIN_VALUE);
                  if (r5 == 1) passed++; else System.out.println("FAIL: sign-bit-only MIN_VALUE -> " + r5);

        total++; int r6 = s.hammingWeight(Integer.MAX_VALUE);
                  if (r6 == 31) passed++; else System.out.println("FAIL: MAX_VALUE -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
