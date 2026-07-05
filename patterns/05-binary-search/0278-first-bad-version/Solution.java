import java.util.*;

class Solution {
    public int firstBadVersion(int n) {
        int lo = 1, hi = n;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (isBadVersion(mid)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    // Overridden by tests to model a specific "bad" threshold.
    protected boolean isBadVersion(int version) {
        return false;
    }
}
