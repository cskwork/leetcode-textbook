public class SolutionTest {
    public static void main(String[] args) {
        int passed = 0, total = 0;

        // Example 1 from LeetCode: k=3, nums=[4,5,8,2], adds [3,5,10,9,4] -> [4,5,5,8,8]
        total++;
        KthLargest kl = new KthLargest(3, new int[]{4, 5, 8, 2});
        int[] adds = {3, 5, 10, 9, 4};
        int[] expected = {4, 5, 5, 8, 8};
        boolean ok = true;
        int[] got = new int[adds.length];
        for (int i = 0; i < adds.length; i++) {
            got[i] = kl.add(adds[i]);
            if (got[i] != expected[i]) ok = false;
        }
        if (ok) passed++;
        else System.out.println("FAIL: example1 -> " + java.util.Arrays.toString(got));

        // k=1 with an empty initial stream: root is always the single largest seen so far
        total++;
        KthLargest kl2 = new KthLargest(1, new int[]{});
        int[] adds2 = {1, 2, 3, 0};
        int[] exp2 = {1, 2, 3, 3};
        boolean ok2 = true;
        int[] got2 = new int[adds2.length];
        for (int i = 0; i < adds2.length; i++) {
            got2[i] = kl2.add(adds2[i]);
            if (got2[i] != exp2[i]) ok2 = false;
        }
        if (ok2) passed++;
        else System.out.println("FAIL: k=1 empty init -> " + java.util.Arrays.toString(got2));

        // Constructor starts under capacity; first add fills to k
        total++;
        KthLargest kl3 = new KthLargest(2, new int[]{0});
        int r3 = kl3.add(-1);           // stream [-1,0], 2nd largest = -1
        if (r3 == -1) passed++;
        else System.out.println("FAIL: k=2 fill-up -> " + r3);

        // Ties: equal values each count, k-th position must stay at that value
        total++;
        KthLargest kl4 = new KthLargest(3, new int[]{5, 5, 5, 5});
        int r4 = kl4.add(5);            // stream is all 5s, 3rd largest = 5
        if (r4 == 5) passed++;
        else System.out.println("FAIL: all-fives -> " + r4);

        // A value smaller than every current survivor does not move the k-th largest
        total++;
        KthLargest kl5 = new KthLargest(3, new int[]{10, 20, 30});
        int r5 = kl5.add(1);            // survivors still 10,20,30 -> 3rd largest 10
        if (r5 == 10) passed++;
        else System.out.println("FAIL: small-add unchanged -> " + r5);

        // A value that breaks into the top k bumps the k-th largest up
        total++;
        KthLargest kl6 = new KthLargest(3, new int[]{10, 20, 30});
        int r6 = kl6.add(25);           // top3 = 30,25,20 -> 3rd largest 20
        if (r6 == 20) passed++;
        else System.out.println("FAIL: bumping add -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
