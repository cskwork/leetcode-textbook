import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> results = new ArrayList<>();
        Arrays.sort(candidates);                                   // enables clean pruning
        backtrack(results, new ArrayList<>(), 0, target, candidates);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           int start, int remaining, int[] candidates) {
        if (remaining == 0) {
            results.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;                  // sorted -> prune rest
            path.add(candidates[i]);                               // CHOOSE
            backtrack(results, path, i, remaining - candidates[i], candidates); // EXPLORE, reuse i
            path.remove(path.size() - 1);                          // UN-CHOOSE
        }
    }
}
