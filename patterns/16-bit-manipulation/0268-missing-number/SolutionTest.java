public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.missingNumber(new int[]{3, 0, 1});
                  if (r1 == 2) passed++; else System.out.println("FAIL: example [3,0,1] -> " + r1);

        total++; int r2 = s.missingNumber(new int[]{0, 1});
                  if (r2 == 2) passed++; else System.out.println("FAIL: example [0,1] -> " + r2);

        total++; int r3 = s.missingNumber(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1});
                  if (r3 == 8) passed++; else System.out.println("FAIL: example [9,6,4,2,3,5,7,0,1] -> " + r3);

        total++; int r4 = s.missingNumber(new int[]{0});
                  if (r4 == 1) passed++; else System.out.println("FAIL: missing-last [0] -> " + r4);

        total++; int r5 = s.missingNumber(new int[]{1});
                  if (r5 == 0) passed++; else System.out.println("FAIL: missing-zero [1] -> " + r5);

        total++; int r6 = s.missingNumber(new int[]{0, 1, 2});
                  if (r6 == 3) passed++; else System.out.println("FAIL: missing-n [0,1,2] -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
