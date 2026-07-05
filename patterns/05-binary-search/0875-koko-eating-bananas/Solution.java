import java.util.*;

class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        int lo = 1, hi = 1;
        for (int p : piles) {
            hi = Math.max(hi, p);
        }
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canFinish(piles, h, mid)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    private boolean canFinish(int[] piles, int h, int speed) {
        long hours = 0;
        for (int p : piles) {
            hours += (p + speed - 1) / speed;     // ceil(p / speed)
            if (hours > h) {
                return false;
            }
        }
        return hours <= h;
    }
}
