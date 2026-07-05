public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.canCompleteCircuit(new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2});
        if (r1 == 3) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.canCompleteCircuit(new int[]{2, 3, 4}, new int[]{3, 4, 3});
        if (r2 == -1) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: single station that is self-sufficient.
        total++;
        int r3 = s.canCompleteCircuit(new int[]{5}, new int[]{4});
        if (r3 == 0) passed++;
        else System.out.println("FAIL: single station OK -> " + r3);

        // Edge: single station that is not self-sufficient.
        total++;
        int r4 = s.canCompleteCircuit(new int[]{2}, new int[]{2});
        if (r4 == 0) passed++;
        else System.out.println("FAIL: single station break-even -> " + r4);

        // Edge: total surplus exactly zero, circuit possible only from one start.
        total++;
        int r5 = s.canCompleteCircuit(new int[]{3, 1, 1}, new int[]{2, 2, 1});
        if (r5 == 0) passed++;
        else System.out.println("FAIL: break-even circuit -> " + r5);

        // Edge: impossible because total surplus < 0 even though a long prefix is positive.
        total++;
        int r6 = s.canCompleteCircuit(new int[]{5, 1, 2, 3}, new int[]{4, 4, 4, 4});
        if (r6 == -1) passed++;
        else System.out.println("FAIL: negative total surplus -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
