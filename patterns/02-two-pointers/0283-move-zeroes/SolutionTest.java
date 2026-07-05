import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int[] a1 = new int[]{0, 1, 0, 3, 12};
        s.moveZeroes(a1);
        if (Arrays.equals(a1, new int[]{1, 3, 12, 0, 0})) passed++;
        else System.out.println("FAIL: LC example 1 -> " + Arrays.toString(a1));

        total++;
        int[] a2 = new int[]{0};
        s.moveZeroes(a2);
        if (Arrays.equals(a2, new int[]{0})) passed++;
        else System.out.println("FAIL: LC example 2 (single zero) -> " + Arrays.toString(a2));

        total++;
        int[] a3 = new int[]{1, 2, 3};
        s.moveZeroes(a3);
        if (Arrays.equals(a3, new int[]{1, 2, 3})) passed++;
        else System.out.println("FAIL: no zeroes -> " + Arrays.toString(a3));

        total++;
        int[] a4 = new int[]{0, 0, 0, 0};
        s.moveZeroes(a4);
        if (Arrays.equals(a4, new int[]{0, 0, 0, 0})) passed++;
        else System.out.println("FAIL: all zeroes -> " + Arrays.toString(a4));

        total++;
        int[] a5 = new int[]{1, 0, 2, 0, 3, 0, 4};
        s.moveZeroes(a5);
        if (Arrays.equals(a5, new int[]{1, 2, 3, 4, 0, 0, 0})) passed++;
        else System.out.println("FAIL: interleaved -> " + Arrays.toString(a5));

        total++;
        int[] a6 = new int[]{};
        s.moveZeroes(a6);
        if (Arrays.equals(a6, new int[]{})) passed++;
        else System.out.println("FAIL: empty array -> " + Arrays.toString(a6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
