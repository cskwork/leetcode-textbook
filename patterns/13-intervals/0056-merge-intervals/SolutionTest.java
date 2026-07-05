import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // LC example 1.
        total++;
        int[][] r1 = s.merge(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}});
        if (sameIntervalSet(r1, new int[][]{{1, 6}, {8, 10}, {15, 18}})) passed++;
        else System.out.println("FAIL: LC example 1 -> " + fmt(r1));

        // LC example 2: touching intervals merge.
        total++;
        int[][] r2 = s.merge(new int[][]{{1, 4}, {4, 5}});
        if (sameIntervalSet(r2, new int[][]{{1, 5}})) passed++;
        else System.out.println("FAIL: LC example 2 touching -> " + fmt(r2));

        // Edge: single interval.
        total++;
        int[][] r3 = s.merge(new int[][]{{7, 9}});
        if (sameIntervalSet(r3, new int[][]{{7, 9}})) passed++;
        else System.out.println("FAIL: single interval -> " + fmt(r3));

        // Edge: empty input.
        total++;
        int[][] r4 = s.merge(new int[][]{});
        if (sameIntervalSet(r4, new int[][]{})) passed++;
        else System.out.println("FAIL: empty input -> " + fmt(r4));

        // Unsorted input with a fully nested interval.
        total++;
        int[][] r5 = s.merge(new int[][]{{2, 5}, {1, 10}, {6, 8}});
        if (sameIntervalSet(r5, new int[][]{{1, 10}})) passed++;
        else System.out.println("FAIL: nested unsorted -> " + fmt(r5));

        // Back-to-back touching intervals that all chain together.
        total++;
        int[][] r6 = s.merge(new int[][]{{1, 2}, {2, 3}, {3, 4}});
        if (sameIntervalSet(r6, new int[][]{{1, 4}})) passed++;
        else System.out.println("FAIL: chained touching -> " + fmt(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    // Compare two int[][] interval sets ignoring row order: sort rows then deep-equals.
    private static boolean sameIntervalSet(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        int[][] ca = copySort(a);
        int[][] cb = copySort(b);
        return Arrays.deepEquals(ca, cb);
    }

    private static int[][] copySort(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = a[i].clone();
        Arrays.sort(c, (x, y) -> x[0] != y[0] ? Integer.compare(x[0], y[0])
                                              : Integer.compare(x[1], y[1]));
        return c;
    }

    private static String fmt(int[][] a) {
        return Arrays.deepToString(a);
    }
}
