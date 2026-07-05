public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 0);
                 if (r1 == 4) passed++; else System.out.println("FAIL: ex1 target 0 -> " + r1);

        total++; int r2 = s.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 3);
                 if (r2 == -1) passed++; else System.out.println("FAIL: ex2 target 3 (absent) -> " + r2);

        total++; int r3 = s.search(new int[]{1}, 0);
                 if (r3 == -1) passed++; else System.out.println("FAIL: single absent -> " + r3);

        total++; int r4 = s.search(new int[]{1, 3}, 3);
                 if (r4 == 1) passed++; else System.out.println("FAIL: two-element hit -> " + r4);

        total++; int r5 = s.search(new int[]{3, 1}, 1);
                 if (r5 == 1) passed++; else System.out.println("FAIL: rotated two hit -> " + r5);

        total++; int r6 = s.search(new int[]{1, 2, 3, 4, 5}, 3);
                 if (r6 == 2) passed++; else System.out.println("FAIL: unrotated -> " + r6);

        total++; int r7 = s.search(new int[]{1, 2, 3, 4, 5}, 1);
                 if (r7 == 0) passed++; else System.out.println("FAIL: first element -> " + r7);

        total++; int r8 = s.search(new int[]{1, 2, 3, 4, 5}, 5);
                 if (r8 == 4) passed++; else System.out.println("FAIL: last element -> " + r8);

        total++; int r9 = s.search(new int[]{5, 1, 2, 3, 4}, 5);
                 if (r9 == 0) passed++; else System.out.println("FAIL: target at rotated start -> " + r9);

        total++; int r10 = s.search(new int[]{}, 0);
                 if (r10 == -1) passed++; else System.out.println("FAIL: empty array -> " + r10);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
