import java.util.TreeMap;

class Solution {
    public boolean isNStraightHand(int[] hand, int groupSize) {
        if (hand.length % groupSize != 0) {
            return false;
        }
        TreeMap<Integer, Integer> counts = new TreeMap<>();
        for (int card : hand) {
            counts.merge(card, 1, Integer::sum);
        }
        while (!counts.isEmpty()) {
            int first = counts.firstKey();
            // The smallest remaining card must start a group; consume its run.
            for (int offset = 0; offset < groupSize; offset++) {
                int need = first + offset;
                Integer have = counts.get(need);
                if (have == null || have == 0) {
                    return false;
                }
                if (have == 1) {
                    counts.remove(need);
                } else {
                    counts.put(need, have - 1);
                }
            }
        }
        return true;
    }
}
