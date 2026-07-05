class Solution {
    public int maxSubArray(int[] nums) {
        int best = Integer.MIN_VALUE;
        int running = 0;
        for (int x : nums) {
            running += x;
            if (running > best) {
                best = running;
            }
            // A negative prefix can only drag down any future subarray,
            // so discard it and start the next subarray fresh.
            if (running < 0) {
                running = 0;
            }
        }
        return best;
    }
}
