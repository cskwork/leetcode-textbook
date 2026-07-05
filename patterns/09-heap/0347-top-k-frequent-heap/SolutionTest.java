import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Result order is unspecified, so compare each case as a set of Integers.
        total++; int[] r1 = s.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2);
                 if (asSet(r1).equals(setOf(1, 2))) passed++;
                 else System.out.println("FAIL: example1 -> " + Arrays.toString(r1));

        total++; int[] r2 = s.topKFrequent(new int[]{1}, 1);
                 if (asSet(r2).equals(setOf(1))) passed++;
                 else System.out.println("FAIL: single -> " + Arrays.toString(r2));

        // k equals the number of distinct values -> everything returned
        total++; int[] r3 = s.topKFrequent(new int[]{1, 2, 3}, 3);
                 if (asSet(r3).equals(setOf(1, 2, 3))) passed++;
                 else System.out.println("FAIL: k=distinct -> " + Arrays.toString(r3));

        // All frequencies equal (1 each) -> any k of them are valid
        total++; int[] r4 = s.topKFrequent(new int[]{1, 2}, 2);
                 if (asSet(r4).equals(setOf(1, 2))) passed++;
                 else System.out.println("FAIL: tied freqs -> " + Arrays.toString(r4));

        // Negatives and uneven counts: freq {2:3, -1:2, 3:1}, k=2 -> {2, -1}
        total++; int[] r5 = s.topKFrequent(new int[]{-1, -1, 2, 2, 2, 3}, 2);
                 if (asSet(r5).equals(setOf(2, -1))) passed++;
                 else System.out.println("FAIL: negatives -> " + Arrays.toString(r5));

        // Larger case: freq {1:4, 2:3, 3:2, 4:1}, k=2 -> {1, 2}
        total++; int[] r6 = s.topKFrequent(new int[]{1, 1, 1, 1, 2, 2, 2, 3, 3, 4}, 2);
                 if (asSet(r6).equals(setOf(1, 2))) passed++;
                 else System.out.println("FAIL: larger -> " + Arrays.toString(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static Set<Integer> asSet(int[] a) {
        Set<Integer> set = new HashSet<>();
        for (int x : a) set.add(x);
        return set;
    }

    private static Set<Integer> setOf(int... xs) {
        Set<Integer> set = new HashSet<>();
        for (int x : xs) set.add(x);
        return set;
    }
}
