public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.singleNumber(new int[]{2, 2, 1});
                  if (r1 == 1) passed++; else System.out.println("FAIL: example [2,2,1] -> " + r1);

        total++; int r2 = s.singleNumber(new int[]{4, 1, 2, 1, 2});
                  if (r2 == 4) passed++; else System.out.println("FAIL: example [4,1,2,1,2] -> " + r2);

        total++; int r3 = s.singleNumber(new int[]{1});
                  if (r3 == 1) passed++; else System.out.println("FAIL: single [1] -> " + r3);

        total++; int r4 = s.singleNumber(new int[]{0});
                  if (r4 == 0) passed++; else System.out.println("FAIL: single-zero [0] -> " + r4);

        total++; int r5 = s.singleNumber(new int[]{-3, -3, -7});
                  if (r5 == -7) passed++; else System.out.println("FAIL: negatives [-3,-3,-7] -> " + r5);

        total++; int r6 = s.singleNumber(new int[]{6, 9, 6, 9, 7});
                  if (r6 == 7) passed++; else System.out.println("FAIL: larger [6,9,6,9,7] -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
