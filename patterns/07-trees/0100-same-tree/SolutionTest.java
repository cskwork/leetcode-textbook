import java.util.ArrayDeque;
import java.util.Deque;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.isSameTree(build(1, 2, 3), build(1, 2, 3));
                 if (r1) passed++; else System.out.println("FAIL: identical [1,2,3] -> " + r1);

        total++; boolean r2 = s.isSameTree(build(1, 2), build(1, null, 2));
                 if (!r2) passed++; else System.out.println("FAIL: shape mismatch -> " + r2);

        total++; boolean r3 = s.isSameTree(build(1, 2, 1), build(1, 1, 2));
                 if (!r3) passed++; else System.out.println("FAIL: same shape, diff vals -> " + r3);

        total++; boolean r4 = s.isSameTree(null, null);
                 if (r4) passed++; else System.out.println("FAIL: both null -> " + r4);

        total++; boolean r5 = s.isSameTree(null, build(1));
                 if (!r5) passed++; else System.out.println("FAIL: one null -> " + r5);

        total++; boolean r6 = s.isSameTree(build(1), build(1));
                 if (r6) passed++; else System.out.println("FAIL: single equal -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static TreeNode build(Integer... vals) {
        if (vals == null || vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Deque<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < vals.length) {
            TreeNode node = q.poll();
            if (i < vals.length) {
                if (vals[i] != null) {
                    node.left = new TreeNode(vals[i]);
                    q.offer(node.left);
                }
                i++;
            }
            if (i < vals.length) {
                if (vals[i] != null) {
                    node.right = new TreeNode(vals[i]);
                    q.offer(node.right);
                }
                i++;
            }
        }
        return root;
    }
}
