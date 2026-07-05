import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; char[][] b1 = toBoard(new String[]{
                      "XXXX",
                      "XOOX",
                      "XXOX",
                      "XOXX"});
                  s.solve(b1);
                  if (Arrays.deepEquals(b1, toBoard(new String[]{
                      "XXXX",
                      "XXXX",
                      "XXXX",
                      "XOXX"}))) passed++;
                  else System.out.println("FAIL: example 1 -> " + render(b1));

        // All O border -> nothing flips.
        total++; char[][] b2 = toBoard(new String[]{
                      "OOOO",
                      "OXXO",
                      "OXXO",
                      "OOOO"});
                  s.solve(b2);
                  if (Arrays.deepEquals(b2, toBoard(new String[]{
                      "OOOO",
                      "OXXO",
                      "OXXO",
                      "OOOO"}))) passed++;
                  else System.out.println("FAIL: border-safe -> " + render(b2));

        // Fully surrounded single O.
        total++; char[][] b3 = toBoard(new String[]{
                      "XXX",
                      "XOX",
                      "XXX"});
                  s.solve(b3);
                  if (Arrays.deepEquals(b3, toBoard(new String[]{
                      "XXX",
                      "XXX",
                      "XXX"}))) passed++;
                  else System.out.println("FAIL: single surrounded -> " + render(b3));

        // 1x1 board.
        total++; char[][] b4 = toBoard(new String[]{"O"});
                  s.solve(b4);
                  if (Arrays.deepEquals(b4, toBoard(new String[]{"O"}))) passed++;
                  else System.out.println("FAIL: 1x1 O -> " + render(b4));

        // Empty board (no inner dimension) -> must not throw.
        total++; char[][] b5 = new char[0][];
                  s.solve(b5);
                  if (b5.length == 0) passed++;
                  else System.out.println("FAIL: empty board -> " + render(b5));

        // O corridor from top border to interior survives.
        total++; char[][] b6 = toBoard(new String[]{
                      "OXO",
                      "OXO",
                      "OOO"});
                  s.solve(b6);
                  // The whole O region touches the border -> nothing flips.
                  if (Arrays.deepEquals(b6, toBoard(new String[]{
                      "OXO",
                      "OXO",
                      "OOO"}))) passed++;
                  else System.out.println("FAIL: corridor -> " + render(b6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static char[][] toBoard(String[] rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            g[i] = rows[i].toCharArray();
        }
        return g;
    }

    private static String render(char[][] b) {
        return Arrays.deepToString(b);
    }
}
