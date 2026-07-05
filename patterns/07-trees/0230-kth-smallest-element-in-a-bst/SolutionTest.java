import java.util.ArrayDeque;
import java.util.Deque;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        //        5
        //       / \
        //      3   6
        //     / \
        //    2   4
        //   /
        //  1
        // Inorder: 1,2,3,4,5,6
        TreeNode root = build(5, 3, 6, 2, 4, null, null, 1);

        total++; int r1 = s.kthSmallest(root, 1);
                 if (r1 == 1) passed++; else System.out.println("FAIL: k=1 -> " + r1);

        total++; int r2 = s.kthSmallest(root, 3);
                 if (r2 == 3) passed++; else System.out.println("FAIL: k=3 -> " + r2);

        total++; int r3 = s.kthSmallest(root, 6);
                 if (r3 == 6) passed++; else System.out.println("FAIL: k=6 (largest) -> " + r3);

        total++; int r4 = s.kthSmallest(build(1), 1);
                 if (r4 == 1) passed++; else System.out.println("FAIL: single -> " + r4);

        // Right-skewed chain: 1 -> 2 -> 3.
        total++; int r5 = s.kthSmallest(build(1, null, 2, null, 3), 2);
                 if (r5 == 2) passed++; else System.out.println("FAIL: right chain k=2 -> " + r5);

        // Left-skewed chain: 3 -> 2 -> 1.
        total++; int r6 = s.kthSmallest(build(3, 2, null, 1), 2);
                 if (r6 == 2) passed++; else System.out.println("FAIL: left chain k=2 -> " + r6);

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
