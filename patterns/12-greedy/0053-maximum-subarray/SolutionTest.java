public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4});
        if (r1 == 6) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.maxSubArray(new int[]{1});
        if (r2 == 1) passed++;
        else System.out.println("FAIL: single element -> " + r2);

        total++;
        int r3 = s.maxSubArray(new int[]{5, 4, -1, 7, 8});
        if (r3 == 23) passed++;
        else System.out.println("FAIL: LC example 3 -> " + r3);

        // Edge case: all negative -> answer is the single largest element.
        total++;
        int r4 = s.maxSubArray(new int[]{-3, -1, -2});
        if (r4 == -1) passed++;
        else System.out.println("FAIL: all negative -> " + r4);

        // Edge case: single negative element.
        total++;
        int r5 = s.maxSubArray(new int[]{-5});
        if (r5 == -5) passed++;
        else System.out.println("FAIL: single negative -> " + r5);

        // Larger mixed array.
        total++;
        int r6 = s.maxSubArray(new int[]{1, 2, 3, -10, 4, 5});
        if (r6 == 9) passed++;
        else System.out.println("FAIL: mixed with late restart -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
