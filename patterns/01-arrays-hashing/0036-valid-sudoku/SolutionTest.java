public class SolutionTest {
    static char[][] board(String... rows) {
        char[][] b = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            b[i] = rows[i].toCharArray();
        }
        return b;
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        char[][] valid = board(
                "53..7....",
                "6..195...",
                ".98....6.",
                "8...6...3",
                "4..8.3..1",
                "7...2...6",
                ".6....28.",
                "...419..5",
                "....8..79");

        total++; boolean r1 = s.isValidSudoku(valid);
                  if (r1) passed++; else System.out.println("FAIL: valid board -> " + r1);

        char[][] badRow = board(
                "53..7...5",
                "6..195...",
                ".98....6.",
                "8...6...3",
                "4..8.3..1",
                "7...2...6",
                ".6....28.",
                "...419..5",
                "....8..79");

        total++; boolean r2 = s.isValidSudoku(badRow);
                  if (!r2) passed++; else System.out.println("FAIL: dup-in-row -> " + r2);

        char[][] badCol = board(
                "53..7....",
                "6..195...",
                ".98....6.",
                "5...6...3",
                "4..8.3..1",
                "7...2...6",
                ".6....28.",
                "...419..5",
                "....8..79");

        total++; boolean r3 = s.isValidSudoku(badCol);
                  if (!r3) passed++; else System.out.println("FAIL: dup-in-col -> " + r3);

        char[][] badBox = board(
                "5........",
                ".5.......",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........");

        total++; boolean r4 = s.isValidSudoku(badBox);
                  if (!r4) passed++; else System.out.println("FAIL: dup-in-box -> " + r4);

        char[][] empty = board(
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........");

        total++; boolean r5 = s.isValidSudoku(empty);
                  if (r5) passed++; else System.out.println("FAIL: empty board -> " + r5);

        char[][] oneDigit = board(
                "5........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........",
                ".........");

        total++; boolean r6 = s.isValidSudoku(oneDigit);
                  if (r6) passed++; else System.out.println("FAIL: one digit -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
