public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.jump(new int[]{2, 3, 1, 1, 4});
        if (r1 == 2) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.jump(new int[]{2, 3, 0, 1, 4});
        if (r2 == 2) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: single element -> zero jumps.
        total++;
        int r3 = s.jump(new int[]{0});
        if (r3 == 0) passed++;
        else System.out.println("FAIL: single element -> " + r3);

        // Edge: two elements -> always one jump.
        total++;
        int r4 = s.jump(new int[]{2, 1});
        if (r4 == 1) passed++;
        else System.out.println("FAIL: two elements -> " + r4);

        // Linear chain of 1s.
        total++;
        int r5 = s.jump(new int[]{1, 1, 1, 1});
        if (r5 == 3) passed++;
        else System.out.println("FAIL: linear chain -> " + r5);

        // One big jump covers everything.
        total++;
        int r6 = s.jump(new int[]{5, 1, 1, 1, 1, 1});
        if (r6 == 1) passed++;
        else System.out.println("FAIL: one big jump -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
