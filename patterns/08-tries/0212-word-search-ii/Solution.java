import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    String word;
}

class Solution {
    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                node = node.children.computeIfAbsent(c, k -> new TrieNode());
            }
            node.word = w;
        }

        List<String> found = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                dfs(board, r, c, root, found);
            }
        }
        return found;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node, List<String> found) {
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) {
            return;
        }
        char letter = board[r][c];
        if (letter == '#' || !node.children.containsKey(letter)) {
            return;
        }

        TrieNode next = node.children.get(letter);
        if (next.word != null) {
            found.add(next.word);
            next.word = null;
        }

        board[r][c] = '#';
        dfs(board, r - 1, c, next, found);
        dfs(board, r + 1, c, next, found);
        dfs(board, r, c - 1, next, found);
        dfs(board, r, c + 1, next, found);
        board[r][c] = letter;
    }
}
