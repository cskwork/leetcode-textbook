public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.findMin(new int[]{3, 4, 5, 1, 2});
                 if (r1 == 1) passed++; else System.out.println("FAIL: ex1 -> " + r1);

        total++; int r2 = s.findMin(new int[]{4, 5, 6, 7, 0, 1, 2});
                 if (r2 == 0) passed++; else System.out.println("FAIL: ex2 -> " + r2);

        total++; int r3 = s.findMin(new int[]{11, 13, 15, 17});
                 if (r3 == 11) passed++; else System.out.println("FAIL: not rotated -> " + r3);

        total++; int r4 = s.findMin(new int[]{2, 1});
                 if (r4 == 1) passed++; else System.out.println("FAIL: two elements -> " + r4);

        total++; int r5 = s.findMin(new int[]{1, 2});
                 if (r5 == 1) passed++; else System.out.println("FAIL: two elements, unrotated -> " + r5);

        total++; int r6 = s.findMin(new int[]{42});
                 if (r6 == 42) passed++; else System.out.println("FAIL: single element -> " + r6);

        total++; int r7 = s.findMin(new int[]{2, 3, 4, 5, 1});
                 if (r7 == 1) passed++; else System.out.println("FAIL: min at last index -> " + r7);

        total++; int r8 = s.findMin(new int[]{5, 1, 2, 3, 4});
                 if (r8 == 1) passed++; else System.out.println("FAIL: min at index 1 -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
