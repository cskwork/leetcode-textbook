public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.minPathSum(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}});
                 if (r1 == 7) passed++; else System.out.println("FAIL: 3x3 example -> " + r1);

        total++; int r2 = s.minPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}});
                 if (r2 == 12) passed++; else System.out.println("FAIL: 2x3 example -> " + r2);

        total++; int r3 = s.minPathSum(new int[][]{{5}});
                 if (r3 == 5) passed++; else System.out.println("FAIL: 1x1 -> " + r3);

        total++; int r4 = s.minPathSum(new int[][]{{1, 2}, {1, 1}});
                 if (r4 == 3) passed++; else System.out.println("FAIL: 2x2 -> " + r4);

        total++; int r5 = s.minPathSum(new int[][]{{1, 2, 3, 4, 5}});
                 if (r5 == 15) passed++; else System.out.println("FAIL: single row -> " + r5);

        total++; int r6 = s.minPathSum(new int[][]{{1}, {2}, {3}});
                 if (r6 == 6) passed++; else System.out.println("FAIL: single col -> " + r6);

        total++; int r7 = s.minPathSum(new int[][]{{0, 0, 0}, {0, 0, 0}});
                 if (r7 == 0) passed++; else System.out.println("FAIL: zeros -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
