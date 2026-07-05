public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.minEatingSpeed(new int[]{3, 6, 7, 11}, 8);
                 if (r1 == 4) passed++; else System.out.println("FAIL: ex1 -> " + r1);

        total++; int r2 = s.minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 5);
                 if (r2 == 30) passed++; else System.out.println("FAIL: ex2 (h==n) -> " + r2);

        total++; int r3 = s.minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 6);
                 if (r3 == 23) passed++; else System.out.println("FAIL: ex3 -> " + r3);

        total++; int r4 = s.minEatingSpeed(new int[]{312884470}, 312884469);
                 if (r4 == 2) passed++; else System.out.println("FAIL: single pile, huge h -> " + r4);

        total++; int r5 = s.minEatingSpeed(new int[]{1000000000}, 2);
                 if (r5 == 500000000) passed++; else System.out.println("FAIL: single pile split -> " + r5);

        total++; int r6 = s.minEatingSpeed(new int[]{1, 1, 1, 1}, 4);
                 if (r6 == 1) passed++; else System.out.println("FAIL: speed 1 suffices -> " + r6);

        total++; int r7 = s.minEatingSpeed(new int[]{1000000000, 1000000000}, 2);
                 if (r7 == 1000000000) passed++; else System.out.println("FAIL: huge piles, h==n -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
