class Solution {
    public int singleNumber(int[] nums) {
        int running = 0;
        for (int v : nums) {
            // x ^ x == 0 and x ^ 0 == x: pairs cancel across the array,
            // leaving the one value that appears an odd number of times.
            running = running ^ v;
        }
        return running;
    }
}
