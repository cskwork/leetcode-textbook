public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.numIslands(toGrid(new String[]{
                     "11110",
                     "11010",
                     "11000",
                     "00000"}));
                 if (r1 == 1) passed++; else System.out.println("FAIL: example 1 -> " + r1);

        total++; int r2 = s.numIslands(toGrid(new String[]{
                     "11000",
                     "11000",
                     "00100",
                     "00011"}));
                 if (r2 == 3) passed++; else System.out.println("FAIL: example 2 -> " + r2);

        total++; int r3 = s.numIslands(toGrid(new String[]{"0"}));
                 if (r3 == 0) passed++; else System.out.println("FAIL: single water -> " + r3);

        total++; int r4 = s.numIslands(toGrid(new String[]{"1"}));
                 if (r4 == 1) passed++; else System.out.println("FAIL: single land -> " + r4);

        // All land -> one giant island.
        total++; int r5 = s.numIslands(toGrid(new String[]{
                     "111",
                     "111",
                     "111"}));
                 if (r5 == 1) passed++; else System.out.println("FAIL: all land -> " + r5);

        // Checkerboard -> 5 islands (no two 1s are 4-connected).
        total++; int r6 = s.numIslands(toGrid(new String[]{
                     "101",
                     "010",
                     "101"}));
                 if (r6 == 5) passed++; else System.out.println("FAIL: checkerboard -> " + r6);

        // All water.
        total++; int r7 = s.numIslands(toGrid(new String[]{
                     "0000",
                     "0000"}));
                 if (r7 == 0) passed++; else System.out.println("FAIL: all water -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static char[][] toGrid(String[] rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            g[i] = rows[i].toCharArray();
        }
        return g;
    }
}
