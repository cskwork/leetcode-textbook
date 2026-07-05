import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        // LC example 1: [0,30] overlaps the others.
        total++;
        boolean r1 = s.canAttendMeetings(new int[][]{{0, 30}, {5, 10}, {15, 20}});
        if (!r1) passed++;
        else System.out.println("FAIL: LC example 1 -> " + r1);

        // LC example 2: disjoint meetings.
        total++;
        boolean r2 = s.canAttendMeetings(new int[][]{{7, 10}, {2, 4}});
        if (r2) passed++;
        else System.out.println("FAIL: LC example 2 -> " + r2);

        // Edge: empty list -> trivially attendable.
        total++;
        boolean r3 = s.canAttendMeetings(new int[][]{});
        if (r3) passed++;
        else System.out.println("FAIL: empty list -> " + r3);

        // Edge: single meeting.
        total++;
        boolean r4 = s.canAttendMeetings(new int[][]{{1, 5}});
        if (r4) passed++;
        else System.out.println("FAIL: single meeting -> " + r4);

        // Touching meetings are compatible (back-to-back allowed).
        total++;
        boolean r5 = s.canAttendMeetings(new int[][]{{1, 2}, {2, 3}});
        if (r5) passed++;
        else System.out.println("FAIL: touching back-to-back -> " + r5);

        // Unsorted input with a non-adjacent overlap.
        total++;
        boolean r6 = s.canAttendMeetings(new int[][]{{2, 4}, {7, 10}, {0, 3}});
        if (!r6) passed++;
        else System.out.println("FAIL: unsorted non-adjacent overlap -> " + r6);

        // Fully nested intervals must conflict.
        total++;
        boolean r7 = s.canAttendMeetings(new int[][]{{1, 10}, {2, 5}, {3, 4}});
        if (!r7) passed++;
        else System.out.println("FAIL: nested conflict -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
