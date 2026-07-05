import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int[] r1 = s.twoSum(new int[]{2, 7, 11, 15}, 9);
                  if (r1[0] == 0 && r1[1] == 1) passed++;
                  else System.out.println("FAIL: example1 -> " + Arrays.toString(r1));

        total++; int[] r2 = s.twoSum(new int[]{3, 2, 4}, 6);
                  if (r2[0] == 1 && r2[1] == 2) passed++;
                  else System.out.println("FAIL: example2 -> " + Arrays.toString(r2));

        total++; int[] r3 = s.twoSum(new int[]{3, 3}, 6);
                  if (r3[0] == 0 && r3[1] == 1) passed++;
                  else System.out.println("FAIL: duplicate-values -> " + Arrays.toString(r3));

        total++; int[] r4 = s.twoSum(new int[]{0, 4, 3, 0}, 0);
                  if (r4[0] == 0 && r4[1] == 3) passed++;
                  else System.out.println("FAIL: zeros -> " + Arrays.toString(r4));

        total++; int[] r5 = s.twoSum(new int[]{-1, -2, -3, -4, -5}, -8);
                  if (r5[0] == 2 && r5[1] == 4) passed++;
                  else System.out.println("FAIL: negatives -> " + Arrays.toString(r5));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
