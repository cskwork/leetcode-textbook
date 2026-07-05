public class SolutionTest {
    public static void main(String[] args) {
        int passed = 0, total = 0;

        // Case 1: LeetCode example sequence.
        {
            Trie trie = new Trie();
            trie.insert("apple");
            total++; boolean a = trie.search("apple");
                     if (a) passed++; else System.out.println("FAIL: search apple -> " + a);
            total++; boolean b = trie.search("app");
                     if (!b) passed++; else System.out.println("FAIL: search app -> " + b);
            total++; boolean c = trie.startsWith("app");
                     if (c) passed++; else System.out.println("FAIL: startsWith app -> " + c);
            trie.insert("app");
            total++; boolean d = trie.search("app");
                     if (d) passed++; else System.out.println("FAIL: search app after insert -> " + d);
        }

        // Case 2: prefix of a word is not itself a word.
        {
            Trie trie = new Trie();
            trie.insert("bands");
            total++; boolean r = trie.search("band");
                     if (!r) passed++; else System.out.println("FAIL: search band (prefix only) -> " + r);
            total++; boolean p = trie.startsWith("band");
                     if (p) passed++; else System.out.println("FAIL: startsWith band -> " + p);
        }

        // Case 3: search for a word that diverges from an inserted one.
        {
            Trie trie = new Trie();
            trie.insert("apple");
            total++; boolean r = trie.search("apples");
                     if (!r) passed++; else System.out.println("FAIL: search apples -> " + r);
            total++; boolean p = trie.startsWith("apples");
                     if (!p) passed++; else System.out.println("FAIL: startsWith apples -> " + p);
        }

        // Case 4: empty trie -- everything is false.
        {
            Trie trie = new Trie();
            total++; boolean r = trie.search("");
                     if (!r) passed++; else System.out.println("FAIL: search empty on empty trie -> " + r);
            total++; boolean p = trie.startsWith("anything");
                     if (!p) passed++; else System.out.println("FAIL: startsWith on empty trie -> " + p);
        }

        // Case 5: single-character words and full alphabet round-trip.
        {
            Trie trie = new Trie();
            for (char c = 'a'; c <= 'z'; c++) {
                trie.insert(String.valueOf(c));
            }
            total++; boolean z = trie.search("z");
                     if (z) passed++; else System.out.println("FAIL: search z -> " + z);
            total++; boolean m = trie.startsWith("m");
                     if (m) passed++; else System.out.println("FAIL: startsWith m -> " + m);
            total++; boolean none = trie.search("ab");
                     if (!none) passed++; else System.out.println("FAIL: search ab -> " + none);
        }

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
