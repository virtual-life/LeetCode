/**

 Given a string, find the length of the longest substring without repeating characters.

 Examples:

 Given "abcabcbb", the answer is "abc", which the length is 3.

 Given "bbbbb", the answer is "b", with the length of 1.

 Given "pwwkew", the answer is "wke", with the length of 3. Note that the answer must be a substring, "pwke" is a subsequence and not a substring

 O(n) - Time
 O(m) - Space
 
  HashSet. 
  Consider the string "abcdaef". When we encounter the second a, we need to reverse the index back starting from b. 
  So the length of the longest substring is "bcdaef", i.e, 6.
 */

public class Solution {
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int start = 0;
        int end = 0;
        int maxlen = 1;
        Set<Character> set = new HashSet<Character>();

        for (end = 0; end < s.length(); end++) {
            if (!set.contains(s.charAt(end))) {
                set.add(s.charAt(end));
            } else {
                maxlen = Math.max(maxlen, end - start);

                // move the start pointer  - until a.charAT(start) == s.charAT(end)
                while (start < end) {
                    set.remove(s.charAt(start));                
                    if (s.charAt(start) == s.charAt(end)) {
                        break;
                    }
                    start++;
                }

                start++;
            }
        }
        maxlen = Math.max(maxlen, end - start);
        return maxlen;
    }
}
