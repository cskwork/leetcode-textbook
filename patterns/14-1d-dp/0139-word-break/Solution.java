import java.util.List;
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            for (String w : wordDict) {
                int len = w.length();
                // A word w is a valid last segment of s[0..i) iff it fits,
                // the prefix before it was segmentable, and the tail matches w.
                if (len <= i && dp[i - len] && s.startsWith(w, i - len)) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }
}
