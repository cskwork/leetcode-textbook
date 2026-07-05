public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.isValid("()");
                 if (r1) passed++; else System.out.println("FAIL: \"()\" -> " + r1);

        total++; boolean r2 = s.isValid("()[]{}");
                 if (r2) passed++; else System.out.println("FAIL: \"()[]{}\" -> " + r2);

        total++; boolean r3 = s.isValid("(]");
                 if (!r3) passed++; else System.out.println("FAIL: \"(]\" -> " + r3);

        total++; boolean r4 = s.isValid("([)]");
                 if (!r4) passed++; else System.out.println("FAIL: \"([)]\" -> " + r4);

        total++; boolean r5 = s.isValid("{[]}");
                 if (r5) passed++; else System.out.println("FAIL: \"{[]}\" -> " + r5);

        total++; boolean r6 = s.isValid("");
                 if (r6) passed++; else System.out.println("FAIL: \"\" (empty) -> " + r6);

        total++; boolean r7 = s.isValid("(");
                 if (!r7) passed++; else System.out.println("FAIL: \"(\" (unclosed) -> " + r7);

        total++; boolean r8 = s.isValid(")");
                 if (!r8) passed++; else System.out.println("FAIL: \")\" (extra closer) -> " + r8);

        total++; boolean r9 = s.isValid("(((((((((())))))))))");
                 if (r9) passed++; else System.out.println("FAIL: deeply nested -> " + r9);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
