public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r = s.minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3});
                 if (r == 2) passed++; else System.out.println("FAIL: example 1 target=7 [2,3,1,2,4,3] -> " + r);

        total++; r = s.minSubArrayLen(4, new int[]{1, 4, 4});
                 if (r == 1) passed++; else System.out.println("FAIL: example 2 target=4 [1,4,4] -> " + r);

        total++; r = s.minSubArrayLen(11, new int[]{1, 1, 1, 1, 1, 1, 1, 1});
                 if (r == 0) passed++; else System.out.println("FAIL: example 3 target=11 [1x8] -> " + r);

        total++; r = s.minSubArrayLen(1, new int[]{});
                 if (r == 0) passed++; else System.out.println("FAIL: empty array -> " + r);

        total++; r = s.minSubArrayLen(15, new int[]{1, 2, 3, 4, 5});
                 if (r == 5) passed++; else System.out.println("FAIL: whole-array sum target=15 [1,2,3,4,5] -> " + r);

        total++; r = s.minSubArrayLen(3, new int[]{1, 1, 1});
                 if (r == 3) passed++; else System.out.println("FAIL: target=3 [1,1,1] -> " + r);

        total++; r = s.minSubArrayLen(5, new int[]{2, 3, 1, 1});
                 if (r == 2) passed++; else System.out.println("FAIL: target=5 [2,3,1,1] -> " + r);

        total++; r = s.minSubArrayLen(6, new int[]{10, 2, 3});
                 if (r == 1) passed++; else System.out.println("FAIL: target=6 [10,2,3] (single big) -> " + r);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
