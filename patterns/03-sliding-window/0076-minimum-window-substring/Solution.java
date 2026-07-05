class Solution {
    public String minWindow(String s, String t) {
        int[] need = new int[128];
        int[] window = new int[128];
        for (char c : t.toCharArray()) {
            need[c]++;
        }
        int required = 0;
        for (int n : need) {
            if (n > 0) required++;
        }

        int formed = 0;
        int left = 0;
        int bestLen = Integer.MAX_VALUE;
        int bestStart = 0;

        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            if (need[ch] > 0) {
                window[ch]++;
                if (window[ch] == need[ch]) {
                    formed++;
                }
            }
            while (formed == required) {
                if (right - left + 1 < bestLen) {
                    bestLen = right - left + 1;
                    bestStart = left;
                }
                char out = s.charAt(left);
                if (need[out] > 0) {
                    if (window[out] == need[out]) {
                        formed--;
                    }
                    window[out]--;
                }
                left++;
            }
        }
        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
    }
}
