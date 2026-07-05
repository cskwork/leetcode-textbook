import java.util.*;

class Twitter {
    private int clock;
    private final Map<Integer, List<int[]>> tweets;     // userId -> list of [tweetId, time]
    private final Map<Integer, Set<Integer>> followees; // userId -> followee ids (incl. self)

    public Twitter() {
        this.clock = 0;
        this.tweets = new HashMap<>();
        this.followees = new HashMap<>();
    }

    public void postTweet(int userId, int tweetId) {
        tweets.computeIfAbsent(userId, k -> new ArrayList<>())
              .add(new int[]{tweetId, clock++});
        follow(userId, userId);                          // a user always sees their own tweets
    }

    public List<Integer> getNewsFeed(int userId) {
        List<Integer> feed = new ArrayList<>();
        Set<Integer> f = followees.computeIfAbsent(userId, k -> new HashSet<>());
        f.add(userId);                                   // defensive: include self even if never posted

        // max-heap by timestamp; entry = [time, userId, indexIntoThatUsersTweetList]
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(b[0], a[0]));
        for (int ff : f) {
            List<int[]> list = tweets.get(ff);
            if (list != null && !list.isEmpty()) {
                int last = list.size() - 1;
                heap.offer(new int[]{list.get(last)[1], ff, last});
            }
        }

        while (feed.size() < 10 && !heap.isEmpty()) {
            int[] top = heap.poll();
            int ff = top[1];
            int idx = top[2];
            feed.add(tweets.get(ff).get(idx)[0]);
            if (idx > 0) {
                int[] prev = tweets.get(ff).get(idx - 1);
                heap.offer(new int[]{prev[1], ff, idx - 1});
            }
        }
        return feed;
    }

    public void follow(int followerId, int followeeId) {
        followees.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
    }

    public void unfollow(int followerId, int followeeId) {
        if (followerId == followeeId) return;            // cannot unfollow self
        Set<Integer> f = followees.get(followerId);
        if (f != null) f.remove(followeeId);
    }
}
