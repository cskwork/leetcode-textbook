import java.util.Arrays;

public class SolutionTest {
    static String sortedStr(int[] a) {
        int[] c = a.clone();
        Arrays.sort(c);
        return Arrays.toString(c);
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; String r1 = sortedStr(s.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2));
                  if (r1.equals("[1, 2]")) passed++; else System.out.println("FAIL: example -> " + r1);

        total++; String r2 = sortedStr(s.topKFrequent(new int[]{1}, 1));
                  if (r2.equals("[1]")) passed++; else System.out.println("FAIL: single -> " + r2);

        total++; String r3 = sortedStr(s.topKFrequent(new int[]{1, 2}, 2));
                  if (r3.equals("[1, 2]")) passed++; else System.out.println("FAIL: all-freq-1 -> " + r3);

        total++; String r4 = sortedStr(s.topKFrequent(new int[]{3, 3, 3, 1, 1, 1, 2, 2, 4}, 2));
                  if (r4.equals("[1, 3]")) passed++; else System.out.println("FAIL: tie-at-top -> " + r4);

        total++; String r5 = sortedStr(s.topKFrequent(new int[]{5, 5, 5, 5, 4, 4, 4, 3, 3, 2, 1}, 3));
                  if (r5.equals("[3, 4, 5]")) passed++; else System.out.println("FAIL: mixed -> " + r5);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
