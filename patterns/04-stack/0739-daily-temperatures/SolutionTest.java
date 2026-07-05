import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int[] r1 = s.dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73});
                 if (Arrays.equals(r1, new int[]{1, 1, 4, 2, 1, 1, 0, 0})) passed++;
                 else System.out.println("FAIL: example 1 -> " + Arrays.toString(r1));

        total++; int[] r2 = s.dailyTemperatures(new int[]{30, 40, 50, 60});
                 if (Arrays.equals(r2, new int[]{1, 1, 1, 0})) passed++;
                 else System.out.println("FAIL: strictly increasing -> " + Arrays.toString(r2));

        total++; int[] r3 = s.dailyTemperatures(new int[]{30, 60, 90});
                 if (Arrays.equals(r3, new int[]{1, 1, 0})) passed++;
                 else System.out.println("FAIL: example 3 -> " + Arrays.toString(r3));

        total++; int[] r4 = s.dailyTemperatures(new int[]{50});
                 if (Arrays.equals(r4, new int[]{0})) passed++;
                 else System.out.println("FAIL: single element -> " + Arrays.toString(r4));

        total++; int[] r5 = s.dailyTemperatures(new int[]{80, 70, 60, 50});
                 if (Arrays.equals(r5, new int[]{0, 0, 0, 0})) passed++;
                 else System.out.println("FAIL: strictly decreasing -> " + Arrays.toString(r5));

        total++; int[] r6 = s.dailyTemperatures(new int[]{70, 70, 70, 70});
                 if (Arrays.equals(r6, new int[]{0, 0, 0, 0})) passed++;
                 else System.out.println("FAIL: all equal (not warmer) -> " + Arrays.toString(r6));

        total++; int[] r7 = s.dailyTemperatures(new int[]{});
                 if (Arrays.equals(r7, new int[]{})) passed++;
                 else System.out.println("FAIL: empty -> " + Arrays.toString(r7));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
