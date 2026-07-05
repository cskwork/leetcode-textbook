import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        Set<List<Integer>> got1 = toSet(s.threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
        Set<List<Integer>> exp1 = new HashSet<>();
        exp1.add(Arrays.asList(-1, -1, 2));
        exp1.add(Arrays.asList(-1, 0, 1));
        if (got1.equals(exp1)) passed++;
        else System.out.println("FAIL: LC example 1 -> " + got1);

        total++;
        Set<List<Integer>> got2 = toSet(s.threeSum(new int[]{0, 1, 1}));
        if (got2.isEmpty()) passed++;
        else System.out.println("FAIL: LC example 2 (no triplet) -> " + got2);

        total++;
        Set<List<Integer>> got3 = toSet(s.threeSum(new int[]{0, 0, 0}));
        Set<List<Integer>> exp3 = new HashSet<>();
        exp3.add(Arrays.asList(0, 0, 0));
        if (got3.equals(exp3)) passed++;
        else System.out.println("FAIL: LC example 3 (all zeroes) -> " + got3);

        total++;
        Set<List<Integer>> got4 = toSet(s.threeSum(new int[]{-2, 0, 0, 2, 2}));
        Set<List<Integer>> exp4 = new HashSet<>();
        exp4.add(Arrays.asList(-2, 0, 2));
        if (got4.size() == 1 && got4.equals(exp4)) passed++;
        else System.out.println("FAIL: dedup check (must be exactly one [-2,0,2]) -> " + got4);

        total++;
        Set<List<Integer>> got5 = toSet(s.threeSum(new int[]{}));
        if (got5.isEmpty()) passed++;
        else System.out.println("FAIL: empty input -> " + got5);

        total++;
        Set<List<Integer>> got6 = toSet(s.threeSum(new int[]{-4, -2, -2, -2, 0, 1, 2, 2, 2, 3, 3, 4, 4, 6, 6}));
        Set<List<Integer>> exp6 = new HashSet<>();
        exp6.add(Arrays.asList(-4, -2, 6));
        exp6.add(Arrays.asList(-4, 0, 4));
        exp6.add(Arrays.asList(-4, 1, 3));
        exp6.add(Arrays.asList(-4, 2, 2));
        exp6.add(Arrays.asList(-2, -2, 4));
        exp6.add(Arrays.asList(-2, 0, 2));
        if (got6.equals(exp6)) passed++;
        else System.out.println("FAIL: larger dedup case -> " + got6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static Set<List<Integer>> toSet(List<List<Integer>> lists) {
        return new HashSet<>(lists);
    }
}
