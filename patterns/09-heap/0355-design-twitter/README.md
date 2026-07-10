# 0355 - Design Twitter

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/design-twitter/

## Concepts used

- **Hash map** -- a key-to-value lookup table with O(1) average lookup; here it maps each user to
  their list of tweets. [glossary](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary)
- **Heap** -- a structure that always gives back the smallest or largest item it holds in O(1),
  with O(log n) cost to add or remove one. [glossary](../../../docs/10-glossary.md#heap--priority-queue)
- **Max-heap** -- a heap whose top item is the *largest* of everything in it (here, the most recent
  timestamp). Java's `PriorityQueue` is a min-heap by default, so we reverse the comparator.
- **Hash set** -- a hash map with only keys, used to answer "is X in here?" in O(1); here it stores
  who a user follows. [glossary](../../../docs/10-glossary.md#hash-set)

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

Picture a newsroom editor who must build today's front page (the 10 most recent stories) from K
reporters. Each reporter has a personal stack of stories, newest on top. The editor never reads all
the stacks end-to-end -- they look at the **top story from each reporter**, pick the newest one
overall, slide it onto the front page, then peek at that reporter's *next* story (now its newest
remaining). Repeat until the front page is full. The reporter whose story was just used always gets
re-peeked, because their next-oldest story is the new candidate from that stack.

This problem is that story, with "reporters" = followees (a user always follows themself), "stories"
= tweets stored newest-last in each user's list, and "front page" = the news feed. Building the feed
is a **K-way merge of K already-sorted lists** (each followee's tweet list is sorted by post time).
The tool that makes "pick the newest of the K candidates" cheap is a
[**max-heap**](../../../docs/10-glossary.md#heap--priority-queue) -- a heap whose top is always the
**largest** item inside it, which here means the largest (most recent) timestamp. Each pick is
O(log K), and we only need 10 picks, so a feed fetch is O(10 * log K) -- far cheaper than dumping
every followee tweet into one list and sorting it.

Why a **max**-heap here, when 0215 and 0347 use a min-heap? Because the value we want to *pick and
output* is the **most recent** (largest timestamp) of the candidates on top, ready to read. In 0215
and 0347 the value on top was the one we wanted to *throw away* (the smallest), so they used a
min-heap. Same size-K heap trick, opposite direction: when the top is the answer you want to output
next, use the order that puts the answer on top -- here, max on timestamp.

The bookkeeping is two [hash maps](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary):
`tweets` maps each user to their list of `(tweetId, timestamp)` appended in post order (so newest is
last), and `followees` maps each user to the [hash set](../../../docs/10-glossary.md#hash-set) of
users they follow. A global `clock` increments on every post, so timestamps are unique and ordered
even though tweet IDs are not. One subtlety: a user always sees their own tweets, so we treat
"follow yourself" as a permanent invariant -- `postTweet` makes the user follow themself, and
`unfollow` refuses to remove self.

**Smallest trace.** Follow the LeetCode example.

1. `postTweet(1, 5)`: user 1's list = `[(5, t0)]`; user 1 follows themself.
2. `getNewsFeed(1)`: one followee (user 1) with one tweet. Seed heap with `(t0, user1, idx0)`. Pop
   -> newest timestamp t0 -> output tweet 5. Feed = `[5]`.
3. `follow(1, 2)`: user 1 now follows user 2.
4. `postTweet(2, 6)`: user 2's list = `[(6, t1)]`.
5. `getNewsFeed(1)`: two followees. Seed heap with each one's newest: `(t0, user1, idx0)` and
   `(t1, user2, idx0)`. Max-heap puts t1 on top -> output tweet 6. User 2 has no older tweet, so
   nothing to re-push. Next top is t0 -> output tweet 5. Feed reaches size 2 = full. Output
   `[6, 5]`, newest first.
6. `unfollow(1, 2)`: user 1's followees shrink back to `{1}` (self untouched).
7. `getNewsFeed(1)`: only user 1's tweet remains -> `[5]`.

The pattern at work in step 5 is the K-way merge; the same "always pick the next-best candidate off
a heap" idea, with a min-heap instead, appears in [0703 - Kth Largest Element in a
Stream](../0703-kth-largest-element-in-a-stream/) and [0215 - Kth Largest Element in an
Array](../0215-kth-largest-element-in-an-array/). This is the one place in the pattern where the
heap is not capped at a fixed size K of *survivors* -- instead it holds one candidate per followee,
and the cap is on how many we *pop*.

### Checkpoint A -- Which heap for the merge

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** In `getNewsFeed`, how is the heap ordered, and what does its root represent?
- a) Max-heap on timestamp; the root is the single newest candidate across all followees
- b) Min-heap on timestamp; the root is the oldest tweet
- c) Heap on tweetId; the root is the smallest id

<details><summary>Show answer</summary>

**(a)** -- we want the most recent tweet next, so the newest timestamp must sit on top. The root is the best candidate to output, polled up to 10 times.

</details>

**Q2 (comprehend).** Right after popping a tweet from the heap, what does the code do, and why?
- a) Pushes that same user's previous (older) tweet, so each followee always keeps one candidate in the heap
- b) Nothing -- moves on to the next followee
- c) Rebuilds the whole heap

<details><summary>Show answer</summary>

**(a)** -- each followee's list is sorted by time, so after taking a user's newest remaining tweet, its next-older tweet is that user's new best candidate. Re-pushing it keeps exactly one entry per followee in the heap -- the core of the K-way merge.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** User 1 follows user 2. Then: `postTweet(2,10)`, `postTweet(2,20)`, `postTweet(1,30)`. What does `getNewsFeed(1)` return (the cap is 10, so all three)?
- a) [30, 20, 10]
- b) [10, 20, 30]
- c) [20, 30, 10]

<details><summary>Show answer</summary>

**(a)** -- timestamps rise 10<t0, 20<t1, 30<t2. Followees of user 1 are {1,2}; the heap seeds with each newest: (t2, user1) and (t1, user2). Pop t2 -> 30; pop t1 -> 20, then re-push user2's older (t0); pop t0 -> 10. Newest first gives [30, 20, 10].

</details>

**Q2 (analyze).** What goes wrong if `unfollow(x, x)` were allowed to remove a user from their own followee set?
- a) The user's own tweets would vanish from their own feed
- b) Nothing changes
- c) It throws an exception

<details><summary>Show answer</summary>

**(a)** -- `getNewsFeed` only scans the user's followees. If self is missing from that set, the user's own tweet list is never seeded into the heap, so their posts disappear from their feed. That is why `unfollow` guards `followerId == followeeId`.

</details>

**Q3 (transfer).** Suppose you only ever needed the SINGLE most-recent tweet across all followees. How would you simplify `getNewsFeed`?

<details><summary>Show answer</summary>

Skip the heap entirely: scan each followee's last (newest) tweet and keep the one with the largest timestamp -- a single O(K) pass where K is the number of followees. The K-way merge only pays off because the cap is 10 and you must repeatedly pick the next newest.

</details>

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
