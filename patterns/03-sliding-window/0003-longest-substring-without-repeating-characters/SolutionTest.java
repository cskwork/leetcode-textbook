public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r = s.lengthOfLongestSubstring("abcabcbb");
                 if (r == 3) passed++; else System.out.println("FAIL: example 1 \"abcabcbb\" -> " + r);

        total++; r = s.lengthOfLongestSubstring("bbbbb");
                 if (r == 1) passed++; else System.out.println("FAIL: example 2 \"bbbbb\" -> " + r);

        total++; r = s.lengthOfLongestSubstring("pwwkew");
                 if (r == 3) passed++; else System.out.println("FAIL: example 3 \"pwwkew\" -> " + r);

        total++; r = s.lengthOfLongestSubstring("");
                 if (r == 0) passed++; else System.out.println("FAIL: empty string -> " + r);

        total++; r = s.lengthOfLongestSubstring(" ");
                 if (r == 1) passed++; else System.out.println("FAIL: single space \" \" -> " + r);

        total++; r = s.lengthOfLongestSubstring("a");
                 if (r == 1) passed++; else System.out.println("FAIL: single char \"a\" -> " + r);

        total++; r = s.lengthOfLongestSubstring("abba");
                 if (r == 2) passed++; else System.out.println("FAIL: tricky \"abba\" -> " + r);

        total++; r = s.lengthOfLongestSubstring("tmmzuxt");
                 if (r == 5) passed++; else System.out.println("FAIL: tricky \"tmmzuxt\" -> " + r);

        total++; r = s.lengthOfLongestSubstring("dvdf");
                 if (r == 3) passed++; else System.out.println("FAIL: \"dvdf\" -> " + r);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
