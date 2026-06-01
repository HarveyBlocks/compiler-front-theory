package org.harvey.vie.theory.semantic.command;

/**
 * 左值位置类别：决定赋值和取值时使用“地址命令”还是“引用命令”。
 * <p>
 * {@link #ADDRESS} 表示变量槽位地址，例如普通局部变量；
 * {@link #REFERENCE} 表示间接位置，例如数组元素或结构体字段。
 * {@link org.harvey.vie.theory.semantic.command.translator.command.AssignStatementTranslator}
 * 根据它选择 {@code assign_from_st_top_to_addr_*} 或 {@code assign_from_st_top_to_ref_*}；
 * {@link org.harvey.vie.theory.semantic.command.translator.command.PrimaryProduceLeftValueTranslator}
 * 根据它选择 {@code st_top_addr_to_val_*} 或 {@code st_top_ref_to_val_*}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 17:10
 */
public enum LocationKind {
    ADDRESS,
    REFERENCE
}
