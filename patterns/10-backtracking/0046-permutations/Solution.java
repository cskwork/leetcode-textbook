import java.util.*;

class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(results, new ArrayList<>(), used, nums);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           boolean[] used, int[] nums) {
        if (path.size() == nums.length) {
            results.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            path.add(nums[i]);                                  // CHOOSE
            used[i] = true;
            backtrack(results, path, used, nums);               // EXPLORE
            used[i] = false;                                    // UN-CHOOSE
            path.remove(path.size() - 1);
        }
    }
}
