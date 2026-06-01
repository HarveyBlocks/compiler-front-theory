package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 声明初始化支路：把 {@code type id = expr;} 翻译成“地址、右值、可选类型转换、写回”。
 * <p>
 * 讲解顺序：
 * 1. 通过 {@link TypeAttributes} 读取声明类型和初始化表达式类型；
 * 2. 用 {@link SemanticDiagnostics#requireAssignable} 检查能否赋值；
 * 3. 通过 {@link #declaredIdentifier(ShiftReduceSemanticContext)} 找到符号表里的
 * {@link IdentifierRecord}，并用 {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#loadIdentifierAddress(IdentifierRecord)}
 * 生成左侧地址命令；
 * 4. 注册右侧表达式子命令，必要时插入
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#stTopCast(CommandDataType, CommandDataType)}；
 * 5. 用 {@code assign_from_st_top_to_addr_*} 写回。
 * <p>
 * 讲完本支路可跳到 {@link AssignStatementTranslator}，它处理普通赋值语句，结构基本相同但会区分地址左值和引用左值。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
public class DeclarationWithInitializationTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production, CommandNodeRegister[] children) {
        SemanticType targetType = TypeAttributes.childType(context, 0);
        SemanticType sourceType = TypeAttributes.childType(context, 3);
        SemanticDiagnostics.requireAssignable(
                context,
                sourceType,
                targetType,
                TypeAttributes.childAnchor(context, 2),
                "assignment requires assignable types."
        );
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        IdentifierRecord record = declaredIdentifier(context);
        // 声明初始化的左边一定是刚声明出的局部/全局槽位地址。
        thisBuilder.add(new TerminalNode(context.getCommandFactory().loadIdentifierAddress(record)));
        children[3].register(thisBuilder);
        if (context.requiresImplicitCast(sourceType, targetType)) {
            // 例如 int32 初始化 float64 时，在写回之前把栈顶值转换成目标类型。
            thisBuilder.add(new TerminalNode(context.getCommandFactory().stTopCast(
                    CommandDataType.forValue(sourceType),
                    CommandDataType.forValue(targetType)
            )));
        }
        thisBuilder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToAddr(
                CommandDataType.forStorage(targetType)
        )));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    private static IdentifierRecord declaredIdentifier(ShiftReduceSemanticContext context) {
        HeadNode head = currentReducedHead(context);
        ShiftReduceSyntaxTreeNode child = head.get(1);
        if (!child.isToken()) {
            throw new CompilerException("declaration identifier requires a token child.");
        }
        SourceToken token = child.toToken().getSource();
        IdentifierRecord record = context.getIdentifier(token);
        if (record == null) {
            throw new CompilerException("declaration identifier is not registered in current scope.");
        }
        return record;
    }

    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new CompilerException("current reduced head is absent for declaration initialization.");
        }
        return context.getTreeContext().peek().toHead();
    }
}
