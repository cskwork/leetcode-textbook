public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2);
                 if (r1 == 5) passed++; else System.out.println("FAIL: example1 -> " + r1);

        total++; int r2 = s.findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4);
                 if (r2 == 4) passed++; else System.out.println("FAIL: example2 -> " + r2);

        total++; int r3 = s.findKthLargest(new int[]{1, 2, 3}, 1);   // max element
                 if (r3 == 3) passed++; else System.out.println("FAIL: k=1 max -> " + r3);

        total++; int r4 = s.findKthLargest(new int[]{7, 2, 9}, 3);   // k=n -> min element
                 if (r4 == 2) passed++; else System.out.println("FAIL: k=n min -> " + r4);

        total++; int r5 = s.findKthLargest(new int[]{1, 1, 1, 2, 2, 3}, 3); // ties: 3,2,2 -> 2
                 if (r5 == 2) passed++; else System.out.println("FAIL: ties -> " + r5);

        total++; int r6 = s.findKthLargest(new int[]{-1, -2, -3}, 1); // negatives
                 if (r6 == -1) passed++; else System.out.println("FAIL: negatives -> " + r6);

        total++; int r7 = s.findKthLargest(new int[]{42}, 1);         // single element
                 if (r7 == 42) passed++; else System.out.println("FAIL: single -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
