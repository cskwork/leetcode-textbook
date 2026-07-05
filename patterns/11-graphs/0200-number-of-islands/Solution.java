class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private char[][] grid;
    private int rows, cols;

    public int numIslands(char[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    sink(r, c);
                }
            }
        }
        return count;
    }

    private void sink(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != '1') {
            return;
        }
        grid[r][c] = '0';
        for (int k = 0; k < 4; k++) {
            sink(r + DR[k], c + DC[k]);
        }
    }
}
