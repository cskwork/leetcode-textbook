import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; ListNode r1 = s.mergeTwoLists(build(1, 2, 4), build(1, 3, 4));
                 if (Arrays.equals(toArray(r1), new int[]{1, 1, 2, 3, 4, 4})) passed++;
                 else System.out.println("FAIL: example1 -> " + Arrays.toString(toArray(r1)));

        total++; ListNode r2 = s.mergeTwoLists(null, null);
                 if (r2 == null) passed++;
                 else System.out.println("FAIL: both-empty -> " + Arrays.toString(toArray(r2)));

        total++; ListNode r3 = s.mergeTwoLists(null, build(0));
                 if (Arrays.equals(toArray(r3), new int[]{0})) passed++;
                 else System.out.println("FAIL: one-empty -> " + Arrays.toString(toArray(r3)));

        total++; ListNode r4 = s.mergeTwoLists(build(5), build(1, 2, 3));
                 if (Arrays.equals(toArray(r4), new int[]{1, 2, 3, 5})) passed++;
                 else System.out.println("FAIL: prepend-all -> " + Arrays.toString(toArray(r4)));

        total++; ListNode r5 = s.mergeTwoLists(build(1, 3, 5), build(2, 4, 6));
                 if (Arrays.equals(toArray(r5), new int[]{1, 2, 3, 4, 5, 6})) passed++;
                 else System.out.println("FAIL: interleave -> " + Arrays.toString(toArray(r5)));

        total++; ListNode r6 = s.mergeTwoLists(build(1, 1, 1), build(1, 1));
                 if (Arrays.equals(toArray(r6), new int[]{1, 1, 1, 1, 1})) passed++;
                 else System.out.println("FAIL: ties -> " + Arrays.toString(toArray(r6)));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    private static int[] toArray(ListNode head) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (ListNode p = head; p != null; p = p.next) list.add(p.val);
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }
}
