import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; ListNode r1 = s.addTwoNumbers(build(2, 4, 3), build(5, 6, 4));
                 if (Arrays.equals(toArray(r1), new int[]{7, 0, 8})) passed++;
                 else System.out.println("FAIL: example1 -> " + Arrays.toString(toArray(r1)));

        total++; ListNode r2 = s.addTwoNumbers(build(0), build(0));
                 if (Arrays.equals(toArray(r2), new int[]{0})) passed++;
                 else System.out.println("FAIL: zeros -> " + Arrays.toString(toArray(r2)));

        total++; ListNode r3 = s.addTwoNumbers(build(9, 9, 9, 9, 9, 9, 9), build(9, 9, 9, 9));
                 if (Arrays.equals(toArray(r3), new int[]{8, 9, 9, 9, 0, 0, 0, 1})) passed++;
                 else System.out.println("FAIL: long-carry -> " + Arrays.toString(toArray(r3)));

        total++; ListNode r4 = s.addTwoNumbers(build(5), build(5));
                 if (Arrays.equals(toArray(r4), new int[]{0, 1})) passed++;
                 else System.out.println("FAIL: single-carry -> " + Arrays.toString(toArray(r4)));

        total++; ListNode r5 = s.addTwoNumbers(build(1, 8), build(0));
                 if (Arrays.equals(toArray(r5), new int[]{1, 8})) passed++;
                 else System.out.println("FAIL: uneven-short-second -> " + Arrays.toString(toArray(r5)));

        total++; ListNode r6 = s.addTwoNumbers(build(9, 9, 9), build(1));
                 if (Arrays.equals(toArray(r6), new int[]{0, 0, 0, 1})) passed++;
                 else System.out.println("FAIL: carry-propagation -> " + Arrays.toString(toArray(r6)));

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
