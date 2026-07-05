public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r = s.maxProfit(new int[]{7, 1, 5, 3, 6, 4});
                 if (r == 5) passed++; else System.out.println("FAIL: example 1 [7,1,5,3,6,4] -> " + r);

        total++; r = s.maxProfit(new int[]{7, 6, 4, 3, 1});
                 if (r == 0) passed++; else System.out.println("FAIL: example 2 [7,6,4,3,1] -> " + r);

        total++; r = s.maxProfit(new int[]{});
                 if (r == 0) passed++; else System.out.println("FAIL: empty array -> " + r);

        total++; r = s.maxProfit(new int[]{5});
                 if (r == 0) passed++; else System.out.println("FAIL: single element [5] -> " + r);

        total++; r = s.maxProfit(new int[]{1, 2, 3, 4, 5});
                 if (r == 4) passed++; else System.out.println("FAIL: rising [1,2,3,4,5] -> " + r);

        total++; r = s.maxProfit(new int[]{2, 4, 1});
                 if (r == 2) passed++; else System.out.println("FAIL: dip-after-peak [2,4,1] -> " + r);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
