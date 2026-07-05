public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.search(new int[]{-1, 0, 3, 5, 9, 12}, 9);
                 if (r1 == 4) passed++; else System.out.println("FAIL: target 9 -> " + r1);

        total++; int r2 = s.search(new int[]{-1, 0, 3, 5, 9, 12}, 2);
                 if (r2 == -1) passed++; else System.out.println("FAIL: target 2 -> " + r2);

        total++; int r3 = s.search(new int[]{5}, 5);
                 if (r3 == 0) passed++; else System.out.println("FAIL: single hit -> " + r3);

        total++; int r4 = s.search(new int[]{5}, 2);
                 if (r4 == -1) passed++; else System.out.println("FAIL: single miss -> " + r4);

        total++; int r5 = s.search(new int[]{1, 2, 3, 4, 5}, 1);
                 if (r5 == 0) passed++; else System.out.println("FAIL: target at left edge -> " + r5);

        total++; int r6 = s.search(new int[]{1, 2, 3, 4, 5}, 5);
                 if (r6 == 4) passed++; else System.out.println("FAIL: target at right edge -> " + r6);

        total++; int r7 = s.search(new int[]{}, 3);
                 if (r7 == -1) passed++; else System.out.println("FAIL: empty array -> " + r7);

        total++; int r8 = s.search(new int[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19}, 15);
                 if (r8 == 7) passed++; else System.out.println("FAIL: larger even-length array -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
