import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Order does not matter, so compare as sets of lists.
        total++; Set<List<Integer>> r1 = toSet(s.combinationSum(new int[]{2, 3, 6, 7}, 7));
                  Set<List<Integer>> e1 = new HashSet<>(Arrays.asList(
                          list(2, 2, 3), list(7)));
                  if (r1.equals(e1)) passed++; else System.out.println("FAIL: [2,3,6,7],7 -> " + r1);

        total++; Set<List<Integer>> r2 = toSet(s.combinationSum(new int[]{2, 3, 5}, 8));
                  Set<List<Integer>> e2 = new HashSet<>(Arrays.asList(
                          list(2, 2, 2, 2), list(2, 3, 3), list(3, 5)));
                  if (r2.equals(e2)) passed++; else System.out.println("FAIL: [2,3,5],8 -> " + r2);

        // Unsorted candidates input -- solution sorts internally; output still unique.
        total++; Set<List<Integer>> r3 = toSet(s.combinationSum(new int[]{5, 3, 2}, 8));
                  if (r3.equals(e2)) passed++; else System.out.println("FAIL: unsorted [5,3,2],8 -> " + r3);

        // No combination possible -> empty list.
        total++; List<List<Integer>> r4 = s.combinationSum(new int[]{2}, 1);
                  if (r4.isEmpty()) passed++; else System.out.println("FAIL: [2],1 -> " + r4);

        // Target equals a candidate exactly -> single one-element answer.
        total++; Set<List<Integer>> r5 = toSet(s.combinationSum(new int[]{1}, 1));
                  Set<List<Integer>> e5 = new HashSet<>(Arrays.asList(list(1)));
                  if (r5.equals(e5)) passed++; else System.out.println("FAIL: [1],1 -> " + r5);

        // All-ones path -- heaviest reuse case; |[1,n]| -> n ways.
        total++; List<List<Integer>> r6 = s.combinationSum(new int[]{1}, 5);
                  Set<List<Integer>> e6 = new HashSet<>(Arrays.asList(list(1, 1, 1, 1, 1)));
                  if (toSet(r6).equals(e6)) passed++; else System.out.println("FAIL: [1],5 -> " + r6);

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
