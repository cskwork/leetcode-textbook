import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Solution {
    @SuppressWarnings("unchecked")
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.merge(x, 1, Integer::sum);
        }

        List<Integer>[] bucket = new List[nums.length + 1];
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            int f = e.getValue();
            if (bucket[f] == null) {
                bucket[f] = new ArrayList<>();
            }
            bucket[f].add(e.getKey());
        }

        int[] result = new int[k];
        int pos = 0;
        for (int f = bucket.length - 1; f >= 0 && pos < k; f--) {
            if (bucket[f] != null) {
                for (int v : bucket[f]) {
                    if (pos < k) {
                        result[pos++] = v;
                    }
                }
            }
        }
        return result;
    }
}
