/**
 * 增广后的, 真的有必要拆分 {@link org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol} 吗? <p>
 * 不拆分可以吗? 不拆分就是后面的 {@link org.harvey.vie.theory.syntax.bu.item.ProductionItem} 复杂一些, 多一些字段.<p>
 * 拆分的话, 将导致 {@link org.harvey.vie.theory.syntax.grammar.first.FirstMapFactory} 的构造更加复杂, 为了提高复用, 必须Adaptor.
 * 思来想去, 我决定直接让用户决定谁是Start
 */
package org.harvey.vie.theory.syntax.bu.augmente;
