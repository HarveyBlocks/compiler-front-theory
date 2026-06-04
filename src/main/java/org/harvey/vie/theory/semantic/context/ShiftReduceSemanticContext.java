package org.harvey.vie.theory.semantic.context;

import lombok.Getter;
import org.harvey.vie.theory.error.SemanticErrorMessage;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType;
import org.harvey.vie.theory.semantic.command.command.factory.CommandFactory;
import org.harvey.vie.theory.semantic.command.FunctionCommandSegment;
import org.harvey.vie.theory.semantic.command.FunctionCommandSegmentContext;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.function.*;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierTableBuilder;
import org.harvey.vie.theory.semantic.structure.StructContext;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.tree.node.TreeContext;
import org.harvey.vie.theory.semantic.type.*;
import org.harvey.vie.theory.semantic.value.ConstantResolver;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.semantic.value.ConstantValueContext;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaseContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-12 21:47
 */
@Getter
public class ShiftReduceSemanticContext {
    private final ShiftReduceCallbackRegister register;
    @Getter
    private SemanticResult result;
    private final ShiftReducePhaseContext syntaxContext;
    private Iterator<ShiftReduceCallback> callbackIter;
    @Getter
    private final TreeContext treeContext = new TreeContext();
    @Getter
    private final CommandContext commandContext = new CommandContext();
    @Getter
    private final FunctionCommandSegmentContext functionCommandSegmentContext = new FunctionCommandSegmentContext();

    @Getter
    private final CommandFactory commandFactory;
    @Getter
    private final TypeContext typeContext = new TypeContext();
    @Getter
    private final ConstantValueContext constantValueContext = new ConstantValueContext();
    private final TypeResolver typeResolver;
    private final ConstantResolver constantResolver;
    private final TypeConversionRule typeConversionRule = new TypeConversionRule();
    private final IdentifierTableBuilder identifierTableBuilder = new IdentifierTableBuilder();
    private final List<IdentifierRecord> identifierRecords = new ArrayList<>();
    private final FunctionContext functionContext = new FunctionContext();
    private final StructContext structContext = new StructContext();
    private final ArrayDeque<Boolean> blockFunctionFlags = new ArrayDeque<>();
    private int pendingStructBraceDepth;
    private FunctionRecord pendingFunction;
    @Getter
    private final FunctionManager functionManager;
/**
 * 函数功能：创建 ShiftReduceSemanticContext 对象。
 * 输入：
 * - register：ShiftReduceCallbackRegister 类型参数。
 * - syntaxContext：ShiftReducePhaseContext 类型参数。
 * - commandFactory：CommandFactory 类型参数。
 * - typeResolver：TypeResolver 类型参数。
 * - constantResolver：ConstantResolver 类型参数。
 * - functionManager：FunctionManager 类型参数。
 * 输出：无。
 */

    public ShiftReduceSemanticContext(
            ShiftReduceCallbackRegister register,
            ShiftReducePhaseContext syntaxContext,
            CommandFactory commandFactory,
            TypeResolver typeResolver,
            ConstantResolver constantResolver, FunctionManager functionManager) {
        this.register = register;
        this.syntaxContext = syntaxContext;
        callbackIter = register.iterator();
        this.commandFactory = commandFactory;
        this.typeResolver = typeResolver;
        this.constantResolver = constantResolver;
        this.functionManager = functionManager;
    }

    // region callback
    /**
     * 函数功能：按产生式体弹出状态。
     * 输入：
     * - body：AlterableSymbol 类型参数。
     * 输出：无。
     */
    private void popStatus(AlterableSymbol body) {
        int k = body.isEpsilon() ? 0 : body.toConcatenation().size();
        while (k-- > 0) {
            if (syntaxContext.isStackEmpty()) {
                throw new CompilerException("no more status in stack to be pop while reducing");
            }
            syntaxContext.pop();
        }
    }
/**
 * 函数功能：调用接受前回调。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    private void invokeBeforeAccept(SimpleGrammarProduction production) {
        // accept 是特殊的 reduce
        AlterableSymbol body = production.getBody();
        popStatus(body);
    }
/**
 * 函数功能：调用规约回调。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    private void invokeReduce(SimpleGrammarProduction production) {
        // 输入指针不动(归约不消耗输入符号)
        AlterableSymbol body = production.getBody();
        popStatus(body);
        int top = syntaxContext.peek();
        int next = syntaxContext.gotoNext(top, production.getHead());
        syntaxContext.push(next);
    }
/**
 * 函数功能：调用移进回调。
 * 输入：
 * - nextStatus：int 类型参数。
 * 输出：无。
 */

    private void invokeShift(int nextStatus) {
        syntaxContext.push(nextStatus);
        syntaxContext.consumeCurrentToken();
    }
/**
 * 函数功能：处理语义分析开始事件。
 * 输入：
 * - 无。
 * 输出：无。
 */

    public void onStart() {
        invokeNext(c -> c.onStart(this), this::invokeNothing);
    }
/**
 * 函数功能：调用下一个语义回调。
 * 输入：
 * - consumer：Consumer<ShiftReduceCallback> 类型参数。
 * - invoker：Runnable 类型参数。
 * 输出：无。
 */

    private void invokeNext(Consumer<ShiftReduceCallback> consumer, Runnable invoker) {
        if (callbackIter.hasNext()) {
            consumer.accept(callbackIter.next());
        } else {
            invoker.run();
            callbackIter = register.iterator();
        }
    }
/**
 * 函数功能：执行空回调操作。
 * 输入：
 * - 无。
 * 输出：无。
 */

    private void invokeNothing() {}
/**
 * 函数功能：处理语义或语法错误事件。
 * 输入：
 * - errorType：ShiftReduceErrorType 类型参数。
 * 输出：无。
 */

    public void onError(ShiftReduceErrorType errorType) {
        invokeNext(c -> c.onError(this, errorType), this::invokeNothing);
    }
/**
 * 函数功能：处理接受事件。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    public void onAccept(SimpleGrammarProduction production) {
        invokeNext(c -> c.onAccept(this, production), this::invokeNothing);
    }
/**
 * 函数功能：处理移进事件。
 * 输入：
 * - nextStatus：int 类型参数。
 * - token：SourceToken 类型参数。
 * 输出：无。
 */

    public void onShift(int nextStatus, SourceToken token) {
        invokeNext(c -> c.onShift(this, nextStatus, token), () -> invokeShift(nextStatus));
    }
/**
 * 函数功能：处理规约事件。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    public void onReduce(SimpleGrammarProduction production) {
        invokeNext(c -> c.onReduce(this, production), () -> invokeReduce(production));
    }
/**
 * 函数功能：处理接受前事件。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    public void beforeAccept(SimpleGrammarProduction production) {
        invokeNext(c -> c.beforeAccept(this, production), () -> invokeBeforeAccept(production));
    }
    // endregion
/**
 * 函数功能：设置语义分析结果。
 * 输入：
 * - result：SemanticResult 类型参数。
 * 输出：无。
 */

    public void setResult(SemanticResult result) {
        this.result = result;
    }
/**
 * 函数功能：注册函数命令片段。
 * 输入：
 * - segment：FunctionCommandSegment 类型参数。
 * 输出：无。
 */

    public void registerFunctionCommandSegment(FunctionCommandSegment segment) {
        functionCommandSegmentContext.register(segment);
    }

    // region error context
    /**
     * 函数功能：添加错误信息。
     * 输入：
     * - offset：int 类型参数。
     * - message：String 类型参数。
     * 输出：无。
     */
    public void addError(int offset, String message) {
        syntaxContext.getErrorContext().addError(new SemanticErrorMessage(offset, message));
    }
    // endregion

    // region identifier table builder
    /**
     * 函数功能：判断标识符是否存在。
     * 输入：
     * - identifierToken：SourceToken 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean existIdentifier(SourceToken identifierToken) {
        return identifierTableBuilder.existIdentifier(identifierToken);
    }
/**
 * 函数功能：获取标识符记录。
 * 输入：
 * - identifierToken：SourceToken 类型参数。
 * 输出：IdentifierRecord 类型返回值。
 */

    public IdentifierRecord getIdentifier(SourceToken identifierToken) {
        return identifierTableBuilder.getIdentifier(identifierToken);
    }
/**
 * 函数功能：更新标识符常量值。
 * 输入：
 * - identifierToken：SourceToken 类型参数。
 * - constantValue：ConstantValue 类型参数。
 * 输出：无。
 */

    public void updateIdentifierConstant(SourceToken identifierToken, ConstantValue constantValue) {
        IdentifierRecord record = getIdentifier(identifierToken);
        if (record != null) {
            record.setConstantValue(constantValue);
        }
    }
/**
 * 函数功能：按编号获取标识符记录。
 * 输入：
 * - no：int 类型参数。
 * 输出：IdentifierRecord 类型返回值。
 */

    public IdentifierRecord getIdentifierByNo(int no) {
        for (IdentifierRecord identifierRecord : identifierRecords) {
            if (identifierRecord.getNo() == no) {
                return identifierRecord;
            }
        }
        return null;
    }
/**
 * 函数功能：注册标识符记录。
 * 输入：
 * - typeHeadNode：HeadNode 类型参数。
 * - declaredType：SemanticType 类型参数。
 * - identifierToken：SourceToken 类型参数。
 * - initialized：boolean 类型参数。
 * - constantValue：ConstantValue 类型参数。
 * 输出：无。
 */

    public void registerIdentifier(
            HeadNode typeHeadNode,
            SemanticType declaredType,
            SourceToken identifierToken,
            boolean initialized,
            ConstantValue constantValue) {
        FunctionRecord ownerFunction = insideFunction() ? currentFunction() : null;
        identifierRecords.add(identifierTableBuilder.registerIdentifier(typeHeadNode,
                declaredType,
                identifierToken,
                initialized,
                ownerFunction,
                constantValue
        ));
    }
/**
 * 函数功能：进入新的作用域。
 * 输入：
 * - 无。
 * 输出：无。
 */


    public void scopeInto() {
        identifierTableBuilder.scopeInto();
    }
/**
 * 函数功能：退出当前作用域。
 * 输入：
 * - 无。
 * 输出：IdentifierRecord[] 类型数组。
 */

    public IdentifierRecord[] scopeExist() {
        return identifierTableBuilder.scopeExist();
    }
/**
 * 函数功能：获取标识符记录列表。
 * 输入：
 * - 无。
 * 输出：IdentifierRecord[] 类型数组。
 */

    public IdentifierRecord[] identifierRecords() {
        return identifierRecords.toArray(IdentifierRecord[]::new);
    }
    // endregion

    // region functions
    /**
     * 函数功能：判断函数是否存在。
     * 输入：
     * - nameToken：SourceToken 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean existFunction(SourceToken nameToken) {
        return functionContext.exists(nameToken);
    }
/**
 * 函数功能：获取函数记录。
 * 输入：
 * - nameToken：SourceToken 类型参数。
 * 输出：FunctionRecord 类型返回值。
 */

    public FunctionRecord getFunction(SourceToken nameToken) {
        return functionContext.get(nameToken);
    }
/**
 * 函数功能：注册函数记录。
 * 输入：
 * - record：FunctionRecord 类型参数。
 * 输出：无。
 */

    public void registerFunction(FunctionRecord record) {
        functionContext.register(record);
    }
/**
 * 函数功能：获取函数表大小。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    public int functionTableSize() {
        return functionContext.records().size();
    }
/**
 * 函数功能：注册当前函数。
 * 输入：
 * - record：FunctionRecord 类型参数。
 * 输出：无。
 */

    public void registerCurrentFunction(FunctionRecord record) {
        functionContext.register(record);
        functionContext.enter(record);
    }
/**
 * 函数功能：进入函数语义上下文。
 * 输入：
 * - record：FunctionRecord 类型参数。
 * 输出：无。
 */

    public void enterFunction(FunctionRecord record) {
        functionContext.enter(record);
    }
/**
 * 函数功能：退出函数语义上下文。
 * 输入：
 * - 无。
 * 输出：FunctionRecord 类型返回值。
 */

    public FunctionRecord exitFunction() {
        return functionContext.exit().getFunction();
    }
/**
 * 函数功能：判断当前是否位于函数内部。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    public boolean insideFunction() {
        return functionContext.insideFunction();
    }
/**
 * 函数功能：获取当前函数返回类型。
 * 输入：
 * - 无。
 * 输出：SemanticType 类型返回值。
 */

    public SemanticType currentFunctionReturnType() {
        return functionContext.currentReturnType();
    }
/**
 * 函数功能：获取当前函数记录。
 * 输入：
 * - 无。
 * 输出：FunctionRecord 类型返回值。
 */

    public FunctionRecord currentFunction() {
        return functionContext.requireCurrent();
    }
/**
 * 函数功能：获取当前函数体状态。
 * 输入：
 * - 无。
 * 输出：FunctionBodyState 类型返回值。
 */

    public FunctionBodyState currentFunctionBodyState() {
        return functionContext.currentBodyState();
    }
/**
 * 函数功能：判断当前代码块是否为函数体。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    public boolean isCurrentBlockFunctionBody() {
        return !blockFunctionFlags.isEmpty() && blockFunctionFlags.peek();
    }
/**
 * 函数功能：标记待进入的函数记录。
 * 输入：
 * - record：FunctionRecord 类型参数。
 * 输出：无。
 */

    public void markPendingFunction(FunctionRecord record) {
        pendingFunction = record;
    }
/**
 * 函数功能：消费待进入的函数记录。
 * 输入：
 * - 无。
 * 输出：FunctionRecord 类型返回值。
 */

    public FunctionRecord consumePendingFunction() {
        FunctionRecord function = pendingFunction;
        pendingFunction = null;
        return function;
    }
/**
 * 函数功能：进入代码块作用域。
 * 输入：
 * - 无。
 * 输出：无。
 */

    public void scopeIntoBlock() {
        scopeInto();
        FunctionRecord function = consumePendingFunction();
        if (function == null) {
            blockFunctionFlags.push(Boolean.FALSE);
            return;
        }
        enterFunction(function);
        blockFunctionFlags.push(Boolean.TRUE);
        for (FunctionParameter parameter : function.getParameters()) {
            SourceToken nameToken = parameter.getNameToken();
            if (existIdentifier(nameToken)) {
                addError(nameToken.getOffset(), "duplicate identifier declaration is not allowed.");
                throw new CompilerException("duplicate identifier declaration is not allowed.");
            }
            registerIdentifier(parameter.getTypeNode(), parameter.getType(), nameToken, true, null);
        }
    }
/**
 * 函数功能：标记待进入的结构体记录。
 * 输入：
 * - 无。
 * 输出：无。
 */

    public void markPendingStructBody() {
        pendingStructBraceDepth++;
    }
/**
 * 函数功能：消费待进入的结构体记录。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    public boolean consumePendingStructBody() {
        if (pendingStructBraceDepth <= 0) {
            return false;
        }
        pendingStructBraceDepth--;
        return true;
    }
/**
 * 函数功能：退出代码块作用域。
 * 输入：
 * - 无。
 * 输出：IdentifierRecord[] 类型数组。
 */

    public IdentifierRecord[] scopeExistBlock() {
        return scopeExist();
    }
/**
 * 函数功能：结束当前代码块作用域。
 * 输入：
 * - 无。
 * 输出：无。
 */

    public void finishBlockScope() {
        boolean functionBody = !blockFunctionFlags.isEmpty() && blockFunctionFlags.pop();
        if (functionBody) {
            exitFunction();
        }
    }
    // endregion

    // region types
    /**
     * 函数功能：解析字面量类型。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public SemanticType literalType(SourceToken token) {
        return typeResolver.literalType(token);
    }
/**
 * 函数功能：解析类型词法单元。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：SemanticType 类型返回值。
 */

    public SemanticType typeToken(SourceToken token) {
        return typeResolver.typeToken(token);
    }
/**
 * 函数功能：解析整数字面量。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：整数结果。
 */

    public int integerLiteral(SourceToken token) {
        return constantResolver.integerLiteral(token);
    }
/**
 * 函数功能：判断源类型是否可隐式转换为目标类型。
 * 输入：
 * - from：SemanticType 类型参数。
 * - to：SemanticType 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean canImplicitlyConvert(SemanticType from, SemanticType to) {
        return typeConversionRule.canImplicitlyConvert(from, to);
    }
/**
 * 函数功能：判断是否需要隐式类型转换。
 * 输入：
 * - from：SemanticType 类型参数。
 * - to：SemanticType 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean requiresImplicitCast(SemanticType from, SemanticType to) {
        return typeConversionRule.requiresImplicitCast(from, to);
    }
/**
 * 函数功能：获取二元表达式的公共类型。
 * 输入：
 * - left：SemanticType 类型参数。
 * - right：SemanticType 类型参数。
 * 输出：SemanticType 类型返回值。
 */

    public SemanticType commonBinaryType(SemanticType left, SemanticType right) {
        return typeConversionRule.commonBinaryType(left, right);
    }
/**
 * 函数功能：判断结构体是否存在。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean existStruct(SourceToken token) {
        return structContext.exists(token);
    }
/**
 * 函数功能：获取结构体记录。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：StructRecord 类型返回值。
 */

    public StructRecord getStruct(SourceToken token) {
        return structContext.get(token);
    }
/**
 * 函数功能：获取结构体记录。
 * 输入：
 * - type：SemanticType 类型参数。
 * 输出：StructRecord 类型返回值。
 */

    public StructRecord getStruct(SemanticType type) {
        if (type == null || type.getNamedTypeKey() == null) {
            return null;
        }
        return structContext.get(type.getNamedTypeKey());
    }
/**
 * 函数功能：注册结构体记录。
 * 输入：
 * - record：StructRecord 类型参数。
 * 输出：无。
 */

    public void registerStruct(StructRecord record) {
        structContext.register(record);
    }
/**
 * 函数功能：获取结构体表大小。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    public int structTableSize() {
        return structContext.size();
    }
/**
 * 函数功能：获取结构体记录集合。
 * 输入：
 * - 无。
 * 输出：List<StructRecord> 类型集合或迭代结果。
 */

    public List<StructRecord> structRecords() {
        return List.copyOf(structContext.records());
    }
/**
 * 函数功能：要求节点已有声明类型。
 * 输入：
 * - type：SemanticType 类型参数。
 * - anchor：SourceToken 类型参数。
 * - message：String 类型参数。
 * 输出：无。
 */

    public void requireDeclaredType(SemanticType type, SourceToken anchor, String message) {
        if (type != null && type.getNamedTypeKey() != null && getStruct(type) == null) {
            addError(anchor.getOffset(), message);
            throw new CompilerException(message);
        }
    }
/**
 * 函数功能：绑定节点的语义类型。
 * 输入：
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * - register：TypeRegister 类型参数。
 * 输出：无。
 */

    public void bindType(ShiftReduceSyntaxTreeNode node, TypeRegister register) {
        typeContext.bind(node, register);
    }
/**
 * 函数功能：获取节点绑定的语义类型。
 * 输入：
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：TypeRegister 类型返回值。
 */

    public TypeRegister getType(ShiftReduceSyntaxTreeNode node) {
        return typeContext.get(node);
    }
/**
 * 函数功能：判断指定节点是否已有语义类型。
 * 输入：
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean hasType(ShiftReduceSyntaxTreeNode node) {
        return typeContext.has(node);
    }
/**
 * 函数功能：移动节点的类型绑定。
 * 输入：
 * - from：ShiftReduceSyntaxTreeNode 类型参数。
 * - to：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：无。
 */

    public void moveTypeBinding(ShiftReduceSyntaxTreeNode from, ShiftReduceSyntaxTreeNode to) {
        typeContext.move(from, to);
    }

    // endregion

    // region constants
    /**
     * 函数功能：绑定节点的常量值。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * - value：ConstantValue 类型参数。
     * 输出：无。
     */
    public void bindConstantValue(ShiftReduceSyntaxTreeNode node, ConstantValue value) {
        constantValueContext.bind(node, value);
    }
/**
 * 函数功能：获取节点绑定的常量值。
 * 输入：
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：ConstantValue 类型返回值。
 */

    public ConstantValue getConstantValue(ShiftReduceSyntaxTreeNode node) {
        return constantValueContext.get(node);
    }
/**
 * 函数功能：判断指定节点是否已有常量值。
 * 输入：
 * - node：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean hasConstantValue(ShiftReduceSyntaxTreeNode node) {
        return constantValueContext.has(node);
    }

    // endregion
}
