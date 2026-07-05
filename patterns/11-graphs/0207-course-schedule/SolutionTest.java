public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.canFinish(2, new int[][]{{1,0}});
                  if (r1) passed++; else System.out.println("FAIL: example 1 -> " + r1);

        total++; boolean r2 = s.canFinish(2, new int[][]{{1,0},{0,1}});
                  if (!r2) passed++; else System.out.println("FAIL: example 2 (cycle) -> " + r2);

        // No prerequisites at all.
        total++; boolean r3 = s.canFinish(5, new int[][]{});
                  if (r3) passed++; else System.out.println("FAIL: no edges -> " + r3);

        // Single course, no prerequisites.
        total++; boolean r4 = s.canFinish(1, new int[][]{});
                  if (r4) passed++; else System.out.println("FAIL: single course -> " + r4);

        // Chain 0->1->2->3, no cycle.
        total++; boolean r5 = s.canFinish(4, new int[][]{{1,0},{2,1},{3,2}});
                  if (r5) passed++; else System.out.println("FAIL: chain -> " + r5);

        // Cycle 1->2->3->1 (self-edge in the cycle), plus an unrelated course 0.
        total++; boolean r6 = s.canFinish(4, new int[][]{{1,0},{2,1},{3,2},{1,3}});
                  if (!r6) passed++; else System.out.println("FAIL: 3-cycle -> " + r6);

        // Self-loop: a course requires itself.
        total++; boolean r7 = s.canFinish(2, new int[][]{{0,0}});
                  if (!r7) passed++; else System.out.println("FAIL: self-loop -> " + r7);

        // Larger DAG: classic "take 101 then 102/103 then 201".
        total++; boolean r8 = s.canFinish(6,
                  new int[][]{{1,0},{2,0},{3,1},{3,2},{4,3},{5,4}});
                  if (r8) passed++; else System.out.println("FAIL: larger DAG -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
