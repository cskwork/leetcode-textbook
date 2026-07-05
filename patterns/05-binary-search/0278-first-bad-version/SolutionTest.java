public class SolutionTest {
    public static void main(String[] args) {
        int passed = 0, total = 0;

        total++; int r1 = new TestSolution(4).firstBadVersion(5);
                 if (r1 == 4) passed++; else System.out.println("FAIL: n=5, bad=4 -> " + r1);

        total++; int r2 = new TestSolution(1).firstBadVersion(1);
                 if (r2 == 1) passed++; else System.out.println("FAIL: n=1, bad=1 -> " + r2);

        total++; int r3 = new TestSolution(1).firstBadVersion(3);
                 if (r3 == 1) passed++; else System.out.println("FAIL: n=3, bad=1 (all bad) -> " + r3);

        total++; int r4 = new TestSolution(6).firstBadVersion(6);
                 if (r4 == 6) passed++; else System.out.println("FAIL: n=6, bad=6 (only last bad) -> " + r4);

        total++; int r5 = new TestSolution(2).firstBadVersion(10);
                 if (r5 == 2) passed++; else System.out.println("FAIL: n=10, bad=2 -> " + r5);

        total++; int r6 = new TestSolution(1702766719).firstBadVersion(2126753390);
                 if (r6 == 1702766719) passed++;
                 else System.out.println("FAIL: large n (overflow check) -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    // Models the LeetCode API: versions >= bad are bad, others are good.
    static class TestSolution extends Solution {
        final int bad;
        TestSolution(int bad) { this.bad = bad; }
        @Override protected boolean isBadVersion(int version) {
            return version >= bad;
        }
    }
}
