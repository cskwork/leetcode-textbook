import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int[] r1 = s.sortedSquares(new int[]{-4, -1, 0, 3, 10});
        if (Arrays.equals(r1, new int[]{0, 1, 9, 16, 100})) passed++;
        else System.out.println("FAIL: LC example 1 -> " + Arrays.toString(r1));

        total++;
        int[] r2 = s.sortedSquares(new int[]{-7, -3, 2, 3, 11});
        if (Arrays.equals(r2, new int[]{4, 9, 9, 49, 121})) passed++;
        else System.out.println("FAIL: LC example 2 -> " + Arrays.toString(r2));

        total++;
        int[] r3 = s.sortedSquares(new int[]{1});
        if (Arrays.equals(r3, new int[]{1})) passed++;
        else System.out.println("FAIL: single element -> " + Arrays.toString(r3));

        total++;
        int[] r4 = s.sortedSquares(new int[]{0});
        if (Arrays.equals(r4, new int[]{0})) passed++;
        else System.out.println("FAIL: single zero -> " + Arrays.toString(r4));

        total++;
        int[] r5 = s.sortedSquares(new int[]{-5, -3, -2, -1});
        if (Arrays.equals(r5, new int[]{1, 4, 9, 25})) passed++;
        else System.out.println("FAIL: all negative -> " + Arrays.toString(r5));

        total++;
        int[] r6 = s.sortedSquares(new int[]{1, 2, 3, 4, 5});
        if (Arrays.equals(r6, new int[]{1, 4, 9, 16, 25})) passed++;
        else System.out.println("FAIL: all positive -> " + Arrays.toString(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
