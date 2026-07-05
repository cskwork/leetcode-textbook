class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private int[][] image;
    private int originalColor;
    private int newColor;

    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        int orig = image[sr][sc];
        if (orig == newColor) {
            return image;
        }
        this.image = image;
        this.originalColor = orig;
        this.newColor = newColor;
        dfs(sr, sc);
        return image;
    }

    private void dfs(int r, int c) {
        if (r < 0 || r >= image.length || c < 0 || c >= image[0].length) {
            return;
        }
        if (image[r][c] != originalColor) {
            return;
        }
        image[r][c] = newColor;
        for (int k = 0; k < 4; k++) {
            dfs(r + DR[k], c + DC[k]);
        }
    }
}
