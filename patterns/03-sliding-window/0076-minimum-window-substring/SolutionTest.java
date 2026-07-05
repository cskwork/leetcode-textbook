public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; String r = s.minWindow("ADOBECODEBANC", "ABC");
                 if (r.equals("BANC")) passed++; else System.out.println("FAIL: example 1 s=\"ADOBECODEBANC\" t=\"ABC\" -> \"" + r + "\"");

        total++; r = s.minWindow("a", "a");
                 if (r.equals("a")) passed++; else System.out.println("FAIL: example 2 s=\"a\" t=\"a\" -> \"" + r + "\"");

        total++; r = s.minWindow("a", "aa");
                 if (r.equals("")) passed++; else System.out.println("FAIL: example 3 s=\"a\" t=\"aa\" -> \"" + r + "\"");

        total++; r = s.minWindow("", "a");
                 if (r.equals("")) passed++; else System.out.println("FAIL: empty s -> \"" + r + "\"");

        total++; r = s.minWindow("aa", "aa");
                 if (r.equals("aa")) passed++; else System.out.println("FAIL: s=\"aa\" t=\"aa\" -> \"" + r + "\"");

        total++; r = s.minWindow("a", "b");
                 if (r.equals("")) passed++; else System.out.println("FAIL: s=\"a\" t=\"b\" -> \"" + r + "\"");

        total++; r = s.minWindow("cabwefgewcwaefgcf", "cae");
                 if (r.equals("cwae")) passed++; else System.out.println("FAIL: s=\"cabwefgewcwaefgcf\" t=\"cae\" -> \"" + r + "\"");

        total++; r = s.minWindow("ab", "b");
                 if (r.equals("b")) passed++; else System.out.println("FAIL: s=\"ab\" t=\"b\" -> \"" + r + "\"");

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
