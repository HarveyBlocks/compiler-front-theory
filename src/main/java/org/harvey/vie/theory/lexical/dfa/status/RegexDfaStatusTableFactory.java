package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 19:35
 */
public class RegexDfaStatusTableFactory implements
        DfaStatusTableFactory<AlphabetCharacter, TokenType, RegexDfaStatusTable> {
    /**
     * 函数功能：根据状态转移数据生产正则 DFA 状态表。
     * 输入：
     * - newStates：状态转移矩阵。
     * - alphabet：字母表数组。
     * - newStart：起始状态编号。
     * - accepts：状态接受类型数组。
     * 输出：生产得到的 RegexDfaStatusTable。
     */
    @Override
    public RegexDfaStatusTable produce(
            int[][] newStates,
            AlphabetCharacter[] alphabet,
            int newStart,
            TokenType[] accepts) {
        return new RegexDfaStatusTable(newStates, alphabet, newStart, accepts);
    }

    /**
     * 函数功能：创建指定长度的词法单元类型数组。
     * 输入：
     * - length：数组长度。
     * 输出：TokenType 数组。
     */
    @Override
    public TokenType[] newVertexArray(int length) {
        return new TokenType[length];
    }

    /**
     * 函数功能：创建指定长度的字母表字符数组。
     * 输入：
     * - length：数组长度。
     * 输出：AlphabetCharacter 数组。
     */
    @Override
    public AlphabetCharacter[] newMotionArray(int length) {
        return new AlphabetCharacter[length];
    }
}
