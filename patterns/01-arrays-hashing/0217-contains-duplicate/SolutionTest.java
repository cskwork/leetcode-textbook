public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.containsDuplicate(new int[]{1, 2, 3, 1});
                  if (r1) passed++; else System.out.println("FAIL: example [1,2,3,1] -> " + r1);

        total++; boolean r2 = s.containsDuplicate(new int[]{1, 2, 3, 4});
                  if (!r2) passed++; else System.out.println("FAIL: no-dup [1,2,3,4] -> " + r2);

        total++; boolean r3 = s.containsDuplicate(new int[]{1, 1, 1, 1});
                  if (r3) passed++; else System.out.println("FAIL: all-equal [1,1,1,1] -> " + r3);

        total++; boolean r4 = s.containsDuplicate(new int[]{});
                  if (!r4) passed++; else System.out.println("FAIL: empty -> " + r4);

        total++; boolean r5 = s.containsDuplicate(new int[]{42});
                  if (!r5) passed++; else System.out.println("FAIL: single [42] -> " + r5);

        total++; boolean r6 = s.containsDuplicate(new int[]{5, 7, 9, 7});
                  if (r6) passed++; else System.out.println("FAIL: late-dup [5,7,9,7] -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
