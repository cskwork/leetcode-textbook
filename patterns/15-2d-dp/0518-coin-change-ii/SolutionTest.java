public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.change(5, new int[]{1, 2, 5});
                 if (r1 == 4) passed++; else System.out.println("FAIL: amount=5 [1,2,5] -> " + r1);

        total++; int r2 = s.change(3, new int[]{2});
                 if (r2 == 0) passed++; else System.out.println("FAIL: amount=3 [2] -> " + r2);

        total++; int r3 = s.change(10, new int[]{10});
                 if (r3 == 1) passed++; else System.out.println("FAIL: amount=10 [10] -> " + r3);

        total++; int r4 = s.change(0, new int[]{7});
                 if (r4 == 1) passed++; else System.out.println("FAIL: amount=0 [7] -> " + r4);

        total++; int r5 = s.change(5, new int[]{});
                 if (r5 == 0) passed++; else System.out.println("FAIL: amount=5 [] -> " + r5);

        total++; int r6 = s.change(4, new int[]{1, 2});
                 if (r6 == 3) passed++; else System.out.println("FAIL: amount=4 [1,2] -> " + r6);

        total++; int r7 = s.change(0, new int[]{});
                 if (r7 == 1) passed++; else System.out.println("FAIL: amount=0 [] -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
