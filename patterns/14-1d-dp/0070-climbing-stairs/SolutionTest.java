public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.climbStairs(2);
        if (r1 == 2) passed++;
        else System.out.println("FAIL: LC example n=2 -> " + r1);

        total++;
        int r2 = s.climbStairs(3);
        if (r2 == 3) passed++;
        else System.out.println("FAIL: LC example n=3 -> " + r2);

        // Edge: smallest input.
        total++;
        int r3 = s.climbStairs(1);
        if (r3 == 1) passed++;
        else System.out.println("FAIL: n=1 -> " + r3);

        // Fibonacci check: ways(5) = 8.
        total++;
        int r4 = s.climbStairs(5);
        if (r4 == 8) passed++;
        else System.out.println("FAIL: n=5 -> " + r4);

        // Larger case.
        total++;
        int r5 = s.climbStairs(10);
        if (r5 == 89) passed++;
        else System.out.println("FAIL: n=10 -> " + r5);

        // Upper constraint: ways(45) = 1836311903 (still fits in int).
        total++;
        int r6 = s.climbStairs(45);
        if (r6 == 1836311903) passed++;
        else System.out.println("FAIL: n=45 -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
