package org.harvey.vie.theory;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.lexical.DefaultLexicalDirector;
import org.harvey.vie.theory.lexical.LexicalDirector;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;

import java.text.ParseException;

/**
 * 启动类
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        LexicalDirector director = new DefaultLexicalDirector();
        try {
            DfaStatusTable table = director.direct("(a|b)*abb(a|b)*");
            log.info("yes");
            log.info("{}",table);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
