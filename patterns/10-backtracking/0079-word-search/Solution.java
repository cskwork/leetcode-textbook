import java.util.*;

class Solution {
    public boolean exist(char[][] board, String word) {
        int rows = board.length, cols = board[0].length;

        // Cheap prune: if the board lacks enough of some character, bail out.
        int[] freq = new int[128];
        for (char[] row : board) for (char ch : row) freq[ch]++;
        for (char ch : word.toCharArray()) {
            if (--freq[ch] < 0) return false;
        }

        // Walk every cell; start a DFS wherever the first char matches.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == word.charAt(0)
                        && backtrack(board, r, c, word, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean backtrack(char[][] board, int r, int c, String word, int index) {
        if (index == word.length() - 1) return true;       // last char already matched
        char saved = board[r][c];
        board[r][c] = '#';                                  // CHOOSE: mark visited in place
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr >= 0 && nr < board.length
                    && nc >= 0 && nc < board[0].length
                    && board[nr][nc] == word.charAt(index + 1)
                    && backtrack(board, nr, nc, word, index + 1)) {
                return true;                                // EXPLORE: first hit wins
            }
        }
        board[r][c] = saved;                               // UN-CHOOSE: restore the cell
        return false;
    }
}
