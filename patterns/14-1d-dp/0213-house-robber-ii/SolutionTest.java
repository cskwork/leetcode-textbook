public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.rob(new int[]{2, 3, 2});
        if (r1 == 3) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.rob(new int[]{1, 2, 3, 1});
        if (r2 == 4) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        total++;
        int r3 = s.rob(new int[]{1, 2, 3});
        if (r3 == 3) passed++;
        else System.out.println("FAIL: LC example 3 -> " + r3);

        // Edge: single house -- the n==1 guard.
        total++;
        int r4 = s.rob(new int[]{7});
        if (r4 == 7) passed++;
        else System.out.println("FAIL: single house -> " + r4);

        // Edge: two houses -- rob the richer; the circle makes them adjacent.
        total++;
        int r5 = s.rob(new int[]{5, 9});
        if (r5 == 9) passed++;
        else System.out.println("FAIL: two houses -> " + r5);

        // All-equal: circle caps you at 2 houses (3 would force adjacency).
        total++;
        int r6 = s.rob(new int[]{10, 10, 10, 10, 10});
        if (r6 == 20) passed++;
        else System.out.println("FAIL: all 10s length 5 -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
