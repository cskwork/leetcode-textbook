import java.util.ArrayDeque;
import java.util.Deque;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.isBalanced(build(3, 9, 20, null, null, 15, 7));
                 if (r1) passed++; else System.out.println("FAIL: balanced example -> " + r1);

        total++; boolean r2 = s.isBalanced(build(1, 2, 2, 3, 3, null, null, 4, 4));
                 if (!r2) passed++; else System.out.println("FAIL: unbalanced example -> " + r2);

        total++; boolean r3 = s.isBalanced(null);
                 if (r3) passed++; else System.out.println("FAIL: empty -> " + r3);

        total++; boolean r4 = s.isBalanced(build(1));
                 if (r4) passed++; else System.out.println("FAIL: single -> " + r4);

        total++; boolean r5 = s.isBalanced(build(1, 2, null, 3, null, 4, null, 5));
                 if (!r5) passed++; else System.out.println("FAIL: left-skewed depth 4 -> " + r5);

        total++; boolean r6 = s.isBalanced(build(1, 2, 3, 4, null, null, null, 5));
                 if (!r6) passed++; else System.out.println("FAIL: left-heavy unbalanced -> " + r6);

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
