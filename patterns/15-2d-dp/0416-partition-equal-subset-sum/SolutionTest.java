public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.canPartition(new int[]{1, 5, 11, 5});
                 if (r1) passed++; else System.out.println("FAIL: [1,5,11,5] -> " + r1);

        total++; boolean r2 = s.canPartition(new int[]{1, 2, 3, 5});
                 if (!r2) passed++; else System.out.println("FAIL: [1,2,3,5] -> " + r2);

        total++; boolean r3 = s.canPartition(new int[]{1, 2, 5});
                 if (!r3) passed++; else System.out.println("FAIL: [1,2,5] -> " + r3);

        total++; boolean r4 = s.canPartition(new int[]{2, 2});
                 if (r4) passed++; else System.out.println("FAIL: [2,2] -> " + r4);

        total++; boolean r5 = s.canPartition(new int[]{1});
                 if (!r5) passed++; else System.out.println("FAIL: [1] -> " + r5);

        total++; boolean r6 = s.canPartition(new int[]{3, 3, 3, 3});
                 if (r6) passed++; else System.out.println("FAIL: [3,3,3,3] -> " + r6);

        total++; boolean r7 = s.canPartition(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 100});
                 if (!r7) passed++; else System.out.println("FAIL: odd total -> " + r7);

        total++; boolean r8 = s.canPartition(new int[]{100});
                 if (!r8) passed++; else System.out.println("FAIL: [100] -> " + r8);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
