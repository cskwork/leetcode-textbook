import java.util.*;

public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int r1 = s.evalRPN(new String[]{"2", "1", "+", "3", "*"});
                 if (r1 == 9) passed++; else System.out.println("FAIL: ((2+1)*3) -> " + r1);

        total++; int r2 = s.evalRPN(new String[]{"4", "13", "5", "/", "+"});
                 if (r2 == 6) passed++; else System.out.println("FAIL: 4+(13/5) -> " + r2);

        total++; int r3 = s.evalRPN(new String[]{"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"});
                 if (r3 == 22) passed++; else System.out.println("FAIL: big example -> " + r3);

        total++; int r4 = s.evalRPN(new String[]{"42"});
                 if (r4 == 42) passed++; else System.out.println("FAIL: single operand -> " + r4);

        total++; int r5 = s.evalRPN(new String[]{"3", "11", "-"});
                 if (r5 == -8) passed++; else System.out.println("FAIL: 3-11 -> " + r5);

        // Division must truncate toward zero, including the negative case: 7 / -3 = -2.
        total++; int r6 = s.evalRPN(new String[]{"7", "-3", "/"});
                 if (r6 == -2) passed++; else System.out.println("FAIL: 7/-3 truncate toward zero -> " + r6);

        total++; int r7 = s.evalRPN(new String[]{"-7", "3", "/"});
                 if (r7 == -2) passed++; else System.out.println("FAIL: -7/3 truncate toward zero -> " + r7);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
