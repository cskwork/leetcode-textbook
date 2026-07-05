# 0994 - Rotting Oranges

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/rotting-oranges/

## Problem

Every minute, any fresh orange (`1`) that is 4-directionally adjacent to a rotten orange (`2`)
becomes rotten. Given an `m x n` `grid`, return the **minimum number of minutes** that must elapse
until no cell has a fresh orange. If that is impossible (some fresh orange can never be reached),
return `-1`. A `0` cell is empty.

Signature:

    int orangesRotting(int[][] grid)

Examples:

    Input:  grid = [[2,1,1],[1,1,0],[0,1,1]]
    Output: 4

    Input:  grid = [[2,1,1],[0,1,1],[1,0,1]]
    Output: -1    (the bottom-left orange is walled off and never rots)

    Input:  grid = [[0,2]]
    Output: 0     (no fresh oranges -> zero minutes)

## Intuition

"Minutes until X" on an unweighted grid is the textbook shortest-path question, and shortest path
on an unweighted graph means **BFS** -- never DFS. The twist: rotting starts from *every* initially
rotten orange simultaneously, not from one source. That is exactly **multi-source BFS** (see the
pattern README): seed the queue with all initial rotten cells, mark them visited, then run ordinary
BFS. The number of BFS levels you drain (until the queue holds only the last wave) is the answer.

A bookkeeping detail: count fresh oranges up front. Each time a fresh orange rots, decrement the
counter. If the counter hits `0`, the current level count is the answer; if BFS ends with the
counter still positive, those orphans can never rot -- return `-1`.

## Pseudocode

    function orangesRotting(grid):
        queue = empty queue
        fresh = 0
        for each cell (r, c):
            if grid[r][c] == 2: enqueue (r, c)        # seed every rotten source
            else if grid[r][c] == 1: fresh += 1

        if fresh == 0: return 0                        # nothing to rot

        minutes = -1                                    # becomes 0 after draining the seed level
        while queue is not empty:
            levelSize = current queue size
            repeat levelSize times:                     # drain exactly one "wave"
                (r, c) = dequeue
                for each neighbor (nr, nc):
                    if in bounds and grid[nr][nc] == 1:
                        grid[nr][nc] = 2                # rot it (= mark visited)
                        fresh -= 1
                        enqueue (nr, nc)
            minutes += 1                                # one minute elapsed for this wave

        if fresh > 0: return -1                         # orphans remain
        return minutes

The `minutes = -1` start is deliberate: the first iteration of the while loop drains the *seed*
level (the initially rotten oranges), and rotting has not actually spread yet, so we should *not*
count that level. Starting at `-1` and incrementing at the end of each loop means the seed level
ends at `0` (correct -- no time elapsed just from having rotten sources), the first real wave ends
at `1`, and so on.

## Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    public int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Deque<int[]> queue = new ArrayDeque<>();
        int fresh = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }

        if (fresh == 0) {
            return 0;
        }

        int minutes = -1;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                int[] cell = queue.poll();
                int r = cell[0], c = cell[1];
                for (int k = 0; k < 4; k++) {
                    int nr = r + DR[k], nc = c + DC[k];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;
                        fresh--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
            minutes++;
        }

        return fresh > 0 ? -1 : minutes;
    }
}
```

`ArrayDeque<int[]>` is the recommended queue (see `03-java-crash-course.md` section 4 -- faster
than the legacy `LinkedList`). Each queue entry is a two-element `int[]` holding `(row, col)`.
Snapshotting `levelSize = queue.size()` before the inner loop is what makes "one wave = one minute"
work -- without it you would drain the whole queue as if it were one level and lose the time
dimension. Mutating the grid (`1 -> 2`) replaces a visited array AND decrements `fresh`, so a single
write does triple duty. The final `fresh > 0 ? -1 : minutes` collapses two return paths into one.

## Complexity

    Time:  O(m * n)  -- each cell enters the queue at most once
    Space: O(m * n)  -- queue can hold every cell in the worst (all-rotten) case

## Dry-Run

Input:

```
2 1 1
1 1 0
0 1 1
```

Initial scan: seed `(0,0)` into the queue, `fresh = 6`.

| Minute | Queue before drain    | Cells rotted this wave        | fresh after |
|-------:|-----------------------|-------------------------------|------------:|
| seed   | [(0,0)]               | (none -- this is the seed)    | 6           |
| 0->1   | [(0,1),(1,0)]         | (0,1) and (1,0) rot           | 4           |
| 1->2   | [(0,2),(1,1)]         | (0,2) and (1,1) rot           | 2           |
| 2->3   | [(2,1)]               | (2,1) rots                    | 1           |
| 3->4   | [(2,2)]               | (2,2) rots                    | 0           |

Trace: `minutes` starts at `-1`. After draining seed level, `minutes = 0`. After wave 1, `minutes =
1`. ... After wave 4, `minutes = 4`. Queue empties; `fresh == 0`. Return `4`.

For the unreachable case `[[2,1,1],[0,1,1],[1,0,1]]`: BFS rots `(0,1),(1,0? no),(0,2),(1,1),(2,1?)`
-- but `(2,0)` is surrounded by `0` cells and the wall, so it never gets a rotten neighbor. BFS
ends with `fresh == 1` (the `(2,0)` orange), so we return `-1`.

## Common mistakes

- Using DFS instead of BFS. DFS finds *a* path to each orange but not the *minimum time*, because
  rotting from different sources reaches different oranges at different times.
- Counting the seed level as minute 1. Initialise `minutes = -1` (or count levels *after* the seed
  drain) to avoid an off-by-one.
- Seeding only one rotten orange and looping BFS for each -- this gives wrong (too large) times
  because each source would start its clock independently. Seed *all* sources up front.
- Forgetting to check `grid[nr][nc] == 1` before rotting, which lets you re-enqueue already-rotten
  or empty cells and inflate the wave count.
- Returning `0` only when the grid is all-zero. The correct early-return condition is `fresh == 0`
  -- any grid with no fresh oranges (including all-rotten or all-empty) takes 0 minutes.

## Related problems

- [0417 - Pacific Atlantic Water Flow](../0417-pacific-atlantic-water-flow/) - same multi-source
  idea but DFS for reachability (no time dimension).
- [0200 - Number of Islands](../0200-number-of-islands/) - the grid DFS counterpart; good
  contrast for "when to pick BFS over DFS".
- [0286 - Walls and Gates](https://leetcode.com/problems/walls-and-gates/) - multi-source BFS that
  records distance into each cell.
