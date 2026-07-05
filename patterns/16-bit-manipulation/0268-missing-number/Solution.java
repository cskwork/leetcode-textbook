class Solution {
    public int missingNumber(int[] nums) {
        int n = nums.length;
        // Seed with n so the target range {0..n} is fully represented once.
        // Every value present in the array cancels its copy in the range
        // (x ^ x == 0); the one value missing from the array survives.
        int running = n;
        for (int i = 0; i < n; i++) {
            running ^= i ^ nums[i];
        }
        return running;
    }
}
