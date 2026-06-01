package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.LocationKind;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 左值转右值支路：当 {@code loc} 出现在表达式位置时，把“位置”读取成“值”。
 * <p>
 * {@link IdentifierUseTranslator}、{@link ArrayAtExpressionTranslator}、{@link MemberAccessTranslator}
 * 这类翻译器通常先生成地址或引用定位命令；本类根据
 * {@link org.harvey.vie.theory.semantic.command.LocationKind} 追加
 * {@code st_top_addr_to_val_*} 或 {@code st_top_ref_to_val_*}，让后续算术/逻辑运算看到真实值。
 * <p>
 * 讲完本支路可继续看 {@link AssignStatementTranslator} 对左值写回的处理，或回到
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:26
 */
public class PrimaryProduceLeftValueTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeRegister constant = ConstantCommandSupport.constantOrNull(context, production, children);
        if (constant != null) {
            return constant;
        }
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[0].register(builder);
        var register = TypeAttributes.childHasType(context, 0)
                ? TypeAttributes.child(context, 0)
                : TypeAttributes.result(context);
        LocationKind locationKind = register.getLocationKind();
        if (locationKind == LocationKind.REFERENCE) {
            builder.add(new TerminalNode(context.getCommandFactory().stTopRefToVal(
                    CommandDataType.forStorage(register.requireType("semantic type is required for left value."))
            )));
        } else {
            builder.add(new TerminalNode(context.getCommandFactory().stTopAddrToVal(
                    CommandDataType.forStorage(register.requireType("semantic type is required for left value."))
            )));
        }
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}
