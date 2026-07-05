import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SolutionTest {
    static String norm(List<List<String>> res) {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> g : res) {
            List<String> c = new ArrayList<>(g);
            Collections.sort(c);
            copy.add(c);
        }
        copy.sort(Comparator.comparing(g -> String.join(",", g)));
        StringBuilder sb = new StringBuilder();
        for (List<String> g : copy) sb.append("[").append(String.join(",", g)).append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        int passed = 0, total = 0;

        total++; String r1 = norm(s.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
                  String e1 = "[ate,eat,tea][bat][nat,tan]";   // groups sorted lexicographically by norm()
                  if (r1.equals(e1)) passed++; else System.out.println("FAIL: example -> " + r1);

        total++; String r2 = norm(s.groupAnagrams(new String[]{""}));
                  String e2 = "[]";
                  if (r2.equals(e2)) passed++; else System.out.println("FAIL: empty-string -> " + r2);

        total++; String r3 = norm(s.groupAnagrams(new String[]{"a"}));
                  String e3 = "[a]";
                  if (r3.equals(e3)) passed++; else System.out.println("FAIL: single -> " + r3);

        total++; String r4 = norm(s.groupAnagrams(new String[]{"abc", "cba", "bca", "xyz", "zyx"}));
                  String e4 = "[abc,bca,cba][xyz,zyx]";
                  if (r4.equals(e4)) passed++; else System.out.println("FAIL: two-groups -> " + r4);

        total++; String r5 = norm(s.groupAnagrams(new String[]{"listen", "silent", "enlist", "hello", "olelh"}));
                  String e5 = "[enlist,listen,silent][hello,olelh]";
                  if (r5.equals(e5)) passed++; else System.out.println("FAIL: larger -> " + r5);

        System.out.println(passed + "/" + total + " tests passed");
        if (passed == total) System.out.println("All tests passed.");
    }
}
