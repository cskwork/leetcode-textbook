public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.uniquePaths(3, 7);
                 if (r1 == 28) passed++; else System.out.println("FAIL: (3,7) -> " + r1);

        total++; int r2 = s.uniquePaths(3, 2);
                 if (r2 == 3) passed++; else System.out.println("FAIL: (3,2) -> " + r2);

        total++; int r3 = s.uniquePaths(1, 1);
                 if (r3 == 1) passed++; else System.out.println("FAIL: (1,1) -> " + r3);

        total++; int r4 = s.uniquePaths(1, 10);
                 if (r4 == 1) passed++; else System.out.println("FAIL: (1,10) single row -> " + r4);

        total++; int r5 = s.uniquePaths(10, 1);
                 if (r5 == 1) passed++; else System.out.println("FAIL: (10,1) single col -> " + r5);

        total++; int r6 = s.uniquePaths(2, 2);
                 if (r6 == 2) passed++; else System.out.println("FAIL: (2,2) -> " + r6);

        total++; int r7 = s.uniquePaths(3, 3);
                 if (r7 == 6) passed++; else System.out.println("FAIL: (3,3) -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
