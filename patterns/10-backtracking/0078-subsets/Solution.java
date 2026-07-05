import java.util.*;

class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        backtrack(results, new ArrayList<>(), 0, nums);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           int start, int[] nums) {
        results.add(new ArrayList<>(path));        // snapshot every node
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);                      // CHOOSE
            backtrack(results, path, i + 1, nums);  // EXPLORE only later indices
            path.remove(path.size() - 1);           // UN-CHOOSE
        }
    }
}
