public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // LC example 1: "bab" or "aba" both accepted.
        total++;
        String r1 = s.longestPalindrome("babad");
        if (r1.equals("bab") || r1.equals("aba")) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        // LC example 2: even-length palindrome.
        total++;
        String r2 = s.longestPalindrome("cbbd");
        if (r2.equals("bb")) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: single character.
        total++;
        String r3 = s.longestPalindrome("a");
        if (r3.equals("a")) passed++;
        else System.out.println("FAIL: single char -> " + r3);

        // Whole string is a palindrome.
        total++;
        String r4 = s.longestPalindrome("racecar");
        if (r4.equals("racecar")) passed++;
        else System.out.println("FAIL: whole-string palindrome -> " + r4);

        // All same characters -> the whole string.
        total++;
        String r5 = s.longestPalindrome("aaaa");
        if (r5.equals("aaaa")) passed++;
        else System.out.println("FAIL: all same -> " + r5);

        // No palindrome longer than 1 -- any single char is valid.
        total++;
        String r6 = s.longestPalindrome("abc");
        if (r6.length() == 1) passed++;
        else System.out.println("FAIL: no palindrome > 1 -> " + r6);

        // Two-char input, even palindrome.
        total++;
        String r7 = s.longestPalindrome("bb");
        if (r7.equals("bb")) passed++;
        else System.out.println("FAIL: 'bb' -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
