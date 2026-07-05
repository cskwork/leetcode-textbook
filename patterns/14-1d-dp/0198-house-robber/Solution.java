class Solution {
    public int rob(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        int prev2 = 0;                 // dp[0]
        int prev1 = nums[0];           // dp[1]
        for (int i = 2; i <= nums.length; i++) {
            // Rob house i-1 (so add nums[i-1] to dp[i-2]) OR skip it (keep dp[i-1]).
            int current = Math.max(prev1, prev2 + nums[i - 1]);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
