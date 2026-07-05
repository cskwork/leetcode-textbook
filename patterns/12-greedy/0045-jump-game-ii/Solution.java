class Solution {
    public int jump(int[] nums) {
        int last = nums.length - 1;
        if (last == 0) {
            return 0;
        }
        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;
        for (int i = 0; i <= last; i++) {
            int reach = i + nums[i];
            if (reach > farthest) {
                farthest = reach;
            }
            // If the next level already covers the goal, take it and stop.
            if (farthest >= last) {
                return jumps + 1;
            }
            // Finished scanning the current level: commit one jump and
            // advance the level boundary to everything we can now reach.
            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
            }
        }
        return jumps;
    }
}
