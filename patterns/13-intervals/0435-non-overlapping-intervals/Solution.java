import java.util.*;

class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;
        // Sort by END: the exchange argument requires the earliest-ending
        // interval be considered first, so it is the safe one to keep.
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int removed = 0;
        int prevEnd = Integer.MIN_VALUE;
        for (int[] cur : intervals) {
            if (cur[0] >= prevEnd) {
                prevEnd = cur[1];          // keep: extend the frontier
            } else {
                removed++;                 // drop: conflicts with a kept interval
            }
        }
        return removed;
    }
}
