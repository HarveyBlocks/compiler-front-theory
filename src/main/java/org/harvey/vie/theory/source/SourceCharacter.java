package org.harvey.vie.theory.source;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:36
 */
public interface SourceCharacter extends  Comparable<SourceCharacter> {
    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

}
