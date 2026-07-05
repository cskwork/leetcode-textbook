import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Order does not matter, so compare as sets of lists.
        total++; Set<List<Integer>> r1 = toSet(s.subsets(new int[]{0}));
                  Set<List<Integer>> e1 = new HashSet<>(Arrays.asList(
                          list(), list(0)));
                  if (r1.equals(e1)) passed++; else System.out.println("FAIL: [0] -> " + r1);

        total++; Set<List<Integer>> r3 = toSet(s.subsets(new int[]{1, 2, 3}));
                  Set<List<Integer>> e3 = new HashSet<>(Arrays.asList(
                          list(),
                          list(1), list(2), list(3),
                          list(1, 2), list(1, 3), list(2, 3),
                          list(1, 2, 3)));
                  if (r3.equals(e3)) passed++; else System.out.println("FAIL: [1,2,3] -> " + r3);

        // Empty input -> exactly one subset, the empty set.
        total++; List<List<Integer>> r0 = s.subsets(new int[]{});
                  if (r0.size() == 1 && r0.get(0).isEmpty()) passed++;
                  else System.out.println("FAIL: [] -> " + r0);

        // Size check: 2^n subsets.
        total++; int c4 = s.subsets(new int[]{1, 2, 3, 4}).size();
                  if (c4 == 16) passed++; else System.out.println("FAIL: |[1..4]| = " + c4 + " (want 16)");

        total++; int c6 = s.subsets(new int[]{5, 6, 7, 8, 9, 10}).size();
                  if (c6 == 64) passed++; else System.out.println("FAIL: |6 elems| = " + c6 + " (want 64)");

        // Negative numbers must be handled (no overflow / sign issues).
        total++; Set<List<Integer>> rn = toSet(s.subsets(new int[]{-1, 1}));
                  Set<List<Integer>> en = new HashSet<>(Arrays.asList(
                          list(), list(-1), list(1), list(-1, 1)));
                  if (rn.equals(en)) passed++; else System.out.println("FAIL: [-1,1] -> " + rn);

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
