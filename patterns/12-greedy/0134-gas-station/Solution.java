class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int totalSurplus = 0;
        int tank = 0;
        int start = 0;
        for (int i = 0; i < gas.length; i++) {
            int surplus = gas[i] - cost[i];
            totalSurplus += surplus;
            tank += surplus;
            // A prefix that drives the tank negative can never help a
            // later start within it: starting later only loses this prefix's
            // contribution. So skip the whole block.
            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }
        if (totalSurplus < 0) {
            return -1;
        }
        return start;
    }
}
