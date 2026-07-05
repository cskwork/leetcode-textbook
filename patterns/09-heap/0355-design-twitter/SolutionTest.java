import java.util.Arrays;
import java.util.List;

public class SolutionTest {
    public static void main(String[] args) {
        int passed = 0, total = 0;

        // Example from LeetCode
        total++;
        Twitter t = new Twitter();
        t.postTweet(1, 5);
        List<Integer> f1 = t.getNewsFeed(1);
        t.follow(1, 2);
        t.postTweet(2, 6);
        List<Integer> f2 = t.getNewsFeed(1);
        t.unfollow(1, 2);
        List<Integer> f3 = t.getNewsFeed(1);
        if (eq(f1, 5) && eq(f2, 6, 5) && eq(f3, 5)) passed++;
        else System.out.println("FAIL: example -> " + f1 + " / " + f2 + " / " + f3);

        // Feed is capped at 10, newest first
        total++;
        Twitter t2 = new Twitter();
        for (int i = 1; i <= 12; i++) t2.postTweet(1, i);   // ids 1..12, 12 is newest
        List<Integer> f4 = t2.getNewsFeed(1);
        if (eq(f4, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3)) passed++;
        else System.out.println("FAIL: cap-10 -> " + f4);

        // K-way merge across three users, interleaved posts
        total++;
        Twitter t3 = new Twitter();
        t3.follow(1, 2);
        t3.follow(1, 3);
        t3.postTweet(2, 201);   // t0
        t3.postTweet(3, 301);   // t1
        t3.postTweet(2, 202);   // t2
        t3.postTweet(1, 101);   // t3  (user 1 follows self via postTweet)
        List<Integer> f5 = t3.getNewsFeed(1);
        if (eq(f5, 101, 202, 301, 201)) passed++;
        else System.out.println("FAIL: k-way merge -> " + f5);

        // unfollow(self) must be a no-op: own tweets stay visible
        total++;
        Twitter t4 = new Twitter();
        t4.postTweet(1, 5);
        t4.unfollow(1, 1);
        List<Integer> f6 = t4.getNewsFeed(1);
        if (eq(f6, 5)) passed++;
        else System.out.println("FAIL: unfollow-self -> " + f6);

        // A user who never posted and follows nobody sees an empty feed
        total++;
        Twitter t5 = new Twitter();
        List<Integer> f7 = t5.getNewsFeed(99);
        if (f7 != null && f7.isEmpty()) passed++;
        else System.out.println("FAIL: empty feed -> " + f7);

        // Follow then unfollow removes the followee's tweets but keeps own
        total++;
        Twitter t6 = new Twitter();
        t6.postTweet(1, 11);    // t0  (user1)
        t6.postTweet(2, 22);    // t1  (user2)
        t6.follow(1, 2);
        List<Integer> before = t6.getNewsFeed(1);   // [22, 11]
        t6.unfollow(1, 2);
        List<Integer> after = t6.getNewsFeed(1);    // [11]
        if (eq(before, 22, 11) && eq(after, 11)) passed++;
        else System.out.println("FAIL: follow/unfollow -> " + before + " / " + after);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }

    private static boolean eq(List<Integer> actual, int... expected) {
        return actual.size() == expected.length
            && Arrays.equals(actual.stream().mapToInt(Integer::intValue).toArray(), expected);
    }
}
