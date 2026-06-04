package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTable;

import java.text.ParseException;
import java.util.List;

/**
 * Interface for directing the compilation of lexical patterns into an
 * executable DFA transition first. It abstracts the multistep process
 * of regex-to-DFA conversion.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 00:32
 */
public interface LexicalDirector {
    /**
     * 函数功能：将单个词法模式构建为正则 DFA 状态表。
     * 输入：
     * - parten：待构建的词法模式。
     * 输出：构建完成的 RegexDfaStatusTable。
     */
    RegexDfaStatusTable direct(LexicalPattern parten) throws ParseException;

    /**
     * 函数功能：将多个词法模式构建为正则 DFA 状态表。
     * 输入：
     * - patterns：待构建的词法模式列表。
     * 输出：构建完成的 RegexDfaStatusTable。
     */
    RegexDfaStatusTable direct(List<LexicalPattern> patterns) throws ParseException;
}
