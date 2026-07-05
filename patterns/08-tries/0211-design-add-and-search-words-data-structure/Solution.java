class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
}

class WordDictionary {
    private TrieNode root;

    public WordDictionary() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) {
                node.children[i] = new TrieNode();
            }
            node = node.children[i];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return match(root, word, 0);
    }

    private boolean match(TrieNode node, String word, int pos) {
        if (pos == word.length()) {
            return node.isEnd;
        }
        char c = word.charAt(pos);
        if (c == '.') {
            for (TrieNode child : node.children) {
                if (child != null && match(child, word, pos + 1)) {
                    return true;
                }
            }
            return false;
        }
        TrieNode child = node.children[c - 'a'];
        return child != null && match(child, word, pos + 1);
    }
}
