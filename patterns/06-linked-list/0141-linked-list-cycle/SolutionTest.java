public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.hasCycle(buildCycle(new int[]{3, 2, 0, -4}, 1));
                  if (r1) passed++; else System.out.println("FAIL: example1-cycle -> " + r1);

        total++; boolean r2 = s.hasCycle(buildCycle(new int[]{1, 2}, 0));
                  if (r2) passed++; else System.out.println("FAIL: two-node-cycle -> " + r2);

        total++; boolean r3 = s.hasCycle(buildCycle(new int[]{3, 2, 0, -4}, -1));
                  if (!r3) passed++; else System.out.println("FAIL: flat-four -> " + r3);

        total++; boolean r4 = s.hasCycle(null);
                  if (!r4) passed++; else System.out.println("FAIL: empty -> " + r4);

        total++; boolean r5 = s.hasCycle(buildCycle(new int[]{1}, -1));
                  if (!r5) passed++; else System.out.println("FAIL: single-flat -> " + r5);

        total++; boolean r6 = s.hasCycle(buildCycle(new int[]{1}, 0));
                  if (r6) passed++; else System.out.println("FAIL: single-self-cycle -> " + r6);

        total++; boolean r7 = s.hasCycle(buildCycle(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 4));
                  if (r7) passed++; else System.out.println("FAIL: long-cycle -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    // Build a list of the given values; if pos >= 0 connect the tail's .next back to node[pos].
    private static ListNode buildCycle(int[] vals, int pos) {
        ListNode[] nodes = new ListNode[vals.length];
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new ListNode(vals[i]);
            tail.next = nodes[i];
            tail = nodes[i];
        }
        if (pos >= 0) {
            tail.next = nodes[pos];
        }
        return dummy.next;
    }
}
