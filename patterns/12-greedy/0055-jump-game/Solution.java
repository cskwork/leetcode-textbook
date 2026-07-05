class Solution {
    public boolean canJump(int[] nums) {
        int farthest = 0;
        int last = nums.length - 1;
        for (int i = 0; i <= last; i++) {
            // If we cannot even reach index i, the goal is unreachable.
            if (i > farthest) {
                return false;
            }
            int reach = i + nums[i];
            if (reach > farthest) {
                farthest = reach;
            }
            if (farthest >= last) {
                return true;
            }
        }
        return true;
    }
}
