import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Order does not matter, so compare as sets of lists.
        total++; Set<List<Integer>> r1 = toSet(s.permute(new int[]{1}));
                  Set<List<Integer>> e1 = new HashSet<>(Arrays.asList(list(1)));
                  if (r1.equals(e1)) passed++; else System.out.println("FAIL: [1] -> " + r1);

        total++; Set<List<Integer>> r2 = toSet(s.permute(new int[]{0, 1}));
                  Set<List<Integer>> e2 = new HashSet<>(Arrays.asList(list(0, 1), list(1, 0)));
                  if (r2.equals(e2)) passed++; else System.out.println("FAIL: [0,1] -> " + r2);

        total++; Set<List<Integer>> r3 = toSet(s.permute(new int[]{1, 2, 3}));
                  Set<List<Integer>> e3 = new HashSet<>(Arrays.asList(
                          list(1, 2, 3), list(1, 3, 2),
                          list(2, 1, 3), list(2, 3, 1),
                          list(3, 1, 2), list(3, 2, 1)));
                  if (r3.equals(e3)) passed++; else System.out.println("FAIL: [1,2,3] -> " + r3);

        // Counts must equal n!.
        total++; int c4 = s.permute(new int[]{1, 2, 3, 4}).size();
                  if (c4 == 24) passed++; else System.out.println("FAIL: |[1,2,3,4]| = " + c4 + " (want 24)");

        total++; int c5 = s.permute(new int[]{5, 4, 3, 2, 1}).size();
                  if (c5 == 120) passed++; else System.out.println("FAIL: |5 elems| = " + c5 + " (want 120)");

        // Every returned permutation must be the same length as the input and
        // contain exactly the same multiset of values.
        total++; boolean allValid = true;
                  int[] input = {7, 8, 9};
                  List<Integer> sortedInput = sortedBoxed(input);
                  for (List<Integer> p : s.permute(input)) {
                      if (p.size() != input.length || !sorted(p).equals(sortedInput)) {
                          allValid = false; break;
                      }
                  }
                  if (allValid) passed++; else System.out.println("FAIL: a permutation of [7,8,9] was malformed");

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static List<Integer> list(Integer... xs) {
        return Arrays.asList(xs);
    }

    private static Set<List<Integer>> toSet(List<List<Integer>> xs) {
        return new HashSet<>(xs);
    }

    private static List<Integer> sortedBoxed(int[] xs) {
        List<Integer> out = new ArrayList<>();
        for (int x : xs) out.add(x);
        Collections.sort(out);
        return out;
    }

    private static List<Integer> sorted(List<Integer> xs) {
        List<Integer> out = new ArrayList<>(xs);
        Collections.sort(out);
        return out;
    }
}
