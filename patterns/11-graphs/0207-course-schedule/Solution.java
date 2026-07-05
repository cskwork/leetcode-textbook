import java.util.ArrayList;
import java.util.List;

class Solution {
    private List<List<Integer>> adj;
    private int[] state;

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : prerequisites) {
            adj.get(edge[1]).add(edge[0]);
        }

        state = new int[numCourses];
        for (int c = 0; c < numCourses; c++) {
            if (hasCycle(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCycle(int node) {
        if (state[node] == 1) {
            return true;
        }
        if (state[node] == 2) {
            return false;
        }
        state[node] = 1;
        for (int neighbor : adj.get(node)) {
            if (hasCycle(neighbor)) {
                return true;
            }
        }
        state[node] = 2;
        return false;
    }
}
