public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.searchInsert(new int[]{1, 3, 5, 6}, 5);
                 if (r1 == 2) passed++; else System.out.println("FAIL: exact hit -> " + r1);

        total++; int r2 = s.searchInsert(new int[]{1, 3, 5, 6}, 2);
                 if (r2 == 1) passed++; else System.out.println("FAIL: insert in middle -> " + r2);

        total++; int r3 = s.searchInsert(new int[]{1, 3, 5, 6}, 7);
                 if (r3 == 4) passed++; else System.out.println("FAIL: insert past end -> " + r3);

        total++; int r4 = s.searchInsert(new int[]{1, 3, 5, 6}, 0);
                 if (r4 == 0) passed++; else System.out.println("FAIL: insert before start -> " + r4);

        total++; int r5 = s.searchInsert(new int[]{1}, 0);
                 if (r5 == 0) passed++; else System.out.println("FAIL: single, before -> " + r5);

        total++; int r6 = s.searchInsert(new int[]{1}, 2);
                 if (r6 == 1) passed++; else System.out.println("FAIL: single, after -> " + r6);

        total++; int r7 = s.searchInsert(new int[]{}, 3);
                 if (r7 == 0) passed++; else System.out.println("FAIL: empty array -> " + r7);

        total++; int r8 = s.searchInsert(new int[]{2, 4, 6, 8, 10}, 6);
                 if (r8 == 2) passed++; else System.out.println("FAIL: hit mid even length -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
