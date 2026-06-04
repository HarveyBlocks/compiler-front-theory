package org.harvey.vie.theory.syntax.bu;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType;
import org.harvey.vie.theory.semantic.command.command.factory.CommandFactory;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.function.FunctionManager;
import org.harvey.vie.theory.semantic.type.TypeResolver;
import org.harvey.vie.theory.semantic.value.ConstantResolver;
import org.harvey.vie.theory.syntax.SyntaxParsingContext;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:23
 */
public class ShiftReducePhaserImpl implements ShiftReducePhaser {
    private final ShiftReduceParsingTable table;
    private final ShiftReduceCallbackRegister register;
    private final TokenFilterPredict tokenFilterPredict;
    private final CommandFactory commandFactory;
    private final TypeResolver typeResolver;
    private final ConstantResolver constantResolver;
    private final FunctionManager functionManager;
/**
 * 函数功能：创建 ShiftReducePhaserImpl 对象。
 * 输入：
 * - table：ShiftReduceParsingTable 类型参数。
 * - tokenFilterPredict：TokenFilterPredict 类型参数。
 * - register：ShiftReduceCallbackRegister 类型参数。
 * - commandFactory：CommandFactory 类型参数。
 * - typeResolver：TypeResolver 类型参数。
 * - constantResolver：ConstantResolver 类型参数。
 * - functionManager：FunctionManager 类型参数。
 * 输出：无。
 */

    public ShiftReducePhaserImpl(
            ShiftReduceParsingTable table,
            TokenFilterPredict tokenFilterPredict,
            ShiftReduceCallbackRegister register,
            CommandFactory commandFactory,
            TypeResolver typeResolver, ConstantResolver constantResolver, FunctionManager functionManager) {
        this.tokenFilterPredict = tokenFilterPredict;
        this.table = table;
        this.register = register;
        this.commandFactory = commandFactory;
        this.constantResolver = constantResolver;
        this.functionManager = functionManager;
        this.typeResolver = typeResolver;
    }
/**
 * 函数功能：执行语法分析并返回语义结果。
 * 输入：
 * - iterator：SourceTokenIterator 类型参数。
 * - errorContext：ErrorContext 类型参数。
 * 输出：SemanticResult 类型返回值。
 */

    @Override
    public SemanticResult phase(SourceTokenIterator iterator, ErrorContext errorContext) {
        ShiftReducePhaseContext ctx = new ShiftReducePhaseContext(table, iterator, tokenFilterPredict, errorContext);
        ShiftReduceSemanticContext context = new ShiftReduceSemanticContext(
                register,
                ctx,
                commandFactory,
                typeResolver,
                constantResolver,
                functionManager
        );
        context.onStart();
        while (true) {
            SourceToken current = ctx.currentToken();
            if (ctx.isStackEmpty()) {
                context.onError(ShiftReduceErrorType.STACK_UNDERFLOW);
                break;
            }
            int top = ctx.peek();
            ActiveTableElement element = table.activeNext(top, table.matchTerminal(current));
            if (element == null) {
                // error
                context.onError(ShiftReduceErrorType.UNDEFINED_ACTION);
            } else if (element.isShift()) {
                onShift(context, element, current);
            } else if (element.isReduce()) {
                onReduce(context, element);
                if (element.isAccept()) {
                    break; // 结束
                }
            } else {
                throw new CompilerException(new IllegalStateException("Unknown active table element type."));
            }
        }
        return context.getResult();
    }
/**
 * 函数功能：处理规约动作。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - element：ActiveTableElement 类型参数。
 * 输出：无。
 */

    private void onReduce(ShiftReduceSemanticContext context, ActiveTableElement element) {
        SimpleGrammarProduction production = table.getProduction(element.getProduction());
        if (element.isAccept()) {
            context.beforeAccept(production);
            onAccept(context, production);
        } else {
            context.onReduce(production);
        }
    }
/**
 * 函数功能：处理接受动作。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    private void onAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        SyntaxParsingContext<Integer> syntaxContext = context.getSyntaxContext();
        if (!syntaxContext.hasNext()) {
            if (syntaxContext.validAcceptStack()) {
                context.onAccept(production);
            } else {
                context.onError(ShiftReduceErrorType.INVALID_ACCEPTING_STATE);
            }
        } else {
            context.onError(ShiftReduceErrorType.TRAILING_INPUT_AFTER_ACCEPT);
        }
    }
/**
 * 函数功能：处理移进动作。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - element：ActiveTableElement 类型参数。
 * - token：SourceToken 类型参数。
 * 输出：无。
 */

    private void onShift(ShiftReduceSemanticContext context, ActiveTableElement element, SourceToken token) {
        context.onShift(element.nextStatus(), token);
    }
}
