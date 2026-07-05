import java.util.ArrayDeque;
import java.util.Deque;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.maxDepth(build(3, 9, 20, null, null, 15, 7));
                 if (r1 == 3) passed++; else System.out.println("FAIL: example -> " + r1);

        total++; int r2 = s.maxDepth(build(1, null, 2));
                 if (r2 == 2) passed++; else System.out.println("FAIL: [1,null,2] -> " + r2);

        total++; int r3 = s.maxDepth(null);
                 if (r3 == 0) passed++; else System.out.println("FAIL: null -> " + r3);

        total++; int r4 = s.maxDepth(build(42));
                 if (r4 == 1) passed++; else System.out.println("FAIL: single -> " + r4);

        total++; int r5 = s.maxDepth(build(1, 2, 3, 4, 5, 6, 7, 8));
                 if (r5 == 4) passed++; else System.out.println("FAIL: left-skewed depth 4 -> " + r5);

        total++; int r6 = s.maxDepth(build(1, 2, null, 3, null, 4, null, 5));
                 if (r6 == 5) passed++; else System.out.println("FAIL: deep left chain -> " + r6);

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
