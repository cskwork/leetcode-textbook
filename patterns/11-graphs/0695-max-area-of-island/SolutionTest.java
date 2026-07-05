public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.maxAreaOfIsland(new int[][]{
                     {0,0,1,0,0,0,0,1,0,0,0,0,0},
                     {0,0,0,0,0,0,0,1,1,1,0,0,0},
                     {0,1,1,0,1,0,0,0,0,0,0,0,0},
                     {0,1,0,0,1,1,0,0,1,0,1,0,0},
                     {0,1,0,0,1,1,0,0,1,1,1,0,0},
                     {0,0,0,0,0,0,0,0,0,0,1,0,0},
                     {0,0,0,0,0,0,0,1,1,1,0,0,0},
                     {0,0,0,0,0,0,0,1,1,0,0,0,0}});
                 if (r1 == 6) passed++; else System.out.println("FAIL: example 1 -> " + r1);

        total++; int r2 = s.maxAreaOfIsland(new int[][]{{0,0,0,0,0,0,0,0}});
                 if (r2 == 0) passed++; else System.out.println("FAIL: all water row -> " + r2);

        total++; int r3 = s.maxAreaOfIsland(new int[][]{{0}});
                 if (r3 == 0) passed++; else System.out.println("FAIL: single water -> " + r3);

        total++; int r4 = s.maxAreaOfIsland(new int[][]{{1}});
                 if (r4 == 1) passed++; else System.out.println("FAIL: single land -> " + r4);

        // Whole grid one island.
        total++; int r5 = s.maxAreaOfIsland(new int[][]{
                     {1,1},
                     {1,1}});
                 if (r5 == 4) passed++; else System.out.println("FAIL: 2x2 all land -> " + r5);

        // Several islands of areas 2, 4, 3 -> best is 4.
        total++; int r6 = s.maxAreaOfIsland(new int[][]{
                     {0,1,1,0,0,0},
                     {0,0,0,0,0,0},
                     {1,1,1,1,0,1},
                     {0,0,0,0,0,1},
                     {0,0,0,0,0,1}});
                 if (r6 == 4) passed++; else System.out.println("FAIL: mixed islands -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
