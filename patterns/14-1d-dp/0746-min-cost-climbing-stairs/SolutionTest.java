public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.minCostClimbingStairs(new int[]{10, 15, 20});
        if (r1 == 15) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.minCostClimbingStairs(new int[]{1, 100, 1, 1, 1, 100, 1, 1, 100, 1});
        if (r2 == 6) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: smallest allowed input, two steps.
        total++;
        int r3 = s.minCostClimbingStairs(new int[]{0, 0});
        if (r3 == 0) passed++;
        else System.out.println("FAIL: two zeros -> " + r3);

        // Edge: starting at index 1 is cheaper than index 0.
        total++;
        int r4 = s.minCostClimbingStairs(new int[]{100, 1, 1, 100});
        if (r4 == 2) passed++;
        else System.out.println("FAIL: avoid expensive step 0 -> " + r4);

        // All-equal costs: take 2-step jumps the whole way.
        total++;
        int r5 = s.minCostClimbingStairs(new int[]{5, 5, 5, 5, 5});
        if (r5 == 10) passed++;
        else System.out.println("FAIL: all 5s length 5 -> " + r5);

        // Larger monotonic costs.
        total++;
        int r6 = s.minCostClimbingStairs(new int[]{0, 0, 0, 0, 100});
        if (r6 == 0) passed++;
        else System.out.println("FAIL: jump over the 100 -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
