import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // LC example 1.
        total++;
        int[][] r1 = s.insert(new int[][]{{1, 3}, {6, 9}}, new int[]{2, 5});
        if (sameIntervalSet(r1, new int[][]{{1, 5}, {6, 9}})) passed++;
        else System.out.println("FAIL: LC example 1 -> " + fmt(r1));

        // LC example 2.
        total++;
        int[][] r2 = s.insert(new int[][]{{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}},
                new int[]{4, 8});
        if (sameIntervalSet(r2, new int[][]{{1, 2}, {3, 10}, {12, 16}})) passed++;
        else System.out.println("FAIL: LC example 2 -> " + fmt(r2));

        // Edge: empty intervals -> result is just newInterval.
        total++;
        int[][] r3 = s.insert(new int[][]{}, new int[]{4, 8});
        if (sameIntervalSet(r3, new int[][]{{4, 8}})) passed++;
        else System.out.println("FAIL: empty intervals -> " + fmt(r3));

        // Edge: single existing interval, no overlap, new is before.
        total++;
        int[][] r4 = s.insert(new int[][]{{5, 6}}, new int[]{1, 2});
        if (sameIntervalSet(r4, new int[][]{{1, 2}, {5, 6}})) passed++;
        else System.out.println("FAIL: new before all -> " + fmt(r4));

        // Edge: single existing interval, no overlap, new is after.
        total++;
        int[][] r5 = s.insert(new int[][]{{1, 2}}, new int[]{5, 6});
        if (sameIntervalSet(r5, new int[][]{{1, 2}, {5, 6}})) passed++;
        else System.out.println("FAIL: new after all -> " + fmt(r5));

        // Touching intervals must merge.
        total++;
        int[][] r6 = s.insert(new int[][]{{1, 5}}, new int[]{5, 7});
        if (sameIntervalSet(r6, new int[][]{{1, 7}})) passed++;
        else System.out.println("FAIL: touching merge -> " + fmt(r6));

        // newInterval fully contains every existing interval.
        total++;
        int[][] r7 = s.insert(new int[][]{{2, 3}, {4, 5}, {6, 7}}, new int[]{1, 10});
        if (sameIntervalSet(r7, new int[][]{{1, 10}})) passed++;
        else System.out.println("FAIL: new swallows all -> " + fmt(r7));

        // newInterval is fully nested inside an existing interval.
        total++;
        int[][] r8 = s.insert(new int[][]{{1, 10}}, new int[]{4, 6});
        if (sameIntervalSet(r8, new int[][]{{1, 10}})) passed++;
        else System.out.println("FAIL: new nested inside -> " + fmt(r8));

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
