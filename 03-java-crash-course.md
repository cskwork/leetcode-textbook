# 03 - Java Crash Course (just enough for LeetCode)

This is the Java you need for this book. If you already write Java, skip to the patterns.

## 1. Primitive types

```java
int   n = 5;          // 32-bit integer, range ~ +/- 2.1 billion
long  big = 10_000_000_000L;   // 64-bit, for results that overflow int
double d = 3.14;
boolean ok = true;
char c = 'A';         // single quote, 16-bit Unicode
```

LeetCode gotcha: many problems where you sum or multiply can overflow `int`. Use `long` for
intermediate results.

## 2. Arrays

```java
int[] nums = new int[5];                 // all zeros
int[] xs = {2, 7, 11, 15};               // literal
int len = xs.length;                     // length is a field, not a method
int first = xs[0];
xs[3] = 99;

int[][] grid = new int[3][4];            // 2-D
int m = grid.length, n = grid[0].length;
```

Sort an array:

```java
int[] a = {3, 1, 2};
Arrays.sort(a);                          // in-place, O(n log n)
```

Convert list <-> array:

```java
List<Integer> list = Arrays.asList(1, 2, 3);   // fixed-size
int[] arr = list.stream().mapToInt(Integer::intValue).toArray();
List<Integer> back = new ArrayList<>();
for (int x : arr) back.add(x);
```

## 3. Strings

```java
String s = "hello";
int len = s.length();                    // method, with parens
char ch = s.charAt(0);                   // 'h'
String sub = s.substring(1, 3);          // "el", end exclusive
boolean eq = s.equals("hello");          // NEVER use == on Strings
String[] parts = "a,b,c".split(",");
String joined = String.join("-", parts); // "a-b-c"
```

To build a String in a loop, use `StringBuilder` (O(1) append) not `+=` (O(n) per append):

```java
StringBuilder sb = new StringBuilder();
for (char c : arr) sb.append(c);
String result = sb.toString();
```

## 4. The collections you will use most

### ArrayList (dynamic array)

```java
List<Integer> list = new ArrayList<>();
list.add(10);                            // append, O(1) amortized
list.add(0, 5);                          // insert at index, O(n)
list.get(2);                             // O(1)
list.set(1, 20);                         // O(1)
list.remove(0);                          // by index, O(n)
list.size();
Collections.sort(list);                  // in-place
boolean has = list.contains(7);          // O(n) - slow!
```

### HashMap (key -> value, O(1) average)

```java
Map<String, Integer> freq = new HashMap<>();
freq.put("apple", 1);
freq.put("apple", freq.getOrDefault("apple", 0) + 1);   // counting idiom
int x = freq.get("apple");               // returns null if missing -> NPE on int
boolean has = freq.containsKey("apple");
freq.remove("apple");

for (Map.Entry<String, Integer> e : freq.entrySet()) {
    String k = e.getKey();
    int v = e.getValue();
}
```

### HashSet (just keys, no values)

```java
Set<Integer> seen = new HashSet<>();
seen.add(7);
seen.contains(7);                        // O(1)
seen.remove(7);
seen.size();
```

### PriorityQueue (heap)

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();           // min first
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
minHeap.offer(5);                        // O(log n)
minHeap.peek();                          // O(1), smallest
minHeap.poll();                          // O(log n), remove smallest
```

### ArrayDeque (use this for both Stack and Queue)

```java
Deque<Integer> stack = new ArrayDeque<>();    // LIFO
stack.push(1); stack.push(2);
int top = stack.peek();                  // 2
stack.pop();                             // 2

Deque<Integer> queue = new ArrayDeque<>();    // FIFO: use offer/poll
queue.offer(1); queue.offer(2);
int head = queue.peek();                 // 1
queue.poll();                            // 1
```

Prefer `ArrayDeque` over the legacy `Stack` and `LinkedList` classes -- it is faster.

## 5. Control flow

```java
if (x > 0) { ... } else if (x == 0) { ... } else { ... }

for (int i = 0; i < n; i++) { ... }                  // count
for (int x : arr) { ... }                            // iterate
while (cond) { ... }
for (int i = 0; i < n && arr[i] != target; i++) { }  // early exit

switch (c) {
    case '(': case '[': case '{': open++; break;
    case ')': case ']': case '}': close++; break;
    default: // anything else
}
```

## 6. Methods and classes

A LeetCode solution is a class with a method matching the problem signature:

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        return new int[]{};
    }
}
```

Note `new int[]{a, b}` -- the literal array syntax for returning.

## 7. Recursive helpers

Most tree / backtracking solutions need a helper method because the public signature does not
accept the extra state you need (a parent, an accumulator, a depth). Define it private:

```java
class Solution {
    public int maxDepth(TreeNode root) {
        return dfs(root);
    }
    private int dfs(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(dfs(node.left), dfs(node.right));
    }
}
```

## 8. Common LeetCode node classes

These are defined by LeetCode; you do not import them. For local testing, copy the definitions
into your test file.

```java
public class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}
```

## 9. Math helpers you'll reuse

```java
Math.max(a, b); Math.min(a, b);
Math.abs(x);
Integer.MAX_VALUE;   // 2147483647
Integer.MIN_VALUE;
Math.pow(2, 10);     // returns double
1 << k;              // 2^k as int
n & (n - 1);         // clears lowest set bit (bit manipulation pattern)
```

## 10. The boilerplate of a runnable test

Every `SolutionTest.java` in this book follows this shape (no JUnit needed):

```java
public class SolutionTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; int[] r1 = s.twoSum(new int[]{2,7,11,15}, 9);
                 if (r1[0]==0 && r1[1]==1) passed++; else System.out.println("FAIL: case 1 -> "+java.util.Arrays.toString(r1));

        total++; int[] r2 = s.twoSum(new int[]{3,2,4}, 6);
                 if (r2[0]==1 && r2[1]==2) passed++; else System.out.println("FAIL: case 2 -> "+java.util.Arrays.toString(r2));

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
```

Run with:

```bash
javac Solution.java SolutionTest.java && java SolutionTest
```

## 11. Style conventions used in this book

- 4-space indentation, no tabs.
- `camelCase` for variables and methods, `PascalCase` for classes.
- Generics with diamond: `new ArrayList<>()`.
- Always declare interfaces on the left: `List<Integer>` not `ArrayList<Integer>` when the
  variable is just iterated.
- Comments only where the *why* is non-obvious.

You now have enough Java to read every solution in this book. Open
[patterns/01-arrays-hashing/](./patterns/01-arrays-hashing/) and begin.
