class Solution {
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int best = 0;
        while (left < right) {
            int h = Math.min(height[left], height[right]);
            int width = right - left;
            int area = h * width;
            if (area > best) {
                best = area;
            }
            // Exchange argument: any container that keeps the shorter line
            // and shrinks width is strictly worse, so abandon the shorter line.
            if (height[left] <= height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }
}
