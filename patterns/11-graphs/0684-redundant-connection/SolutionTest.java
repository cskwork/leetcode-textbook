import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int[] r1 = s.findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}});
                  if (Arrays.equals(r1, new int[]{2,3})) passed++;
                  else System.out.println("FAIL: example 1 -> " + Arrays.toString(r1));

        total++; int[] r2 = s.findRedundantConnection(
                      new int[][]{{1,2},{2,3},{3,4},{1,4},{1,5}});
                  if (Arrays.equals(r2, new int[]{1,4})) passed++;
                  else System.out.println("FAIL: example 2 -> " + Arrays.toString(r2));

        // Smallest possible: triangle.
        total++; int[] r3 = s.findRedundantConnection(new int[][]{{1,2},{2,3},{1,3}});
                  if (Arrays.equals(r3, new int[]{1,3})) passed++;
                  else System.out.println("FAIL: triangle -> " + Arrays.toString(r3));

        // Two-node cycle isn't possible (no multi-edges in input); smallest cycle is triangle.
        // Long chain plus one closing edge.
        total++; int[] r4 = s.findRedundantConnection(
                      new int[][]{{1,2},{2,3},{3,4},{4,5},{5,1}});
                  if (Arrays.equals(r4, new int[]{5,1})) passed++;
                  else System.out.println("FAIL: pentagon cycle -> " + Arrays.toString(r4));

        // Redundant edge in the middle (not the last edge), tie-broken by input order.
        total++; int[] r5 = s.findRedundantConnection(
                      new int[][]{{1,2},{2,3},{3,1},{4,5}});
                  if (Arrays.equals(r5, new int[]{3,1})) passed++;
                  else System.out.println("FAIL: mid redundant -> " + Arrays.toString(r5));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
