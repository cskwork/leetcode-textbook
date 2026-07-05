public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        boolean r1 = s.canJump(new int[]{2, 3, 1, 1, 4});
        if (r1) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        boolean r2 = s.canJump(new int[]{3, 2, 1, 0, 4});
        if (!r2) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: single element -> already at the last index.
        total++;
        boolean r3 = s.canJump(new int[]{0});
        if (r3) passed++;
        else System.out.println("FAIL: single zero -> " + r3);

        // Edge: single positive element.
        total++;
        boolean r4 = s.canJump(new int[]{5});
        if (r4) passed++;
        else System.out.println("FAIL: single positive -> " + r4);

        // Edge: first element is zero but n > 1 -> cannot move.
        total++;
        boolean r5 = s.canJump(new int[]{0, 1});
        if (!r5) passed++;
        else System.out.println("FAIL: starts stuck -> " + r5);

        // Larger case: reachable via a multi-hop path 0 -> 1 -> 3 -> 4.
        total++;
        boolean r6 = s.canJump(new int[]{2, 2, 0, 1, 1});
        if (r6) passed++;
        else System.out.println("FAIL: multi-hop reachable -> " + r6);

        // Larger case: dead-end zero midway traps the frontier.
        total++;
        boolean r7 = s.canJump(new int[]{2, 0, 1, 0, 1});
        if (!r7) passed++;
        else System.out.println("FAIL: dead-end zero -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
