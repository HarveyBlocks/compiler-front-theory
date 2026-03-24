package org.harvey.vie.theory.lexical.analysis.token;

/**
 * TODO
 * input 可以为流式. 何为流式? 最后的词可以不全, 无法构成词元, 不全的话会被保留.
 * 比如, 由于 buffer, 导致一次读入的数据不全, 但不代表最后的词元是错误的, 应该被保留, 留到和下一次的合并来解决.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:15
 */
public interface StreamSourceTokenIterator {
    SourceTokenIterator iterator(String input);
    SourceTokenIterator iterator(byte[] input);
    SourceTokenIterator iterator(char[] input);

    SourceTokenIterator iterator(byte[] input, int offset, int limit);
    SourceTokenIterator iterator(char[] input, int offset, int limit);
}
