import java.util.Arrays;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; ListNode r1 = build(1, 2, 3, 4);
                 s.reorderList(r1);
                 if (Arrays.equals(toArray(r1), new int[]{1, 4, 2, 3})) passed++;
                 else System.out.println("FAIL: even-four -> " + Arrays.toString(toArray(r1)));

        total++; ListNode r2 = build(1, 2, 3, 4, 5);
                 s.reorderList(r2);
                 if (Arrays.equals(toArray(r2), new int[]{1, 5, 2, 4, 3})) passed++;
                 else System.out.println("FAIL: odd-five -> " + Arrays.toString(toArray(r2)));

        total++; ListNode r3 = build(1);
                 s.reorderList(r3);
                 if (Arrays.equals(toArray(r3), new int[]{1})) passed++;
                 else System.out.println("FAIL: single -> " + Arrays.toString(toArray(r3)));

        total++; ListNode r4 = build(1, 2);
                 s.reorderList(r4);
                 if (Arrays.equals(toArray(r4), new int[]{1, 2})) passed++;
                 else System.out.println("FAIL: two -> " + Arrays.toString(toArray(r4)));

        total++; ListNode r5 = build(1, 2, 3);
                 s.reorderList(r5);
                 if (Arrays.equals(toArray(r5), new int[]{1, 3, 2})) passed++;
                 else System.out.println("FAIL: three -> " + Arrays.toString(toArray(r5)));

        total++; ListNode r6 = build(1, 2, 3, 4, 5, 6);
                 s.reorderList(r6);
                 if (Arrays.equals(toArray(r6), new int[]{1, 6, 2, 5, 3, 4})) passed++;
                 else System.out.println("FAIL: even-six -> " + Arrays.toString(toArray(r6)));

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
