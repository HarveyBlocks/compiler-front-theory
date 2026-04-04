package org.harvey.vie.theory;

/**
 * 启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:52
 */
public class Main {
    public static void main(String[] args) {
        // 不含重复字符的最长子串()
        System.out.println(deal("bbbbb"));
    }

    public static int deal(String s) {
        // abcabcabc -> 3
        int maxLen = 0;
        int[] dict = new int[26];
        for (int l = 0, r = 0; r < s.length(); ) {
            char c = s.charAt(r);
            if (dict[c - 'a'] == 0) {
                // 没有重复
                dict[c - 'a']++;
                r++;
                maxLen = Math.max(maxLen, r - l);
            } else {
                // 有重复
                char lc;
                while ((lc = s.charAt(l)) != c) {
                    l++;
                    dict[lc - 'a']--;
                }
                l++;
                dict[lc - 'a']--;
            }
        }
        return maxLen;
    }

}