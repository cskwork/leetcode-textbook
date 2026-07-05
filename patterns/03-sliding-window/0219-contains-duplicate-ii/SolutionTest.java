public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r = s.containsNearbyDuplicate(new int[]{1, 2, 3, 1}, 3);
                 if (r) passed++; else System.out.println("FAIL: example 1 [1,2,3,1] k=3 -> " + r);

        total++; r = s.containsNearbyDuplicate(new int[]{1, 0, 1, 1}, 1);
                 if (r) passed++; else System.out.println("FAIL: example 2 [1,0,1,1] k=1 -> " + r);

        total++; r = s.containsNearbyDuplicate(new int[]{1, 2, 3, 1, 2, 3}, 2);
                 if (!r) passed++; else System.out.println("FAIL: example 3 [1,2,3,1,2,3] k=2 -> " + r);

        total++; r = s.containsNearbyDuplicate(new int[]{}, 0);
                 if (!r) passed++; else System.out.println("FAIL: empty array -> " + r);

        total++; r = s.containsNearbyDuplicate(new int[]{1}, 1);
                 if (!r) passed++; else System.out.println("FAIL: single element [1] -> " + r);

        total++; r = s.containsNearbyDuplicate(new int[]{1, 1}, 0);
                 if (!r) passed++; else System.out.println("FAIL: [1,1] k=0 -> " + r);

        total++; r = s.containsNearbyDuplicate(new int[]{1, 1}, 1);
                 if (r) passed++; else System.out.println("FAIL: [1,1] k=1 -> " + r);

        total++; r = s.containsNearbyDuplicate(new int[]{1, 2, 3, 4, 5, 1}, 4);
                 if (!r) passed++; else System.out.println("FAIL: [1,2,3,4,5,1] k=4 (distance 5 > 4) -> " + r);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
