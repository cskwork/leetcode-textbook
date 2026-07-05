class Solution {
    public int characterReplacement(String s, int k) {
        int[] freq = new int[26];
        int left = 0;
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            freq[s.charAt(right) - 'A']++;
            while ((right - left + 1) - maxFreq(freq) > k) {
                freq[s.charAt(left) - 'A']--;
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    private int maxFreq(int[] freq) {
        int m = 0;
        for (int c : freq) {
            m = Math.max(m, c);
        }
        return m;
    }
}
