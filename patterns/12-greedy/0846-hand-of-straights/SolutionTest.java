public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        boolean r1 = s.isNStraightHand(new int[]{1, 2, 3, 6, 2, 3, 4, 7, 8}, 3);
        if (r1) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        total++;
        boolean r2 = s.isNStraightHand(new int[]{1, 2, 3, 4, 5}, 4);
        if (!r2) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: groupSize == 1 -> always true (every card is its own group).
        total++;
        boolean r3 = s.isNStraightHand(new int[]{1, 2, 3, 4, 5}, 1);
        if (r3) passed++;
        else System.out.println("FAIL: groupSize 1 -> " + r3);

        // Edge: one group covering everything.
        total++;
        boolean r4 = s.isNStraightHand(new int[]{1, 2, 3, 4, 5}, 5);
        if (r4) passed++;
        else System.out.println("FAIL: single group of 5 -> " + r4);

        // Edge: duplicate cards cause a gap.
        total++;
        boolean r5 = s.isNStraightHand(new int[]{1, 1, 2, 2, 3, 3}, 3);
        if (r5) passed++;
        else System.out.println("FAIL: two parallel straights -> " + r5);

        // Edge: missing middle card.
        total++;
        boolean r6 = s.isNStraightHand(new int[]{1, 2, 4, 5, 6, 7}, 3);
        if (!r6) passed++;
        else System.out.println("FAIL: missing middle card -> " + r6);

        // Edge: single card, groupSize 1.
        total++;
        boolean r7 = s.isNStraightHand(new int[]{7}, 1);
        if (r7) passed++;
        else System.out.println("FAIL: single card group 1 -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
