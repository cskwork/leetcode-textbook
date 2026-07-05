import java.util.*;

class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int start = newInterval[0];
        int end = newInterval[1];
        boolean placed = false;

        for (int[] cur : intervals) {
            if (cur[1] < start) {
                // Entirely before the new interval.
                result.add(cur);
            } else if (cur[0] > end) {
                // Entirely after the new interval: emit it once, then this one.
                if (!placed) {
                    result.add(new int[]{start, end});
                    placed = true;
                }
                result.add(cur);
            } else {
                // Overlap (incl. touching): absorb cur into the running range.
                start = Math.min(start, cur[0]);
                end = Math.max(end, cur[1]);
            }
        }

        // newInterval belongs at the tail (no later interval triggered phase 3).
        if (!placed) {
            result.add(new int[]{start, end});
        }
        return result.toArray(new int[0][]);
    }
}
