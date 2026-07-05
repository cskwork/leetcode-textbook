import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // Case 1: LeetCode example 1.
        {
            char[][] board = {
                {'o', 'a', 'a', 'n'},
                {'e', 't', 'a', 'e'},
                {'i', 'h', 'k', 'r'},
                {'i', 'f', 'l', 'v'}
            };
            String[] words = {"oath", "pea", "eat", "rain"};
            total++; List<String> r = s.findWords(board, words);
                     if (sameSet(r, new String[]{"eat", "oath"})) passed++;
                     else System.out.println("FAIL: example1 -> " + r);
        }

        // Case 2: LeetCode example 2 -- word needs to reuse a cell.
        {
            char[][] board = {
                {'a', 'b'},
                {'c', 'd'}
            };
            String[] words = {"abcb"};
            total++; List<String> r = s.findWords(board, words);
                     if (r.isEmpty()) passed++;
                     else System.out.println("FAIL: example2 reuse cell -> " + r);
        }

        // Case 3: no words match.
        {
            char[][] board = {
                {'a', 'b'},
                {'c', 'd'}
            };
            String[] words = {"xyz", "zzz"};
            total++; List<String> r = s.findWords(board, words);
                     if (r.isEmpty()) passed++;
                     else System.out.println("FAIL: no match -> " + r);
        }

        // Case 4: single-cell board with a single-character word.
        {
            char[][] board = {{'a'}};
            String[] words = {"a", "b"};
            total++; List<String> r = s.findWords(board, words);
                     if (sameSet(r, new String[]{"a"})) passed++;
                     else System.out.println("FAIL: single cell -> " + r);
        }

        // Case 5: duplicate words in dictionary -- reported only once.
        {
            char[][] board = {
                {'a', 'b'},
                {'c', 'd'}
            };
            String[] words = {"abdc", "abdc", "acdb"};
            total++; List<String> r = s.findWords(board, words);
                     if (sameSet(r, new String[]{"abdc", "acdb"})) passed++;
                     else System.out.println("FAIL: duplicate words -> " + r);
        }

        // Case 6: every word long, board full of same letter -- DFS pruned by trie depth.
        {
            char[][] board = {
                {'a', 'a'},
                {'a', 'a'}
            };
            String[] words = {"aaaaa", "aa", "aaa"};
            total++; List<String> r = s.findWords(board, words);
                     if (sameSet(r, new String[]{"aa", "aaa"})) passed++;
                     else System.out.println("FAIL: same-letter board -> " + r);
        }

        // Case 7: empty word list.
        {
            char[][] board = {{'a', 'b'}, {'c', 'd'}};
            String[] words = {};
            total++; List<String> r = s.findWords(board, words);
                     if (r.isEmpty()) passed++;
                     else System.out.println("FAIL: empty words -> " + r);
        }

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static boolean sameSet(List<String> actual, String[] expected) {
        if (actual.size() != expected.length) return false;
        List<String> a = new java.util.ArrayList<>(actual);
        List<String> e = new java.util.ArrayList<>(Arrays.asList(expected));
        Collections.sort(a);
        Collections.sort(e);
        return a.equals(e);
    }
}
