package org.harvey.vie.theory;

import org.harvey.vie.theory.util.RuntimeProperties;

/**
 * 启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:52
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(deal(""));
        System.out.println(RuntimeProperties.lexicalFlushTable());
        System.out.println(RuntimeProperties.syntaxFlushTable());
        System.out.println(RuntimeProperties.programTestCase());
        System.out.println(RuntimeProperties.configPath());
    }

    public static int deal(String s) {
        return s.length();
    }

}
