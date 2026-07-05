import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.orangesRotting(new int[][]{
                     {2,1,1},
                     {1,1,0},
                     {0,1,1}});
                 if (r1 == 4) passed++; else System.out.println("FAIL: example 1 -> " + r1);

        total++; int r2 = s.orangesRotting(new int[][]{
                     {2,1,1},
                     {0,1,1},
                     {1,0,1}});
                 if (r2 == -1) passed++; else System.out.println("FAIL: unreachable -> " + r2);

        total++; int r3 = s.orangesRotting(new int[][]{{0,2}});
                 if (r3 == 0) passed++; else System.out.println("FAIL: no fresh -> " + r3);

        total++; int r4 = s.orangesRotting(new int[][]{{0}});
                 if (r4 == 0) passed++; else System.out.println("FAIL: empty cell -> " + r4);

        // All fresh, no rotten -> impossible.
        total++; int r5 = s.orangesRotting(new int[][]{
                     {1,1},
                     {1,1}});
                 if (r5 == -1) passed++; else System.out.println("FAIL: no rotten -> " + r5);

        // Two rotten sources spread simultaneously -> 1 minute.
        total++; int r6 = s.orangesRotting(new int[][]{
                     {2,1,2}});
                 if (r6 == 1) passed++; else System.out.println("FAIL: two sources -> " + r6);

        // Single fresh adjacent to rotten -> 1 minute.
        total++; int r7 = s.orangesRotting(new int[][]{{2},{1}});
                 if (r7 == 1) passed++; else System.out.println("FAIL: single fresh -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
