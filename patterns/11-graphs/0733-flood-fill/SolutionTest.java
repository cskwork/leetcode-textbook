import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int[][] r1 = s.floodFill(new int[][]{{1,1,1},{1,1,0},{1,0,1}}, 1, 1, 2);
                 if (deepEquals(r1, new int[][]{{2,2,2},{2,2,0},{2,0,1}})) passed++;
                 else System.out.println("FAIL: example 1 -> " + deepToString(r1));

        total++; int[][] r2 = s.floodFill(new int[][]{{0,0,0},{0,0,0}}, 0, 0, 2);
                 if (deepEquals(r2, new int[][]{{2,2,2},{2,2,2}})) passed++;
                 else System.out.println("FAIL: all-same color -> " + deepToString(r2));

        total++; int[][] r3 = s.floodFill(new int[][]{{1}}, 0, 0, 9);
                 if (deepEquals(r3, new int[][]{{9}})) passed++;
                 else System.out.println("FAIL: single cell -> " + deepToString(r3));

        // newColor equals original: must be a no-op (no infinite recursion).
        total++; int[][] in4 = {{1,1},{1,1}};
                 int[][] r4 = s.floodFill(in4, 0, 0, 1);
                 if (deepEquals(r4, new int[][]{{1,1},{1,1}})) passed++;
                 else System.out.println("FAIL: newColor==original -> " + deepToString(r4));

        // Start pixel is isolated (different color from all neighbors).
        total++; int[][] r5 = s.floodFill(new int[][]{{0,1,0},{1,0,1},{0,1,0}}, 1, 1, 5);
                 if (deepEquals(r5, new int[][]{{0,1,0},{1,5,1},{0,1,0}})) passed++;
                 else System.out.println("FAIL: isolated start -> " + deepToString(r5));

        // Non-square grid; region is a full row.
        total++; int[][] r6 = s.floodFill(new int[][]{{1,1,1,1,1},{1,2,2,2,1},{1,1,1,1,1}}, 1, 2, 9);
                 if (deepEquals(r6, new int[][]{{1,1,1,1,1},{1,9,9,9,1},{1,1,1,1,1}})) passed++;
                 else System.out.println("FAIL: 3x5 island -> " + deepToString(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static boolean deepEquals(int[][] a, int[][] b) {
        return Arrays.deepEquals(a, b);
    }

    private static String deepToString(int[][] a) {
        return Arrays.deepToString(a);
    }
}
