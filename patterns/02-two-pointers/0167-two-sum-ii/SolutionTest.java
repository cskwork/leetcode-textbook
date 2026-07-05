import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        int[] r1 = s.twoSum(new int[]{2, 7, 11, 15}, 9);
        if (r1[0] == 1 && r1[1] == 2) passed++;
        else System.out.println("FAIL: LC example 1 -> " + Arrays.toString(r1));

        total++;
        int[] r2 = s.twoSum(new int[]{2, 3, 4}, 6);
        if (r2[0] == 1 && r2[1] == 3) passed++;
        else System.out.println("FAIL: LC example 2 -> " + Arrays.toString(r2));

        total++;
        int[] r3 = s.twoSum(new int[]{-1, 0}, -1);
        if (r3[0] == 1 && r3[1] == 2) passed++;
        else System.out.println("FAIL: LC example 3 (negatives) -> " + Arrays.toString(r3));

        total++;
        int[] r4 = s.twoSum(new int[]{1, 2, 3, 4, 4, 9, 56, 90}, 8);
        if (r4[0] == 4 && r4[1] == 5) passed++;
        else System.out.println("FAIL: duplicate values -> " + Arrays.toString(r4));

        total++;
        int[] r5 = s.twoSum(new int[]{5, 25, 75}, 100);
        if (r5[0] == 2 && r5[1] == 3) passed++;
        else System.out.println("FAIL: three-element, answer at ends -> " + Arrays.toString(r5));

        total++;
        int[] r6 = s.twoSum(new int[]{-5, -3, -1, 0, 2}, -4);
        if (r6[0] == 2 && r6[1] == 3) passed++;
        else System.out.println("FAIL: negative target -> " + Arrays.toString(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
