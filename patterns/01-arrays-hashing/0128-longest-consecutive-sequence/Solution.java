import java.util.HashSet;
import java.util.Set;

class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) {
            set.add(x);
        }

        int best = 0;
        for (int x : set) {
            if (!set.contains(x - 1)) {
                int length = 1;
                int next = x + 1;
                while (set.contains(next)) {
                    length++;
                    next++;
                }
                best = Math.max(best, length);
            }
        }
        return best;
    }
}
