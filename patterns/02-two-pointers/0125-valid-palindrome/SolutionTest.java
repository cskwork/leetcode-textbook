public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++;
        if (s.isPalindrome("A man, a plan, a canal: Panama")) passed++;
        else System.out.println("FAIL: LC example 1 -> " + s.isPalindrome("A man, a plan, a canal: Panama"));

        total++;
        if (!s.isPalindrome("race a car")) passed++;
        else System.out.println("FAIL: LC example 2 -> " + s.isPalindrome("race a car"));

        total++;
        if (s.isPalindrome(" ")) passed++;
        else System.out.println("FAIL: LC example 3 (single space) -> " + s.isPalindrome(" "));

        total++;
        if (s.isPalindrome("")) passed++;
        else System.out.println("FAIL: empty string -> " + s.isPalindrome(""));

        total++;
        if (!s.isPalindrome("0P")) passed++;
        else System.out.println("FAIL: 0P -> " + s.isPalindrome("0P"));

        total++;
        if (s.isPalindrome("Was it a car or a cat I saw?")) passed++;
        else System.out.println("FAIL: sentence palindrome -> " + s.isPalindrome("Was it a car or a cat I saw?"));

        total++;
        if (s.isPalindrome(".,")) passed++;
        else System.out.println("FAIL: only punctuation -> " + s.isPalindrome(".,"));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
