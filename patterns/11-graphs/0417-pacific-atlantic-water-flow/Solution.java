import java.util.ArrayList;
import java.util.List;

class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private int[][] heights;
    private int rows, cols;

    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        this.heights = heights;
        this.rows = heights.length;
        this.cols = heights[0].length;

        boolean[][] pac = new boolean[rows][cols];
        boolean[][] atl = new boolean[rows][cols];

        for (int r = 0; r < rows; r++) {
            dfs(r, 0, pac);
            dfs(r, cols - 1, atl);
        }
        for (int c = 0; c < cols; c++) {
            dfs(0, c, pac);
            dfs(rows - 1, c, atl);
        }

        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pac[r][c] && atl[r][c]) {
                    result.add(List.of(r, c));
                }
            }
        }
        return result;
    }

    private void dfs(int r, int c, boolean[][] reachable) {
        if (reachable[r][c]) {
            return;
        }
        reachable[r][c] = true;
        for (int k = 0; k < 4; k++) {
            int nr = r + DR[k];
            int nc = c + DC[k];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && heights[nr][nc] >= heights[r][c]) {
                dfs(nr, nc, reachable);
            }
        }
    }
}
