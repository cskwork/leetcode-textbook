public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int r1 = s.coinChange(new int[]{1, 2, 5}, 11);
        if (r1 == 3) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        int r2 = s.coinChange(new int[]{2}, 3);
        if (r2 == -1) passed++;
        else System.out.println("FAIL: impossible -> " + r2);

        total++;
        int r3 = s.coinChange(new int[]{1}, 0);
        if (r3 == 0) passed++;
        else System.out.println("FAIL: amount 0 -> " + r3);

        // Greedy would fail here: 4+1+1 = 3 coins, but 3+3 = 2 coins.
        total++;
        int r4 = s.coinChange(new int[]{1, 3, 4}, 6);
        if (r4 == 2) passed++;
        else System.out.println("FAIL: non-canonical (anti-greedy) -> " + r4);

        // Single coin exactly equal to amount.
        total++;
        int r5 = s.coinChange(new int[]{7}, 7);
        if (r5 == 1) passed++;
        else System.out.println("FAIL: exact single coin -> " + r5);

        // Larger amount with the classic US denominations.
        total++;
        int r6 = s.coinChange(new int[]{1, 5, 10, 25}, 100);
        if (r6 == 4) passed++;   // 25*4 = 100
        else System.out.println("FAIL: 100 cents -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
