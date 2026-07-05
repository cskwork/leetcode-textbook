public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18});
        if (r1 == 4) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3});
        if (r2 == 4) passed++;   // [0,1,2,3]
        else System.out.println("FAIL: LC example 2 -> " + r2);

        total++;
        int r3 = s.lengthOfLIS(new int[]{7, 7, 7, 7, 7, 7, 7});
        if (r3 == 1) passed++;   // strict increase forbids equal
        else System.out.println("FAIL: all equal -> " + r3);

        // Edge: single element.
        total++;
        int r4 = s.lengthOfLIS(new int[]{5});
        if (r4 == 1) passed++;
        else System.out.println("FAIL: single element -> " + r4);

        // Strictly decreasing -> LIS is any single element.
        total++;
        int r5 = s.lengthOfLIS(new int[]{5, 4, 3, 2, 1});
        if (r5 == 1) passed++;
        else System.out.println("FAIL: strictly decreasing -> " + r5);

        // Strictly increasing -> LIS is the whole array.
        total++;
        int r6 = s.lengthOfLIS(new int[]{1, 2, 3, 4, 5});
        if (r6 == 5) passed++;
        else System.out.println("FAIL: strictly increasing -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
