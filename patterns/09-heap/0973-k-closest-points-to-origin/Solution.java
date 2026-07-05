import java.util.*;

class Solution {
    public int[][] kClosest(int[][] points, int k) {
        // max-heap of size k keyed on squared distance: root = farthest survivor, evicted when overfull
        PriorityQueue<int[]> heap = new PriorityQueue<>(
            (a, b) -> Integer.compare(b[0], a[0]));

        for (int[] p : points) {
            int d = p[0] * p[0] + p[1] * p[1];
            heap.offer(new int[]{d, p[0], p[1]});
            if (heap.size() > k) {
                heap.poll();
            }
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            int[] e = heap.poll();
            result[i][0] = e[1];
            result[i][1] = e[2];
        }
        return result;
    }
}
