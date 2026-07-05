public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; boolean r1 = s.isAnagram("anagram", "nagaram");
                  if (r1) passed++; else System.out.println("FAIL: example anagram -> " + r1);

        total++; boolean r2 = s.isAnagram("rat", "car");
                  if (!r2) passed++; else System.out.println("FAIL: example rat/car -> " + r2);

        total++; boolean r3 = s.isAnagram("", "");
                  if (r3) passed++; else System.out.println("FAIL: both empty -> " + r3);

        total++; boolean r4 = s.isAnagram("a", "a");
                  if (r4) passed++; else System.out.println("FAIL: single char match -> " + r4);

        total++; boolean r5 = s.isAnagram("ab", "a");
                  if (!r5) passed++; else System.out.println("FAIL: length mismatch -> " + r5);

        total++; boolean r6 = s.isAnagram("aabbcc", "cbacba");
                  if (r6) passed++; else System.out.println("FAIL: rearranged -> " + r6);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
