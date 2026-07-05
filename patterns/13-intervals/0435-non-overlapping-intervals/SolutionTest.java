import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // LC example 1.
        total++;
        int r1 = s.eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 3}});
        if (r1 == 1) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        // LC example 2: three identical intervals.
        total++;
        int r2 = s.eraseOverlapIntervals(new int[][]{{1, 2}, {1, 2}, {1, 2}});
        if (r2 == 2) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // LC example 3: touching intervals are non-overlapping.
        total++;
        int r3 = s.eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}});
        if (r3 == 0) passed++;
        else System.out.println("FAIL: LC example 3 touching -> " + r3);

        // Edge: empty input.
        total++;
        int r4 = s.eraseOverlapIntervals(new int[][]{});
        if (r4 == 0) passed++;
        else System.out.println("FAIL: empty input -> " + r4);

        // Edge: single interval -> nothing to remove.
        total++;
        int r5 = s.eraseOverlapIntervals(new int[][]{{5, 9}});
        if (r5 == 0) passed++;
        else System.out.println("FAIL: single interval -> " + r5);

        // The start-sort counterexample: long early interval blocks two short ones.
        total++;
        int r6 = s.eraseOverlapIntervals(new int[][]{{1, 10}, {2, 3}, {4, 5}});
        if (r6 == 1) passed++;
        else System.out.println("FAIL: long-blocks-short -> " + r6);

        // Fully nested intervals: only one can survive.
        total++;
        int r7 = s.eraseOverlapIntervals(new int[][]{{1, 10}, {2, 5}, {3, 4}});
        if (r7 == 2) passed++;
        else System.out.println("FAIL: nested -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
