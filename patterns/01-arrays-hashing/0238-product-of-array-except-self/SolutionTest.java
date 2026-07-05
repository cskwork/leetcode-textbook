import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int[] r1 = s.productExceptSelf(new int[]{1, 2, 3, 4});
                  if (Arrays.equals(r1, new int[]{24, 12, 8, 6})) passed++;
                  else System.out.println("FAIL: example -> " + Arrays.toString(r1));

        total++; int[] r2 = s.productExceptSelf(new int[]{-1, 1, 0, -3, 3});
                  if (Arrays.equals(r2, new int[]{0, 0, 9, 0, 0})) passed++;
                  else System.out.println("FAIL: with-zero -> " + Arrays.toString(r2));

        total++; int[] r3 = s.productExceptSelf(new int[]{2, 3});
                  if (Arrays.equals(r3, new int[]{3, 2})) passed++;
                  else System.out.println("FAIL: two-elems -> " + Arrays.toString(r3));

        total++; int[] r4 = s.productExceptSelf(new int[]{0, 0});
                  if (Arrays.equals(r4, new int[]{0, 0})) passed++;
                  else System.out.println("FAIL: two-zeros -> " + Arrays.toString(r4));

        total++; int[] r5 = s.productExceptSelf(new int[]{0, 4, 0});
                  if (Arrays.equals(r5, new int[]{0, 0, 0})) passed++;
                  else System.out.println("FAIL: two-zeros-spread -> " + Arrays.toString(r5));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
