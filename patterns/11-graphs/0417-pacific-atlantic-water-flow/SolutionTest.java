import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; List<List<Integer>> r1 = s.pacificAtlantic(new int[][]{
                     {1,2,2,3,5},
                     {3,2,3,4,4},
                     {2,4,5,3,1},
                     {6,7,1,4,5},
                     {5,1,1,2,4}});
                 Set<String> e1 = setOf("0,4","1,3","1,4","2,2","3,0","3,1","4,0");
                 if (matches(r1, e1)) passed++;
                 else System.out.println("FAIL: example 1 -> " + r1);

        // Single cell flows to both oceans.
        total++; List<List<Integer>> r2 = s.pacificAtlantic(new int[][]{{5}});
                 if (matches(r2, setOf("0,0"))) passed++;
                 else System.out.println("FAIL: single cell -> " + r2);

        // 1x2 row: both cells touch both oceans.
        total++; List<List<Integer>> r3 = s.pacificAtlantic(new int[][]{{3,4}});
                 if (matches(r3, setOf("0,0","0,1"))) passed++;
                 else System.out.println("FAIL: 1x2 row -> " + r3);

        // 3x3 hand-traced grid: output should exclude (0,0) and (0,1).
        total++; List<List<Integer>> r4 = s.pacificAtlantic(new int[][]{
                     {1,2,3},
                     {8,9,4},
                     {7,6,5}});
                 Set<String> e4 = setOf("0,2","1,0","1,1","1,2","2,0","2,1","2,2");
                 if (matches(r4, e4)) passed++;
                 else System.out.println("FAIL: 3x3 grid -> " + r4);

        // Uniform heights: every cell reaches both oceans.
        total++; List<List<Integer>> r5 = s.pacificAtlantic(new int[][]{
                     {2,2,2},
                     {2,2,2},
                     {2,2,2}});
                 Set<String> e5 = setOf("0,0","0,1","0,2","1,0","1,1","1,2","2,0","2,1","2,2");
                 if (matches(r5, e5)) passed++;
                 else System.out.println("FAIL: uniform heights -> " + r5);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static Set<String> setOf(String... keys) {
        Set<String> set = new HashSet<>();
        for (String k : keys) set.add(k);
        return set;
    }

    private static boolean matches(List<List<Integer>> got, Set<String> expected) {
        if (got.size() != expected.size()) return false;
        Set<String> gotSet = new HashSet<>();
        for (List<Integer> p : got) {
            gotSet.add(p.get(0) + "," + p.get(1));
        }
        return gotSet.equals(expected);
    }
}
