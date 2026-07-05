class Solution {
    public String longestPalindrome(String s) {
        int n = s.length();
        if (n == 0) {
            return "";
        }
        int start = 0;
        int best = 1;
        for (int center = 0; center < n; center++) {
            int lenOdd = expand(s, center, center);
            int lenEven = expand(s, center, center + 1);
            int len = Math.max(lenOdd, lenEven);
            if (len > best) {
                best = len;
                start = center - (len - 1) / 2;
            }
        }
        return s.substring(start, start + best);
    }

    private int expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        // After the loop, s[left] != s[right] (or an edge was hit),
        // so the palindrome is (left+1 .. right-1), length (right - left - 1).
        return right - left - 1;
    }
}
