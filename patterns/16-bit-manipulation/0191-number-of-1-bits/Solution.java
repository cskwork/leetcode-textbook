class Solution {
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            // n & (n-1) clears the lowest set bit; each iteration removes
            // exactly one 1-bit, so the loop runs once per set bit and
            // works on signed ints (two's complement) without >>>.
            n &= (n - 1);
            count++;
        }
        return count;
    }
}
