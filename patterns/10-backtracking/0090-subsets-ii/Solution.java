import java.util.*;

class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        Arrays.sort(nums);                                   // puts equal values adjacent
        backtrack(results, new ArrayList<>(), 0, nums);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           int start, int[] nums) {
        results.add(new ArrayList<>(path));                  // every node is a subset
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue; // skip sibling duplicate
            path.add(nums[i]);                                // CHOOSE
            backtrack(results, path, i + 1, nums);            // EXPLORE later indices
            path.remove(path.size() - 1);                     // UN-CHOOSE
        }
    }
}
