import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Order does not matter, so compare as sets of lists.
        total++; Set<List<Integer>> r1 = toSet(s.subsetsWithDup(new int[]{1, 2, 2}));
                  Set<List<Integer>> e1 = new HashSet<>(Arrays.asList(
                          list(), list(1), list(1, 2), list(1, 2, 2),
                          list(2), list(2, 2)));
                  if (r1.equals(e1)) passed++; else System.out.println("FAIL: [1,2,2] -> " + r1);

        total++; Set<List<Integer>> r2 = toSet(s.subsetsWithDup(new int[]{0}));
                  Set<List<Integer>> e2 = new HashSet<>(Arrays.asList(list(), list(0)));
                  if (r2.equals(e2)) passed++; else System.out.println("FAIL: [0] -> " + r2);

        // All-equal input: n+1 distinct subsets, not 2^n.
        total++; Set<List<Integer>> r3 = toSet(s.subsetsWithDup(new int[]{4, 4, 4, 4}));
                  Set<List<Integer>> e3 = new HashSet<>(Arrays.asList(
                          list(),
                          list(4), list(4, 4), list(4, 4, 4), list(4, 4, 4, 4)));
                  if (r3.equals(e3)) passed++; else System.out.println("FAIL: [4,4,4,4] -> " + r3);

        // Unsorted input with duplicates -- solution sorts internally.
        total++; Set<List<Integer>> r4 = toSet(s.subsetsWithDup(new int[]{2, 1, 2}));
                  if (r4.equals(e1)) passed++; else System.out.println("FAIL: unsorted [2,1,2] -> " + r4);

        // Empty input -> exactly one subset.
        total++; List<List<Integer>> r0 = s.subsetsWithDup(new int[]{});
                  if (r0.size() == 1 && r0.get(0).isEmpty()) passed++;
                  else System.out.println("FAIL: [] -> " + r0);

        // Duplicates of negatives + zero.
        total++; Set<List<Integer>> r5 = toSet(s.subsetsWithDup(new int[]{-1, 0, -1}));
                  Set<List<Integer>> e5 = new HashSet<>(Arrays.asList(
                          list(),
                          list(-1), list(-1, -1), list(0),
                          list(-1, 0), list(-1, -1, 0)));
                  if (r5.equals(e5)) passed++; else System.out.println("FAIL: [-1,0,-1] -> " + r5);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static List<Integer> list(Integer... xs) {
        return Arrays.asList(xs);
    }

    private static Set<List<Integer>> toSet(List<List<Integer>> xs) {
        return new HashSet<>(xs);
    }
}
