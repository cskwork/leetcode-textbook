import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; String r1 = render(s.levelOrder(build(3, 9, 20, null, null, 15, 7)));
                 if (r1.equals("[[3],[9,20],[15,7]]")) passed++;
                 else System.out.println("FAIL: example -> " + r1);

        total++; String r2 = render(s.levelOrder(build(1)));
                 if (r2.equals("[[1]]")) passed++;
                 else System.out.println("FAIL: single -> " + r2);

        total++; String r3 = render(s.levelOrder(null));
                 if (r3.equals("[]")) passed++;
                 else System.out.println("FAIL: null -> " + r3);

        total++; String r4 = render(s.levelOrder(build(1, 2, 3, 4, 5, 6, 7)));
                 if (r4.equals("[[1],[2,3],[4,5,6,7]]")) passed++;
                 else System.out.println("FAIL: perfect tree -> " + r4);

        total++; String r5 = render(s.levelOrder(build(1, null, 2, null, 3)));
                 if (r5.equals("[[1],[2],[3]]")) passed++;
                 else System.out.println("FAIL: right chain -> " + r5);

        total++; String r6 = render(s.levelOrder(build(1, 2, 3, 4, null, null, 7, 8, null)));
                 if (r6.equals("[[1],[2,3],[4,7],[8]]")) passed++;
                 else System.out.println("FAIL: irregular -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static String render(List<List<Integer>> levels) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < levels.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(levels.get(i).toString());
        }
        return sb.append("]").toString().replace(" ", "");
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
