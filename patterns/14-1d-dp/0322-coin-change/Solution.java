class Solution {
    public int coinChange(int[] coins, int amount) {
        int inf = amount + 1;
        int[] dp = new int[amount + 1];
        java.util.Arrays.fill(dp, inf);
        dp[0] = 0;
        for (int a = 1; a <= amount; a++) {
            for (int c : coins) {
                if (c <= a) {
                    // Reaching amount a by adding coin c to the optimal solution for a-c.
                    dp[a] = Math.min(dp[a], dp[a - c] + 1);
                }
            }
        }
        return dp[amount] == inf ? -1 : dp[amount];
    }
}
