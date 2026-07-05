public class SolutionTest {
    public static void main(String[] args) {
        int passed = 0, total = 0;

        // Case 1: LeetCode example.
        {
            WordDictionary wd = new WordDictionary();
            wd.addWord("bad");
            wd.addWord("dad");
            wd.addWord("mad");

            total++; boolean r = wd.search("pad");
                     if (!r) passed++; else System.out.println("FAIL: pad -> " + r);
            total++; boolean r2 = wd.search("bad");
                     if (r2) passed++; else System.out.println("FAIL: bad -> " + r2);
            total++; boolean r3 = wd.search(".ad");
                     if (r3) passed++; else System.out.println("FAIL: .ad -> " + r3);
            total++; boolean r4 = wd.search("b..");
                     if (r4) passed++; else System.out.println("FAIL: b.. -> " + r4);
        }

        // Case 2: all-dot query.
        {
            WordDictionary wd = new WordDictionary();
            wd.addWord("a");
            wd.addWord("b");
            total++; boolean r = wd.search(".");
                     if (r) passed++; else System.out.println("FAIL: . -> " + r);
            total++; boolean r2 = wd.search("..");
                     if (!r2) passed++; else System.out.println("FAIL: .. -> " + r2);
        }

        // Case 3: wildcard in the middle, prefix mismatch.
        {
            WordDictionary wd = new WordDictionary();
            wd.addWord("at");
            wd.addWord("and");
            wd.addWord("an");
            wd.addWord("add");
            total++; boolean r = wd.search("a");
                     if (!r) passed++; else System.out.println("FAIL: a (prefix not word) -> " + r);
            total++; boolean r2 = wd.search(".t");
                     if (r2) passed++; else System.out.println("FAIL: .t -> " + r2);
            total++; boolean r3 = wd.search("a.d.");
                     if (!r3) passed++; else System.out.println("FAIL: a.d. -> " + r3);
            total++; boolean r4 = wd.search("an.");
                     if (r4) passed++; else System.out.println("FAIL: an. -> " + r4);
            total++; boolean r5 = wd.search(".n.");
                     if (r5) passed++; else System.out.println("FAIL: .n. -> " + r5);
        }

        // Case 4: single character exact and wildcard.
        {
            WordDictionary wd = new WordDictionary();
            wd.addWord("a");
            wd.addWord("ab");
            total++; boolean exact = wd.search("a");
                     if (exact) passed++; else System.out.println("FAIL: exact a -> " + exact);
            total++; boolean wild = wd.search(".");
                     if (wild) passed++; else System.out.println("FAIL: . single -> " + wild);
            total++; boolean wildTwo = wd.search("..");
                     if (wildTwo) passed++; else System.out.println("FAIL: .. -> " + wildTwo);
        }

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
