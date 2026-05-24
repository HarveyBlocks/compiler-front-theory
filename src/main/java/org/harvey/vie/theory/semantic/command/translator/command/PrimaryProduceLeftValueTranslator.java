package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.factory.DefaultCommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO primary->lvalue
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
        // primary->lvalue
        // 此时id保存的commend是reference, primary是右值, 因此需要将引用转成值
        // lvalue.command(); 获取到引用
        // DefaultCommandFactory.st_top_ref_to_val();
        if (children.length != 1) {
            throw new CompilerException("illegal statement on primary to left value production.");
        }
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        thisBuilder.add(new TerminalNode(context.getCommandFactory().stTopRefToVal(TypeAttributes.childType(context, 0))));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}
