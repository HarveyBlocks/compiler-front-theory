package org.harvey.vie.theory.syntax.grammar.tag;

import org.harvey.vie.theory.io.ILoader;
import org.harvey.vie.theory.io.Storage;

/**
 * <p>
 * 用于区分什么Production对应什么Semantic处理. 可以帮助再语义阶段判断当前处理的产生式是否合理<br>
 * <ul>
 *  <li>有些Tag是在左部的, 有些Tag是在右部的</li>
 *  <li>比如 production->a|b|c</li>
 *  <li>production上有tag1</li>
 *  <li>a上有tag2, tag3</li>
 *  <li>b上有tag1, tag2</li>
 *  <li>c上有tag1, tag3</li>
 *  <li>那么, 每一个产生式的tag是</li>
 *  <li>production->a tag1, tag2, tag3</li>
 *  <li>production->b tag1, tag2</li>
 *  <li>production->c tag1, tag3</li>
 * </ul>
 * 实现类应该重写Hashcode和Equals
 * </p>
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 16:18
 */
public interface SemanticTag extends Storage {
    interface Loader<T extends SemanticTag> extends ILoader<T> {
    }
}
