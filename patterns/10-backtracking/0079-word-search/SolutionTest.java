import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        char[][] board = {
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}
        };

        // LeetCode examples.
        total++; if (s.exist(copy(board), "ABCCED")) passed++;
                  else System.out.println("FAIL: ABCCED -> false");
        total++; if (s.exist(copy(board), "SEE")) passed++;
                  else System.out.println("FAIL: SEE -> false");
        total++; if (!s.exist(copy(board), "ABCB")) passed++;
                  else System.out.println("FAIL: ABCB -> true (want false)");

        // Single-cell board.
        total++; if (s.exist(new char[][]{{'a'}}, "a")) passed++;
                  else System.out.println("FAIL: single 'a' -> false");
        total++; if (!s.exist(new char[][]{{'a'}}, "b")) passed++;
                  else System.out.println("FAIL: single 'a' vs 'b' -> true (want false)");

        // Word longer than the board has cells -> impossible.
        total++; if (!s.exist(new char[][]{{'a', 'b'}}, "aba")) passed++;
                  else System.out.println("FAIL: 'aba' on [a,b] -> true (want false)");

        // Reuse blocked: "aaa" on a 3-cell line of a's needs 3 distinct cells.
        total++; if (s.exist(new char[][]{{'a', 'a', 'a'}}, "aaa")) passed++;
                  else System.out.println("FAIL: 'aaa' on [a,a,a] -> false");

        // Character not on the board at all -> fast prune, false.
        total++; if (!s.exist(copy(board), "XYZ")) passed++;
                  else System.out.println("FAIL: XYZ -> true (want false)");

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    // Defensive copy so each test starts from the unmutated board; the
    // solution mutates the board in place during its search.
    private static char[][] copy(char[][] b) {
        char[][] out = new char[b.length][];
        for (int i = 0; i < b.length; i++) out[i] = b[i].clone();
        return out;
    }
}
