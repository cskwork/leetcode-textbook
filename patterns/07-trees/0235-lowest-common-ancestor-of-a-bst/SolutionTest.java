import java.util.ArrayDeque;
import java.util.Deque;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        //        6
        //       / \
        //      2   8
        //     /\   /\
        //    0  4 7  9
        //       /\
        //      3  5
        TreeNode root = build(6, 2, 8, 0, 4, 7, 9, null, null, 3, 5);
        TreeNode n2 = find(root, 2);
        TreeNode n8 = find(root, 8);
        TreeNode n0 = find(root, 0);
        TreeNode n4 = find(root, 4);
        TreeNode n3 = find(root, 3);
        TreeNode n5 = find(root, 5);
        TreeNode n7 = find(root, 7);
        TreeNode n9 = find(root, 9);

        total++; TreeNode r1 = s.lowestCommonAncestor(root, n2, n8);
                 if (r1 == n6(root)) passed++; else System.out.println("FAIL: 2,8 -> " + val(r1));

        total++; TreeNode r2 = s.lowestCommonAncestor(root, n2, n4);
                 if (r2 == n2) passed++; else System.out.println("FAIL: 2,4 (one is ancestor) -> " + val(r2));

        total++; TreeNode r3 = s.lowestCommonAncestor(root, n3, n5);
                 if (r3 == n4) passed++; else System.out.println("FAIL: 3,5 -> " + val(r3));

        total++; TreeNode r4 = s.lowestCommonAncestor(root, n0, n9);
                 if (r4 == n6(root)) passed++; else System.out.println("FAIL: 0,9 extremes -> " + val(r4));

        total++; TreeNode r5 = s.lowestCommonAncestor(root, n7, n9);
                 if (r5 == n8) passed++; else System.out.println("FAIL: 7,9 -> " + val(r5));

        // p == q: a node is its own ancestor.
        total++; TreeNode r6 = s.lowestCommonAncestor(root, n4, n4);
                 if (r6 == n4) passed++; else System.out.println("FAIL: same node -> " + val(r6));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static TreeNode n6(TreeNode root) { return root; }

    private static int val(TreeNode n) { return n == null ? -1 : n.val; }

    private static TreeNode find(TreeNode root, int target) {
        while (root != null) {
            if (target == root.val) return root;
            root = target < root.val ? root.left : root.right;
        }
        return null;
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
