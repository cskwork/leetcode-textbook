public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.isPalindrome(build(1, 2, 2, 1));
                  if (r1) passed++; else System.out.println("FAIL: even-palindrome -> " + r1);

        total++; boolean r2 = s.isPalindrome(build(1, 2));
                  if (!r2) passed++; else System.out.println("FAIL: two-non-palindrome -> " + r2);

        total++; boolean r3 = s.isPalindrome(build(1, 2, 3, 2, 1));
                  if (r3) passed++; else System.out.println("FAIL: odd-palindrome -> " + r3);

        total++; boolean r4 = s.isPalindrome(build(1));
                  if (r4) passed++; else System.out.println("FAIL: single-node -> " + r4);

        total++; boolean r5 = s.isPalindrome(null);
                  if (r5) passed++; else System.out.println("FAIL: empty -> " + r5);

        total++; boolean r6 = s.isPalindrome(build(1, 0, 0));
                  if (!r6) passed++; else System.out.println("FAIL: near-palindrome -> " + r6);

        total++; boolean r7 = s.isPalindrome(build(1, 0, 1));
                  if (r7) passed++; else System.out.println("FAIL: odd-with-zero -> " + r7);

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
}
