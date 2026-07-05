public class SolutionTest {
    public static void main(String[] args) {
        int passed = 0, total = 0;

        // Case 1: LeetCode example.
        {
            MinStack s = new MinStack();
            s.push(-2); s.push(0); s.push(-3);
            total++; if (s.getMin() == -3) passed++; else System.out.println("FAIL: getMin after pushes -> " + s.getMin());
            s.pop();
            total++; if (s.top() == 0)    passed++; else System.out.println("FAIL: top after pop -> " + s.top());
            total++; if (s.getMin() == -2) passed++; else System.out.println("FAIL: getMin after pop -> " + s.getMin());
        }

        // Case 2: repeated equal minima stay correct.
        {
            MinStack s = new MinStack();
            s.push(5); s.push(5); s.push(5);
            total++; if (s.getMin() == 5) passed++; else System.out.println("FAIL: equal minima getMin -> " + s.getMin());
            s.pop();
            total++; if (s.getMin() == 5) passed++; else System.out.println("FAIL: equal minima after pop -> " + s.getMin());
        }

        // Case 3: single element.
        {
            MinStack s = new MinStack();
            s.push(42);
            total++; if (s.top() == 42 && s.getMin() == 42) passed++;
                     else System.out.println("FAIL: single element top=" + s.top() + " min=" + s.getMin());
        }

        // Case 4: minimum resurfaces after the global min is popped.
        {
            MinStack s = new MinStack();
            s.push(2); s.push(0); s.push(3); s.push(0);
            total++; if (s.getMin() == 0) passed++; else System.out.println("FAIL: dup-zero min -> " + s.getMin());
            s.pop();
            total++; if (s.getMin() == 0) passed++; else System.out.println("FAIL: after popping dup-zero -> " + s.getMin());
            s.pop();
            total++; if (s.getMin() == 0) passed++; else System.out.println("FAIL: min should still be 0 -> " + s.getMin());
            s.pop();
            total++; if (s.getMin() == 2) passed++; else System.out.println("FAIL: min should be 2 now -> " + s.getMin());
        }

        // Case 5: descending then ascending pushes.
        {
            MinStack s = new MinStack();
            s.push(10); s.push(7); s.push(4); s.push(9); s.push(12);
            total++; if (s.getMin() == 4) passed++; else System.out.println("FAIL: mixed push getMin -> " + s.getMin());
            s.pop(); // removes 12
            s.pop(); // removes 9
            total++; if (s.getMin() == 4) passed++; else System.out.println("FAIL: getMin after pops -> " + s.getMin());
        }

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
