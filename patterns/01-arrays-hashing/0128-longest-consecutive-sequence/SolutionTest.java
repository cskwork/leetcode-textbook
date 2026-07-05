public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.longestConsecutive(new int[]{100, 4, 200, 1, 3, 2});
                  if (r1 == 4) passed++; else System.out.println("FAIL: example1 -> " + r1);

        total++; int r2 = s.longestConsecutive(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1});
                  if (r2 == 9) passed++; else System.out.println("FAIL: example2 -> " + r2);

        total++; int r3 = s.longestConsecutive(new int[]{});
                  if (r3 == 0) passed++; else System.out.println("FAIL: empty -> " + r3);

        total++; int r4 = s.longestConsecutive(new int[]{1});
                  if (r4 == 1) passed++; else System.out.println("FAIL: single -> " + r4);

        total++; int r5 = s.longestConsecutive(new int[]{1, 2, 0, 1});
                  if (r5 == 3) passed++; else System.out.println("FAIL: with-duplicate -> " + r5);

        total++; int r6 = s.longestConsecutive(new int[]{9, 1, 4, 7, 3, -1, 0, 5, 8, -1, 6});
                  if (r6 == 7) passed++; else System.out.println("FAIL: negatives -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
