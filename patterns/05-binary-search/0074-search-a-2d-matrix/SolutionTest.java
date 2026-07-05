public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        int[][] m1 = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}};

        total++; boolean r1 = s.searchMatrix(m1, 3);
                 if (r1) passed++; else System.out.println("FAIL: target 3 (present) -> " + r1);

        total++; boolean r2 = s.searchMatrix(m1, 13);
                 if (!r2) passed++; else System.out.println("FAIL: target 13 (absent) -> " + r2);

        total++; boolean r3 = s.searchMatrix(m1, 1);
                 if (r3) passed++; else System.out.println("FAIL: target 1 (first cell) -> " + r3);

        total++; boolean r4 = s.searchMatrix(m1, 60);
                 if (r4) passed++; else System.out.println("FAIL: target 60 (last cell) -> " + r4);

        total++; boolean r5 = s.searchMatrix(m1, 0);
                 if (!r5) passed++; else System.out.println("FAIL: target 0 (before all) -> " + r5);

        total++; boolean r6 = s.searchMatrix(m1, 100);
                 if (!r6) passed++; else System.out.println("FAIL: target 100 (after all) -> " + r6);

        int[][] m2 = {{5}};
        total++; boolean r7 = s.searchMatrix(m2, 5);
                 if (r7) passed++; else System.out.println("FAIL: single cell hit -> " + r7);

        total++; boolean r8 = s.searchMatrix(m2, 9);
                 if (!r8) passed++; else System.out.println("FAIL: single cell miss -> " + r8);

        int[][] m3 = {{1}, {3}, {5}, {7}};
        total++; boolean r9 = s.searchMatrix(m3, 5);
                 if (r9) passed++; else System.out.println("FAIL: single column -> " + r9);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
