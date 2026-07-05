class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private int[][] grid;
    private int rows, cols;

    public int maxAreaOfIsland(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        int best = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    best = Math.max(best, area(r, c));
                }
            }
        }
        return best;
    }

    private int area(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != 1) {
            return 0;
        }
        grid[r][c] = 0;
        int size = 1;
        for (int k = 0; k < 4; k++) {
            size += area(r + DR[k], c + DC[k]);
        }
        return size;
    }
}
