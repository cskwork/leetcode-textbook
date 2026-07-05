class Solution {
    public int minCostClimbingStairs(int[] cost) {
        int prev2 = 0;          // dp[0]
        int prev1 = 0;          // dp[1]
        for (int i = 2; i <= cost.length; i++) {
            // dp[i] = cheaper of: arrive from i-1 (pay cost[i-1]) or i-2 (pay cost[i-2])
            int current = Math.min(prev1 + cost[i - 1], prev2 + cost[i - 2]);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
