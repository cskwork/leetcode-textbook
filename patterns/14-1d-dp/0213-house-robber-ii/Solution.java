class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        // Either skip the last house (range [0..n-2]) or skip the first ([1..n-1]).
        return Math.max(robLinear(nums, 0, n - 2), robLinear(nums, 1, n - 1));
    }

    private int robLinear(int[] nums, int lo, int hi) {
        int prev2 = 0;
        int prev1 = nums[lo];
        for (int i = lo + 1; i <= hi; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
