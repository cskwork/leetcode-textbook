import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public boolean isValidSudoku(char[][] board) {
        List<Set<Character>> rows = new ArrayList<>();
        List<Set<Character>> cols = new ArrayList<>();
        List<Set<Character>> boxes = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            rows.add(new HashSet<>());
            cols.add(new HashSet<>());
            boxes.add(new HashSet<>());
        }

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = board[r][c];
                if (ch == '.') {
                    continue;
                }
                int boxIndex = (r / 3) * 3 + c / 3;
                if (!rows.get(r).add(ch)
                        || !cols.get(c).add(ch)
                        || !boxes.get(boxIndex).add(ch)) {
                    return false;
                }
            }
        }
        return true;
    }
}
