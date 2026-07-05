# 0355 - Design Twitter

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/design-twitter/

## Problem

Design a simplified Twitter with four operations:

- `postTweet(userId, tweetId)` -- compose a new tweet.
- `getNewsFeed(userId)` -- return the **10 most recent** tweet IDs in the user's news feed. Each
  item is a tweet posted by the user themself **or** by someone they follow. Order most-recent first.
- `follow(followerId, followeeId)` -- the follower starts following the followee.
- `unfollow(followerId, followeeId)` -- the follower stops following the followee.

Signatures:

    class Twitter:
        constructor()
        void postTweet(int userId, int tweetId)
        List<Integer> getNewsFeed(int userId)
        void follow(int followerId, int followeeId)
        void unfollow(int followerId, int followeeId)

Example:

    Twitter twitter = new Twitter();
    twitter.postTweet(1, 5);        // user 1 posts tweet 5
    twitter.getNewsFeed(1);         // -> [5]
    twitter.follow(1, 2);           // user 1 follows user 2
    twitter.postTweet(2, 6);        // user 2 posts tweet 6
    twitter.getNewsFeed(1);         // -> [6, 5]      (6 is newer than 5)
    twitter.unfollow(1, 2);         // user 1 unfollows user 2
    twitter.getNewsFeed(1);         // -> [5]

## Intuition

Two facts to model. (1) Each user owns an ordered list of tweets -- append on `postTweet` with a
monotonically increasing timestamp, so each list is already sorted oldest-to-newest. (2) A user's
news feed is the **merge of K sorted lists**: their own list plus each followee's list, taking the 10
newest overall.

That merge is exactly the **K-way merge** template from the pattern intro. Seed a max-heap (most
recent first) with the newest tweet from each followee; repeatedly pop the newest, output it, and
push that same user's next-newer tweet as the new candidate. Each pop/push is O(log K) where K is the
number of followees, so a feed fetch is O(10 * log K) = O(log K).

A user always sees their own tweets, so we treat "follow yourself" as an invariant: `postTweet`
makes the user follow themself, and `getNewsFeed` guarantees self is in the followee set.

## Pseudocode

    constructor():
        clock = 0                              # global, increments on every post
        tweets   = map: userId -> list of (tweetId, time) appended in post order
        followees = map: userId -> set of ids they follow (always includes themselves)

    function postTweet(userId, tweetId):
        add (tweetId, clock) to tweets[userId]; clock += 1
        add userId to followees[userId]        # a user always sees their own tweets

    function follow(followerId, followeeId):
        add followeeId to followees[followerId]

    function unfollow(followerId, followeeId):
        if followerId != followeeId:           # never unfollow self
            remove followeeId from followees[followerId]

    function getNewsFeed(userId):
        f = followees of userId (including userId itself)
        create an empty max-heap ordered by timestamp descending
        for each followee ff in f:
            if ff has at least one tweet:
                let i = index of ff's newest tweet (last in its list)
                push (timestamp of tweet i, ff, i) into the heap
        feed = empty list
        while feed has fewer than 10 items and heap is not empty:
            pop (ts, ff, i) from the heap
            append tweetId of ff's tweet i to feed
            if i > 0:                          # older tweet from the same user exists
                push (timestamp of ff's tweet i-1, ff, i-1) into the heap
        return feed

## Java Solution

```java
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
```

Each tweet is stored as `int[]{tweetId, time}` appended to that user's list, so a list is naturally
oldest-to-newest and the **last index is the newest** -- which is what we seed the heap with. The
heap entry is `{time, followeeId, index}` and its comparator compares only `time` descending
(`Integer.compare(b[0], a[0])`), making it a **max-heap on recency**: the root is always the single
newest tweet among the candidates. Popping it outputs that tweet; then we immediately re-seed from
the *same* user's list by decrementing the index, so the heap always holds one candidate per
followee. The self-follow invariant is established by `postTweet` (which calls `follow(userId,
userId)`) and reinforced defensively in `getNewsFeed`, so a user's own tweets are never missing.
`unfollow` refuses to remove self so the invariant cannot be broken by the caller.

## Complexity

Let K = number of followees (including self), and let the feed size cap be the constant 10.

    Time:  postTweet  O(1)              -- append + set add
           follow     O(1)
           unfollow   O(1)
           getNewsFeed O(10 * log K) ~= O(log K)  -- up to 10 pops, each O(log K) into the heap
    Space: O(P + F)  -- P total tweets stored across all users, F total follow edges

## Dry-Run

Run the LeetCode example. The clock ticks `t0, t1, ...` per post.

| # | Operation             | State after                                                   | feed result |
|--:|-----------------------|---------------------------------------------------------------|-------------|
| 1 | `postTweet(1, 5)`     | tweets[1]=[(5,t0)]; followees[1]={1}                          | -           |
| 2 | `getNewsFeed(1)`      | followees={1}; seed heap with (t0,1,idx0); pop -> tweet 5     | **[5]**     |
| 3 | `follow(1, 2)`        | followees[1]={1,2}                                            | -           |
| 4 | `postTweet(2, 6)`     | tweets[2]=[(6,t1)]; followees[2]={2}                          | -           |
| 5 | `getNewsFeed(1)`      | seed (t0,1,0) and (t1,2,0); pop t1 -> 6, then pop t0 -> 5     | **[6,5]**   |
| 6 | `unfollow(1, 2)`      | followees[1]={1}  (self untouched)                            | -           |
| 7 | `getNewsFeed(1)`      | followees={1}; seed (t0,1,0); pop -> tweet 5                  | **[5]**     |

Step 5 in detail (the K-way merge): two followees (1 and 2). Seed the heap with each one's newest:
`(t1, user2, idx0)` and `(t0, user1, idx0)`. Max-heap root is `t1` -> output tweet 6; user2 has no
older tweet so nothing is re-pushed. Next root is `t0` -> output tweet 5; feed reaches size 2,
loop ends. Output `[6, 5]`, newest first.

## Common mistakes

- Forgetting self-follow. A user who posts but never appears in their own followee set sees an empty
  feed. Either add self on every `postTweet` (as here) or always inject `userId` into the candidate
  set inside `getNewsFeed`.
- Allowing `unfollow(x, x)`. If self can be removed, a user suddenly loses their own tweets. Guard
  with the `followerId == followeeId` check.
- Using a **min-heap** on timestamp. The root would be the *oldest* tweet, so the feed would come
  out oldest-first and the 10 returned would be the wrong ten. The merge needs a **max-heap** on
  recency.
- Re-heapifying the whole feed on every call by dumping all followee tweets into a list and sorting.
  Correct but O(P log P) per call; the K-way merge is O(10 log K) and the whole reason a heap is the
  right tool here.
- Losing the "next candidate from this same user" step after a pop. If you pop a tweet but never
  push that user's previous tweet, the merge silently drops older tweets and the feed is incomplete.
- Comparing tweets by ID instead of timestamp. Tweet IDs are not guaranteed monotonic with recency;
  only the posting order (clock) is.

## Related problems

- [0973 - K Closest Points to Origin](../0973-k-closest-points-to-origin/) - the other max-heap in
  this section, also evicting the "worst" survivor, but a size-k cap rather than a K-way merge.
- [0215 - Kth Largest Element in an Array](../0215-kth-largest-element-in-an-array/) - the simpler
  size-k min-heap that this design builds on.
- [0703 - Kth Largest Element in a Stream](../0703-kth-largest-element-in-a-stream/) - a streaming
  design class like `Twitter`, using a size-k min-heap instead of a K-way merge.
