import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; ListNode r1 = s.removeNthFromEnd(build(1, 2, 3, 4, 5), 2);
                 if (Arrays.equals(toArray(r1), new int[]{1, 2, 3, 5})) passed++;
                 else System.out.println("FAIL: example1 -> " + Arrays.toString(toArray(r1)));

        total++; ListNode r2 = s.removeNthFromEnd(build(1), 1);
                 if (r2 == null) passed++;
                 else System.out.println("FAIL: single-remove -> " + Arrays.toString(toArray(r2)));

        total++; ListNode r3 = s.removeNthFromEnd(build(1, 2), 1);
                 if (Arrays.equals(toArray(r3), new int[]{1})) passed++;
                 else System.out.println("FAIL: remove-tail -> " + Arrays.toString(toArray(r3)));

        total++; ListNode r4 = s.removeNthFromEnd(build(1, 2), 2);
                 if (Arrays.equals(toArray(r4), new int[]{2})) passed++;
                 else System.out.println("FAIL: remove-head -> " + Arrays.toString(toArray(r4)));

        total++; ListNode r5 = s.removeNthFromEnd(build(1, 2, 3, 4, 5), 5);
                 if (Arrays.equals(toArray(r5), new int[]{2, 3, 4, 5})) passed++;
                 else System.out.println("FAIL: remove-head-of-five -> " + Arrays.toString(toArray(r5)));

        total++; ListNode r6 = s.removeNthFromEnd(build(1, 2, 3, 4, 5), 1);
                 if (Arrays.equals(toArray(r6), new int[]{1, 2, 3, 4})) passed++;
                 else System.out.println("FAIL: remove-last-of-five -> " + Arrays.toString(toArray(r6)));

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
