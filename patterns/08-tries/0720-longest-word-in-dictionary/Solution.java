class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
    String word;
}

class Solution {
    public String longestWord(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                int i = c - 'a';
                if (node.children[i] == null) {
                    node.children[i] = new TrieNode();
                }
                node = node.children[i];
            }
            node.isEnd = true;
            node.word = w;
        }

        String[] best = {""};
        dfs(root, best);
        return best[0];
    }

    private void dfs(TrieNode node, String[] best) {
        if (node.word != null) {
            if (node.word.length() > best[0].length()
                    || (node.word.length() == best[0].length()
                        && node.word.compareTo(best[0]) < 0)) {
                best[0] = node.word;
            }
        }
        for (int i = 0; i < 26; i++) {
            TrieNode child = node.children[i];
            if (child != null && child.isEnd) {
                dfs(child, best);
            }
        }
    }
}
