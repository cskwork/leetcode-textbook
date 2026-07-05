class Solution {
    public boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        if (total % 2 != 0) {
            return false;
        }
        int target = total / 2;

        int n = nums.length;
        boolean[][] dp = new boolean[n + 1][target + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        for (int i = 1; i <= n; i++) {
            for (int t = 1; t <= target; t++) {
                dp[i][t] = dp[i - 1][t];
                if (t >= nums[i - 1]) {
                    dp[i][t] = dp[i][t] || dp[i - 1][t - nums[i - 1]];
                }
            }
        }

        return dp[n][target];
    }
}
