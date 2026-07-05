import java.util.ArrayDeque;
import java.util.Deque;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.diameterOfBinaryTree(build(1, 2, 3, 4, 5));
                 if (r1 == 3) passed++; else System.out.println("FAIL: example -> " + r1);

        total++; int r2 = s.diameterOfBinaryTree(build(1, 2));
                 if (r2 == 1) passed++; else System.out.println("FAIL: [1,2] -> " + r2);

        total++; int r3 = s.diameterOfBinaryTree(null);
                 if (r3 == 0) passed++; else System.out.println("FAIL: null -> " + r3);

        total++; int r4 = s.diameterOfBinaryTree(build(5));
                 if (r4 == 0) passed++; else System.out.println("FAIL: single -> " + r4);

        total++; int r5 = s.diameterOfBinaryTree(build(1, 2, null, 3, null, 4, null, 5));
                 if (r5 == 4) passed++; else System.out.println("FAIL: left chain depth 4 -> " + r5);

        total++; int r6 = s.diameterOfBinaryTree(build(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
                 if (r6 == 6) passed++; else System.out.println("FAIL: full-ish tree -> " + r6);

        // Ensure the global field does not leak between two calls on the same instance.
        total++; int r7 = s.diameterOfBinaryTree(build(1, 2, 3, 4, 5, 6, 7));
                 int r8 = s.diameterOfBinaryTree(build(1, 2));
                 if (r7 == 4 && r8 == 1) passed++;
                 else System.out.println("FAIL: state leak r7=" + r7 + " r8=" + r8);

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
