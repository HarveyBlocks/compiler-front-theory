package org.harvey.vie.theory;

import org.harvey.vie.theory.demo.SyntaxDemo;
import org.harvey.vie.theory.demo.program.ProgramLexicalDemo;

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
        // lexical.flushTable
        System.out.println("ProgramLexicalDemo.FLUSH_TABLE = " + ProgramLexicalDemo.FLUSH_TABLE);
        // syntax.flushTable
        System.out.println("SyntaxDemo.FLUSH_TABLE = " + SyntaxDemo.FLUSH_TABLE);
    }

    public static int deal(String s) {
        return s.length();
    }

}