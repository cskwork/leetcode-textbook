import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; TreeNode r1 = s.invertTree(build(4, 2, 7, 1, 3, 6, 9));
                 if (render(r1).equals("[4,7,2,9,6,3,1]")) passed++;
                 else System.out.println("FAIL: example [4,2,7,1,3,6,9] -> " + render(r1));

        total++; TreeNode r2 = s.invertTree(build(2, 1, 3));
                 if (render(r2).equals("[2,3,1]")) passed++;
                 else System.out.println("FAIL: small [2,1,3] -> " + render(r2));

        total++; TreeNode r3 = s.invertTree(null);
                 if (r3 == null) passed++; else System.out.println("FAIL: null root -> non-null");

        total++; TreeNode r4 = s.invertTree(build(1));
                 if (render(r4).equals("[1]")) passed++;
                 else System.out.println("FAIL: single node [1] -> " + render(r4));

        total++; TreeNode r5 = s.invertTree(build(1, 2, null, 3, null, 4, null));
                 if (render(r5).equals("[1,null,2,null,3,null,4]")) passed++;
                 else System.out.println("FAIL: left-skewed -> " + render(r5));

        total++; TreeNode r6 = s.invertTree(build(1, null, 2, null, 3));
                 if (render(r6).equals("[1,2,null,3]")) passed++;
                 else System.out.println("FAIL: right-skewed -> " + render(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    // Build a tree from a level-order array, using null for missing children.
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

    // Render a tree to its LeetCode-style level-order string. LinkedList permits null placeholders.
    private static String render(TreeNode root) {
        if (root == null) return "[]";
        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);
        StringBuilder sb = new StringBuilder("[");
        while (!q.isEmpty()) {
            TreeNode node = q.poll();
            if (node == null) {
                sb.append("null,");
            } else {
                sb.append(node.val).append(",");
                q.offer(node.left);
                q.offer(node.right);
            }
        }
        String out = sb.toString().replaceAll("(null,)+$", "").replaceAll(",$", "");
        return out + "]";
    }
}
