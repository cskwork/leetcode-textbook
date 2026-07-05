import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // null input -> null output.
        total++; Node r1 = s.cloneGraph(null);
                 if (r1 == null) passed++; else System.out.println("FAIL: null -> non-null");

        // Single node, no neighbors.
        total++; Node solo = new Node(7);
                 Node r2 = s.cloneGraph(solo);
                 if (r2 != null && r2 != solo && r2.val == 7 && r2.neighbors.isEmpty()) passed++;
                 else System.out.println("FAIL: single node");

        // adjList = [[2,4],[1,3],[2,4],[1,3]] (4-node square with diagonals).
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        n1.neighbors.add(n2); n1.neighbors.add(n4);
        n2.neighbors.add(n1); n2.neighbors.add(n3);
        n3.neighbors.add(n2); n3.neighbors.add(n4);
        n4.neighbors.add(n1); n4.neighbors.add(n3);

        total++; Node r3 = s.cloneGraph(n1);
                 if (isStructuralCopy(r3, n1) && sharesNoReferences(r3, n1)) passed++;
                 else System.out.println("FAIL: square w/ diagonals");

        // adjList = [[2],[1]] (two-node edge).
        Node a = new Node(1);
        Node b = new Node(2);
        a.neighbors.add(b);
        b.neighbors.add(a);
        total++; Node r4 = s.cloneGraph(a);
                 if (isStructuralCopy(r4, a) && sharesNoReferences(r4, a)) passed++;
                 else System.out.println("FAIL: two-node edge");

        // adjList = [[2,3,4],[1,3,4],[1,2,4],[1,2,3]] (K4 complete graph).
        Node[] k = new Node[5];
        for (int i = 1; i <= 4; i++) k[i] = new Node(i);
        for (int i = 1; i <= 4; i++)
            for (int j = 1; j <= 4; j++)
                if (i != j) k[i].neighbors.add(k[j]);
        total++; Node r5 = s.cloneGraph(k[1]);
                 if (isStructuralCopy(r5, k[1]) && sharesNoReferences(r5, k[1])) passed++;
                 else System.out.println("FAIL: K4 complete graph");

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    // Structural equality: same number of nodes, same values, same adjacency by value.
    private static boolean isStructuralCopy(Node clone, Node original) {
        Map<Node, Node> origIndex = new IdentityHashMap<>();
        collect(original, origIndex);
        Map<Node, Node> cloneIndex = new IdentityHashMap<>();
        collect(clone, cloneIndex);
        if (origIndex.size() != cloneIndex.size()) return false;
        // For each original node, the corresponding clone must have the same val and the same
        // set of neighbor vals.
        for (Node o : origIndex.values()) {
            Node c = findVal(cloneIndex, o.val);
            if (c == null) return false;
            List<Integer> oVals = sortedNeighborVals(o);
            List<Integer> cVals = sortedNeighborVals(c);
            if (!oVals.equals(cVals)) return false;
        }
        return true;
    }

    // No original node is reused in the clone (deep copy requirement).
    private static boolean sharesNoReferences(Node clone, Node original) {
        Map<Node, Node> origSet = new IdentityHashMap<>();
        collect(original, origSet);
        Map<Node, Node> cloneSet = new IdentityHashMap<>();
        collect(clone, cloneSet);
        for (Node c : cloneSet.keySet()) {
            if (origSet.containsKey(c)) return false;
        }
        return true;
    }

    private static void collect(Node start, Map<Node, Node> seen) {
        if (start == null || seen.containsKey(start)) return;
        seen.put(start, start);
        for (Node n : start.neighbors) collect(n, seen);
    }

    private static Node findVal(Map<Node, Node> nodes, int val) {
        for (Node n : nodes.keySet()) if (n.val == val) return n;
        return null;
    }

    private static List<Integer> sortedNeighborVals(Node n) {
        List<Integer> v = new ArrayList<>();
        for (Node nb : n.neighbors) v.add(nb.val);
        java.util.Collections.sort(v);
        return v;
    }
}
