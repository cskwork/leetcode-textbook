import java.util.*;

class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] cur : intervals) {
            if (!merged.isEmpty() && cur[0] <= merged.get(merged.size() - 1)[1]) {
                // Overlap (incl. touching): extend the end, never shrink it.
                merged.get(merged.size() - 1)[1] =
                        Math.max(merged.get(merged.size() - 1)[1], cur[1]);
            } else {
                merged.add(cur);
            }
        }
        return merged.toArray(new int[0][]);
    }
}
