# Pattern 5 - Binary Search

Binary search is the single most underused technique among beginners. Most
learners reach for it only when a problem literally says "sorted array" -- and
miss the much larger class of problems where the **answer itself** is what you
binary-search over. This section teaches both faces of the pattern.

---

## What the pattern is

**Halve a monotonic search space every step.** If you can phrase the problem as
"the answer lies somewhere in a range, and one probe in the middle tells me
which half to keep," binary search turns an O(n) or O(range) scan into O(log n)
or O(log range).

The word *monotonic* is the whole deal. Binary search needs the search space to
behave in an ordered way: as you move in one direction, the property you are
testing changes at most once (from *false* to *true*, or from *valid* to
*invalid*, or from *smaller* to *larger*). Once you confirm that monotonic shape
exists, a `while` loop that halves the range is correct by construction.

Concretely, every problem in this section is one of two shapes:

- **Classic binary search.** You have a *sorted array* and you want to find an
  element, its insert position, or the boundary of a property *in the array*.
  The array indices are the search space.
- **Binary search on the answer space.** There is no array to scan -- or there
  is, but the thing you are looking for is a *number* (a speed, a capacity, a
  threshold) living in some range `[lo, hi]`. You binary-search that range, and
  each "is `mid` a valid answer?" probe is an O(n) check over the input.

The second shape is the meta-pattern beginners miss. The give-away phrase is
*"minimum X such that ..."*, *"maximum X such that ..."*, or *"smallest
capacity that works"*. Whenever you see it, ask: *can I check a candidate answer
in one pass? Is the yes/no monotonic as the candidate grows?* If yes, you are
doing binary search on the answer space, not on an array.

---

## When it applies -- trigger signals

Scan the problem for any of these phrases (from `01-patterns-overview.md`):

| Signal in the problem | Which flavor | Example problem |
|---|---|---|
| "sorted array", "find in O(log n)", "find target" | Classic, exact match | 704 Binary Search |
| "where would it go", "insert position", "lower/upper bound" | Classic, insert position | 35 Search Insert Position |
| "first/last version that is bad", "first true in a range" | Classic, boundary | 278 First Bad Version |
| "matrix with sorted rows and columns", "treat 2-D as 1-D" | Classic on flattened index | 74 Search a 2D Matrix |
| "rotated sorted array", "where did it shift", "find the pivot/min" | Classic on a rotated array | 153, 33 |
| "minimum speed / capacity / size such that ...", "smallest k that works" | **Answer space** | 875 Koko Eating Bananas |
| "minimum capacity to ship in D days", "split array largest sum" | **Answer space** | (siblings of 875) |

Two questions decide which flavor you need:

1. *Is the search space the array indices, or is it a range of candidate
   answers?* -> classic vs. answer-space.
2. *Are you looking for an exact element, or for a boundary where a property
   flips?* -> exact-match vs. lower/upper-bound variant inside classic.

---

## The two reusable templates

### Template A -- classic binary search, exact match

Use when you want a *specific index* (the element, its insert position, a value
in a matrix). The loop runs while `lo <= hi`, both pointers move by `±1`, and
you usually return from inside the loop on a hit.

```
function classicSearch(sorted_array, target):
    low  <- 0
    high <- length(sorted_array) - 1

    while low <= high:
        mid <- low + (high - low) / 2      # overflow-safe midpoint
        if sorted_array[mid] equals target:
            return mid                      # exact match found
        else if sorted_array[mid] < target:
            low  <- mid + 1                 # discard left half (incl. mid)
        else:
            high <- mid - 1                 # discard right half (incl. mid)

    return not-found sentinel               # e.g. -1, or `low` for insert position
```

The key property of Template A: when the loop ends, `low` is the index *just
past* the largest element still below `target`. That is exactly the **insert
position**. LC 35 exploits this -- it is literally Template A with `return low`
in place of `return -1`.

### Template B -- binary search on the answer space (and on boundaries)

Use when the search space is a *range of candidate answers*, or when you want
the *first* index where a predicate flips from false to true. The loop runs
while `low < high`, only one pointer (`low`) ever advances by `+1`, and the loop
ends with `low == high` -- which *is* the answer.

```
function answerSpaceSearch(lowest_candidate, highest_candidate, predicate):
    low  <- lowest_candidate
    high <- highest_candidate

    while low < high:
        mid <- low + (high - low) / 2       # floor leans toward low on purpose
        if predicate(mid) is true:
            high <- mid                     # mid works -> answer is <= mid
        else:
            low  <- mid + 1                 # mid fails -> answer is strictly > mid

    return low                              # low == high == first true answer
```

The `predicate` is a yes/no function that is **monotonic**: once it becomes true
for some candidate, it stays true for every larger candidate (or the reverse --
the direction is what tells you which pointer to move).

- For LC 278 the predicate is `isBadVersion(mid)` -- once versions go bad they
  stay bad, so "true" means look left (`high = mid`).
- For LC 875 the predicate is `canFinishEatingAtSpeed(mid)` -- once a speed is
  fast enough, every faster speed is also fast enough, so again "true" means
  look left (`high = mid`). The answer space is `[1, max(piles)]`, *not* the
  array indices.

**Why this form is safe from infinite loops.** Because `mid` is floored toward
`low`, when `low < high` we always have `mid < high`, so `high <- mid` strictly
shrinks the upper end; and `low <- mid + 1` strictly grows the lower end. Both
moves are non-zero, so the gap closes every iteration.

### Which template for which problem?

| Problem | Template | Predicate / what `mid` means |
|---|---|---|
| 704 Binary Search | A | exact equality on `nums[mid]` |
| 35 Search Insert Position | A | equality; fall back to `return low` |
| 278 First Bad Version | B | `isBadVersion(mid)` -- first true in `1..n` |
| 74 Search a 2D Matrix | A | flatten `m x n` into one index range |
| 875 Koko Eating Bananas | B | `canFinish(piles, mid, h)` -- answer space `[1, max]` |
| 153 Find Min in Rotated | B-ish | compare `nums[mid]` vs `nums[high]` to pick the half |
| 33 Search in Rotated | A | decide which half is sorted, then where target lives |

LC 153 sits between the two templates: it uses the `low < high` shape of B but
its "predicate" is a comparison between `nums[mid]` and `nums[high]` rather than
an external yes/no function. LC 33 is a Template-A loop whose *body* is more
involved, because a rotated array has two sorted halves and you must figure out
which one `mid` lands in.

---

## The three loop conventions (and which one this book picks)

Three idioms float around online, and they confuse every beginner. Here is what
each means and when it is correct.

1. **`while (lo <= hi)` with `hi = mid - 1` and `lo = mid + 1`.**
   *Inclusive* on both ends. The loop runs one extra iteration where `lo == hi`.
   Correct for **exact match** (Template A). You can `return` from inside the
   loop the instant you hit the target. After the loop, `lo` is one past the last
   element < target, which is the insert position.

2. **`while (lo < hi)` with `hi = mid` (not `mid - 1`) and `lo = mid + 1`.**
   *Half-open / reducing* form. The loop ends with `lo == hi`. Correct for
   **boundaries and answer-space** (Template B). `mid` must be the floor
   (`lo + (hi - lo) / 2`); using the ceiling here causes an infinite loop.

3. **`while (lo < hi)` with `hi = mid - 1` style but treated as exclusive upper
   bound.** Rarely worth the cognitive cost; easy to get off-by-one. Avoid in
   this book.

**This book's rule:** use Template A (`lo <= hi`) whenever you are hunting an
exact index or insert position, and Template B (`lo < hi`) whenever you are
hunting a *boundary* or searching an *answer space*. Never mix the two within
one problem. Picking the form first, then writing the body, eliminates almost
every off-by-one bug.

---

## Problems in this section

Seven problems, ramping Easy -> Medium, classic -> answer-space -> rotated.

| # | LC | Folder | Difficulty | One-line teaser |
|---|----|--------|-----------|-----------------|
| 26 | 704 | [0704-binary-search](./0704-binary-search/) | Easy | The "hello world" of binary search -- find a target in a sorted array. |
| 27 | 35 | [0035-search-insert-position](./0035-search-insert-position/) | Easy | Same loop; when the target is missing, `lo` is exactly where it belongs. |
| 28 | 278 | [0278-first-bad-version](./0278-first-bad-version/) | Easy | Your first *boundary* search: find the first version where `isBadVersion` flips true. |
| 29 | 74 | [0074-search-a-2d-matrix](./0074-search-a-2d-matrix/) | Medium | A sorted matrix is just one long sorted array -- map an index to (row, col). |
| 30 | 875 | [0875-koko-eating-bananas](./0875-koko-eating-bananas/) | Medium | The canonical **answer-space** search: binary-search the eating speed, not the array. |
| 31 | 153 | [0153-find-minimum-in-rotated-sorted-array](./0153-find-minimum-in-rotated-sorted-array/) | Medium | Use the rotated invariant: one half is always sorted; the min hides in the other. |
| 32 | 33 | [0033-search-in-rotated-sorted-array](./0033-search-in-rotated-sorted-array/) | Medium | Like 153, but you want a target, so first work out which half is sorted each step. |

Do them in order. 704 and 35 install Template A. 278 introduces Template B on a
trivial predicate. 74 stretches Template A across two dimensions. 875 is the
payoff -- Template B with a *real* O(n) predicate over an answer space. 153 and
33 then show that "sorted" can be locally true even in a rotated array, which is
the gateway to every hard binary-search variant.

---

## Common pitfalls

Binary search is famous for off-by-one bugs. These are the ones that bite
beginners in this section:

- **Integer overflow on `(lo + hi) / 2`.** If `lo + hi` exceeds `Integer.MAX_VALUE`
  (about 2.1 billion) the sum wraps to a negative index and the program crashes.
  Always write `mid = lo + (hi - lo) / 2`. It computes the same midpoint without
  ever adding the two bounds. This is non-negotiable in every solution below.

- **Infinite loop when only one pointer moves and `mid` does not lean the right
  way.** In Template B with `hi = mid`, if you used the ceiling midpoint
  `mid = lo + (hi - lo + 1) / 2` instead of the floor, then on a two-element
  range `mid == hi`, `hi = mid` makes no progress, and the loop never ends. The
  floor midpoint is what makes `hi = mid` terminate. (Symmetrically, the ceiling
  is required if you ever write `lo = mid`.) Match the midpoint to the pointer
  that takes the `= mid` branch.

- **Picking the wrong mid variant for rotated arrays.** LC 153 and 33 are not
  about a single global predicate; they are about *which side is sorted*. The
  reliable trick is to compare `nums[mid]` against `nums[hi]` (for 153) or
  `nums[lo]` (for 33) and reason about the sorted half. Comparing `nums[mid]`
  against the *target* first, before you know which half is sorted, is the
  classic mistake that sends you into the wrong half every time.

- **Off-by-one on the insert position.** LC 35 returns `lo` after the loop, not
  `-1`, not `hi`, and not `lo + 1`. The invariant of Template A guarantees that
  `lo` is the first index whose value is `>= target`. Trust the invariant; do
  not "fix" it with a `+1`.

- **Treating the answer-space search like the array search.** In LC 875 the
  thing you binary-search (the speed `k`) is *not* an index into `piles`; it is
  a value in `[1, max(piles)]`. Beginners often try `hi = piles.length - 1`,
  which is meaningless here. Always name the search space explicitly: "the answer
  is an integer in [lo, hi]; what are lo and hi?"

- **Forgetting the empty array.** LC 704 and 35 must handle `length == 0`. With
  Template A the loop simply never executes and you fall through to the
  sentinel / `return lo` (= 0), which is correct. But a manual `nums[0]` access
  before the loop will throw.

- **Using `mid = (lo + hi) >>> 1` and not knowing why.** The unsigned-right-shift
  trick also dodges overflow (it shoves the wrapped sum back into range via the
  sign bit). It works, but `lo + (hi - lo) / 2` is clearer to a beginner and is
  what every solution in this section uses. Pick one and be consistent.

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In one sentence, what single property must the search space have for binary search to be valid at all?

<details><summary>Show answer</summary>

It must be monotonic -- the property you test must change at most once as you move through the space (from false to true, or smaller to larger). One clean flip is what halving can hunt.

</details>

**Q2 (pattern recognition).** A new problem: "you have n jobs of given sizes and h hours; find the smallest machine speed such that every job finishes in time." Which flavor of this pattern fits?
- a) Binary search on the answer space (Template B), with an O(n) feasibility check per candidate speed
- b) Classic exact-match search (Template A) on the job array
- c) A hash map of job sizes

<details><summary>Show answer</summary>

**(a)** -- "smallest speed such that ..." is the answer-space signal; the speed is the hidden number, and "can all jobs finish at this speed?" is the monotonic predicate. This is Koko in another costume.

</details>

**Q3 (pattern recognition).** A new problem: "given a sorted array of distinct numbers, count how many elements are strictly less than x." Which approach uses this pattern most directly?
- a) Run Template A for `x`; the value of `lo` at exit IS that count (the insert position of `x`)
- b) Binary search the answer space `[0, n]`
- c) A linear scan is the only option

<details><summary>Show answer</summary>

**(a)** -- the LC 35 invariant says `lo` after the loop is the first index whose value is `>= x`, which is exactly the count of elements `< x`. One search, no counting loop.

</details>

**Q4 (apply).** On `nums = [2, 4, 6, 8, 10]`, a Template A search for `target = 5` exits. What are `lo` and `hi`, and what does `lo` represent?
- a) `lo = 2, hi = 1`; `lo` is the insert position (first index with value `>= 5`)
- b) `lo = 1, hi = 2`
- c) `lo = 0, hi = 4`

<details><summary>Show answer</summary>

**(a)** -- the loop narrows to `lo=2, hi=1` and exits; index 2 holds 6, the first value `>= 5`, so that is both the insert position and the "strictly-less" count.

</details>

**Q5 (design).** Sketch (in words, not code) how to solve "minimum size subarray sum `>= target`" using binary search on the answer space. What is the search space, and what is the feasibility check?

<details><summary>Show answer</summary>

Search space is subarray length `[1, n]`. For a candidate length `mid`, check (via a sliding window of size `mid`, or prefix sums) whether any subarray of that length sums to `>= target`. The predicate "a subarray of length `mid` reaches target" is monotonic -- longer lengths only make larger sums reachable -- so Template B finds the minimum length that works.

</details>

---

Next: [0704 - Binary Search](./0704-binary-search/) -- start here.
