public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.rob(new int[]{1, 2, 3, 1});
        if (r1 == 4) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.rob(new int[]{2, 7, 9, 3, 1});
        if (r2 == 12) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: single house.
        total++;
        int r3 = s.rob(new int[]{5});
        if (r3 == 5) passed++;
        else System.out.println("FAIL: single house -> " + r3);

        // Edge: two houses -- rob the richer one.
        total++;
        int r4 = s.rob(new int[]{3, 8});
        if (r4 == 8) passed++;
        else System.out.println("FAIL: two houses -> " + r4);

        // All-equal: take every other house.
        total++;
        int r5 = s.rob(new int[]{10, 10, 10, 10, 10});
        if (r5 == 30) passed++;
        else System.out.println("FAIL: all 10s length 5 -> " + r5);

        // Strictly increasing -- pick odd indices.
        total++;
        int r6 = s.rob(new int[]{1, 2, 3, 4, 5, 6});
        if (r6 == 12) passed++;   // 1 + 3 + 5 = 9? No: 2+4+6=12, or 1+3+5=9 -> 12
        else System.out.println("FAIL: increasing -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
