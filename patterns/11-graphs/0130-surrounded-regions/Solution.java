class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private char[][] board;
    private int rows, cols;

    public void solve(char[][] board) {
        if (board.length == 0) {
            return;
        }
        this.board = board;
        this.rows = board.length;
        this.cols = board[0].length;

        for (int r = 0; r < rows; r++) {
            markSafe(r, 0);
            markSafe(r, cols - 1);
        }
        for (int c = 0; c < cols; c++) {
            markSafe(0, c);
            markSafe(rows - 1, c);
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'O') {
                    board[r][c] = 'X';
                } else if (board[r][c] == 'S') {
                    board[r][c] = 'O';
                }
            }
        }
    }

    private void markSafe(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != 'O') {
            return;
        }
        board[r][c] = 'S';
        for (int k = 0; k < 4; k++) {
            markSafe(r + DR[k], c + DC[k]);
        }
    }
}
