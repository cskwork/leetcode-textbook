public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7});
        if (r1 == 49) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.maxArea(new int[]{1, 1});
        if (r2 == 1) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        total++;
        int r3 = s.maxArea(new int[]{1, 2});
        if (r3 == 1) passed++;
        else System.out.println("FAIL: two-element, ascending -> " + r3);

        total++;
        int r4 = s.maxArea(new int[]{4, 3, 2, 1, 4});
        if (r4 == 16) passed++;
        else System.out.println("FAIL: tall at both ends -> " + r4);

        total++;
        int r5 = s.maxArea(new int[]{1, 2, 1});
        if (r5 == 2) passed++;
        else System.out.println("FAIL: three-element -> " + r5);

        total++;
        int r6 = s.maxArea(new int[]{2, 3, 10, 5, 7, 8, 9});
        if (r6 == 36) passed++;
        else System.out.println("FAIL: mixed heights -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
