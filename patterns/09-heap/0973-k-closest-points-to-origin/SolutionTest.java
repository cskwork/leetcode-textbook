import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Result order is unspecified, so compare as a set of "x,y" strings.
        total++; int[][] r1 = s.kClosest(new int[][]{{1, 3}, {-2, 2}}, 1);
                 if (asSet(r1).equals(setOf("-2,2"))) passed++;
                 else System.out.println("FAIL: example1 -> " + fmt(r1));

        total++; int[][] r2 = s.kClosest(new int[][]{{3, 3}, {5, -1}, {-2, 4}}, 2);
                 if (asSet(r2).equals(setOf("3,3", "-2,4"))) passed++;
                 else System.out.println("FAIL: example2 -> " + fmt(r2));

        // k equals the number of points -> all returned
        total++; int[][] r3 = s.kClosest(new int[][]{{0, 1}, {1, 0}}, 2);
                 if (asSet(r3).equals(setOf("0,1", "1,0"))) passed++;
                 else System.out.println("FAIL: k=all -> " + fmt(r3));

        // Single point
        total++; int[][] r4 = s.kClosest(new int[][]{{5, 5}}, 1);
                 if (asSet(r4).equals(setOf("5,5"))) passed++;
                 else System.out.println("FAIL: single -> " + fmt(r4));

        // Points already at the origin (distance 0) win
        total++; int[][] r5 = s.kClosest(new int[][]{{0, 0}, {1, 1}, {0, 0}, {2, 2}}, 2);
                 if (asSet(r5).equals(setOf("0,0"))) passed++;   // both survivors are origin points
                 else System.out.println("FAIL: origin -> " + fmt(r5));

        // Larger case: dist^2 = [2,1,0,5,4], k=3 -> the 1, 2, 0 entries = [1,1],[1,0],[0,0]
        total++; int[][] r6 = s.kClosest(
                       new int[][]{{1, 1}, {1, 0}, {0, 0}, {1, 2}, {2, 0}}, 3);
                 if (asSet(r6).equals(setOf("1,1", "1,0", "0,0"))) passed++;
                 else System.out.println("FAIL: larger -> " + fmt(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static Set<String> asSet(int[][] a) {
        Set<String> set = new HashSet<>();
        for (int[] p : a) set.add(p[0] + "," + p[1]);
        return set;
    }

    private static Set<String> setOf(String... xs) {
        Set<String> set = new HashSet<>();
        for (String x : xs) set.add(x);
        return set;
    }

    private static String fmt(int[][] a) {
        String[] parts = new String[a.length];
        for (int i = 0; i < a.length; i++) parts[i] = "[" + a[i][0] + "," + a[i][1] + "]";
        return Arrays.toString(parts);
    }
}
