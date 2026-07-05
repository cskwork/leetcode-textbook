import java.util.ArrayDeque;
import java.util.Deque;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.isValidBST(build(2, 1, 3));
                 if (r1) passed++; else System.out.println("FAIL: valid [2,1,3] -> " + r1);

        total++; boolean r2 = s.isValidBST(build(5, 1, 4, null, null, 3, 6));
                 if (!r2) passed++; else System.out.println("FAIL: invalid [5,1,4,null,null,3,6] -> " + r2);

        total++; boolean r3 = s.isValidBST(null);
                 if (r3) passed++; else System.out.println("FAIL: empty -> " + r3);

        total++; boolean r4 = s.isValidBST(build(1));
                 if (r4) passed++; else System.out.println("FAIL: single -> " + r4);

        // Equal values are NOT allowed in a strict BST.
        total++; boolean r5 = s.isValidBST(build(1, 1));
                 if (!r5) passed++; else System.out.println("FAIL: duplicate -> " + r5);

        // Integer.MIN_VALUE as a root is valid: must NOT be rejected by an int-bound check.
        total++; boolean r6 = s.isValidBST(build(Integer.MIN_VALUE));
                 if (r6) passed++; else System.out.println("FAIL: MIN_VALUE root -> " + r6);

        // MIN_VALUE root with a larger right child: still a valid BST.
        total++; boolean r7 = s.isValidBST(build(Integer.MIN_VALUE, null, 0));
                 if (r7) passed++; else System.out.println("FAIL: MIN_VALUE, null, 0 -> " + r7);

        // MAX_VALUE root with a smaller left child: valid BST.
        total++; boolean r8 = s.isValidBST(build(Integer.MAX_VALUE, 0, null));
                 if (r8) passed++; else System.out.println("FAIL: MAX_VALUE, 0 -> " + r8);

        // Right subtree's left child is smaller than the root: invalid even though it
        // satisfies the immediate parent. Classic "bounds needed" trap.
        total++; boolean r9 = s.isValidBST(build(10, 5, 15, null, null, 6, 20));
                 if (!r9) passed++; else System.out.println("FAIL: 6 below 10 (invalid) -> " + r9);

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
