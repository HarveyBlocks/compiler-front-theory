package org.harvey.vie.theory.semantic.context;

import lombok.Getter;
import org.harvey.vie.theory.error.SemanticErrorMessage;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeConversionRule;
import org.harvey.vie.theory.semantic.type.TypeResolver;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.function.FunctionBodyState;
import org.harvey.vie.theory.semantic.function.FunctionContext;
import org.harvey.vie.theory.semantic.function.FunctionParameter;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierTableBuilder;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.tree.node.TreeContext;
import org.harvey.vie.theory.semantic.type.TypeContext;
import org.harvey.vie.theory.semantic.type.TypeRegister;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.semantic.value.ConstantValueContext;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaseContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;

import java.util.ArrayList;
import java.util.ArrayDeque;
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
    private SemanticResult result;
    private final ShiftReduceCallbackRegister register;
    private final ShiftReducePhaseContext syntaxContext;
    private Iterator<ShiftReduceCallback> callbackIter;
    @Getter
    private final TreeContext treeContext = new TreeContext();
    @Getter
    private final CommandContext commandContext = new CommandContext();
    @Getter
    private final TypeContext typeContext = new TypeContext();
    @Getter
    private final ConstantValueContext constantValueContext = new ConstantValueContext();
    private final TypeResolver typeResolver = new TypeResolver();
    private final TypeConversionRule typeConversionRule = new TypeConversionRule();
    private final IdentifierTableBuilder identifierTableBuilder = new IdentifierTableBuilder();
    private final List<IdentifierRecord> identifierRecords = new ArrayList<>();
    private final FunctionContext functionContext = new FunctionContext();
    private final ArrayDeque<Boolean> blockFunctionFlags = new ArrayDeque<>();
    private FunctionRecord pendingFunction;

    public ShiftReduceSemanticContext(ShiftReduceCallbackRegister register, ShiftReducePhaseContext syntaxContext) {
        this.register = register;
        this.syntaxContext = syntaxContext;
        callbackIter = register.iterator();
    }

    // region callback
    private void popStatus(AlterableSymbol body) {
        int k = body.isEpsilon() ? 0 : body.toConcatenation().size();
        while (k-- > 0) {
            if (syntaxContext.isStackEmpty()) {
                throw new CompilerException("no more status in stack to be pop while reducing");
            }
            syntaxContext.pop();
        }
    }

    private void invokeBeforeAccept(SimpleGrammarProduction production) {
        // accept 是特殊的 reduce
        AlterableSymbol body = production.getBody();
        popStatus(body);
    }

    private void invokeReduce(SimpleGrammarProduction production) {
        // 输入指针不动(归约不消耗输入符号)
        AlterableSymbol body = production.getBody();
        popStatus(body);
        int top = syntaxContext.peek();
        int next = syntaxContext.gotoNext(top, production.getHead());
        syntaxContext.push(next);
    }

    private void invokeShift(int nextStatus) {
        syntaxContext.push(nextStatus);
        syntaxContext.consumeCurrentToken();
    }

    public void onStart() {
        invokeNext(c -> c.onStart(this), this::invokeNothing);
    }

    private void invokeNext(Consumer<ShiftReduceCallback> consumer, Runnable invoker) {
        if (callbackIter.hasNext()) {
            consumer.accept(callbackIter.next());
        } else {
            invoker.run();
            callbackIter = register.iterator();
        }
    }

    private void invokeNothing() {}

    public void onError(ShiftReduceErrorType errorType) {
        invokeNext(c -> c.onError(this, errorType), this::invokeNothing);
    }

    public void onAccept(SimpleGrammarProduction production) {
        invokeNext(c -> c.onAccept(this, production), this::invokeNothing);
    }

    public void onShift(int nextStatus, SourceToken token) {
        invokeNext(c -> c.onShift(this, nextStatus, token), () -> invokeShift(nextStatus));
    }

    public void onReduce(SimpleGrammarProduction production) {
        invokeNext(c -> c.onReduce(this, production), () -> invokeReduce(production));
    }

    public void beforeAccept(SimpleGrammarProduction production) {
        invokeNext(c -> c.beforeAccept(this, production), () -> invokeBeforeAccept(production));
    }
    // endregion

    public void setResult(SemanticResult result) {
        this.result = result;
    }

    public SemanticResult getResult() {
        return result;
    }

    // region error context
    public void addError(int offset, String message) {
        syntaxContext.getErrorContext().addError(new SemanticErrorMessage(offset, message));
    }
    // endregion

    // region identifier table builder
    public boolean existIdentifier(SourceToken identifierToken) {
        return identifierTableBuilder.existIdentifier(identifierToken);
    }

    public IdentifierRecord getIdentifier(SourceToken identifierToken) {
        return identifierTableBuilder.getIdentifier(identifierToken);
    }

    public void updateIdentifierConstant(SourceToken identifierToken, ConstantValue constantValue) {
        IdentifierRecord record = getIdentifier(identifierToken);
        if (record != null) {
            record.setConstantValue(constantValue);
        }
    }

    public IdentifierRecord getIdentifierByNo(int no) {
        for (IdentifierRecord identifierRecord : identifierRecords) {
            if (identifierRecord.getNo() == no) {
                return identifierRecord;
            }
        }
        return null;
    }

    public void registerIdentifier(
            HeadNode typeHeadNode,
            SemanticType declaredType,
            SourceToken identifierToken,
            boolean initialized,
            ConstantValue constantValue) {
        identifierRecords.add(identifierTableBuilder.registerIdentifier(
                typeHeadNode,
                declaredType,
                identifierToken,
                initialized,
                constantValue
        ));
    }


    public void scopeInto() {
        identifierTableBuilder.scopeInto();
    }

    public IdentifierRecord[] scopeExist() {
        return identifierTableBuilder.scopeExist();
    }

    public IdentifierRecord[] identifierRecords() {
        return identifierRecords.toArray(IdentifierRecord[]::new);
    }
    // endregion

    // region functions
    public boolean existFunction(SourceToken nameToken) {
        return functionContext.exists(nameToken);
    }

    public FunctionRecord getFunction(SourceToken nameToken) {
        return functionContext.get(nameToken);
    }

    public void registerFunction(FunctionRecord record) {
        functionContext.register(record);
    }

    public void registerCurrentFunction(FunctionRecord record) {
        functionContext.register(record);
        functionContext.enter(record);
    }

    public void enterFunction(FunctionRecord record) {
        functionContext.enter(record);
    }

    public FunctionRecord exitFunction() {
        return functionContext.exit().getFunction();
    }

    public boolean insideFunction() {
        return functionContext.insideFunction();
    }

    public SemanticType currentFunctionReturnType() {
        return functionContext.currentReturnType();
    }

    public FunctionRecord currentFunction() {
        return functionContext.requireCurrent();
    }

    public FunctionBodyState currentFunctionBodyState() {
        return functionContext.currentBodyState();
    }

    public boolean isCurrentBlockFunctionBody() {
        return !blockFunctionFlags.isEmpty() && blockFunctionFlags.peek();
    }

    public void markPendingFunction(FunctionRecord record) {
        pendingFunction = record;
    }

    public FunctionRecord consumePendingFunction() {
        FunctionRecord function = pendingFunction;
        pendingFunction = null;
        return function;
    }

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

    public IdentifierRecord[] scopeExistBlock() {
        return scopeExist();
    }

    public void finishBlockScope() {
        boolean functionBody = !blockFunctionFlags.isEmpty() && blockFunctionFlags.pop();
        if (functionBody) {
            exitFunction();
        }
    }
    // endregion

    // region types
    public SemanticType literalType(SourceToken token) {
        return typeResolver.literalType(token);
    }

    public SemanticType typeToken(SourceToken token) {
        return typeResolver.typeToken(token);
    }

    public int integerLiteral(SourceToken token) {
        return typeResolver.integerLiteral(token);
    }

    public boolean canImplicitlyConvert(SemanticType from, SemanticType to) {
        return typeConversionRule.canImplicitlyConvert(from, to);
    }

    public boolean requiresImplicitCast(SemanticType from, SemanticType to) {
        return typeConversionRule.requiresImplicitCast(from, to);
    }

    public SemanticType commonBinaryType(SemanticType left, SemanticType right) {
        return typeConversionRule.commonBinaryType(left, right);
    }

    public void bindType(ShiftReduceSyntaxTreeNode node, TypeRegister register) {
        typeContext.bind(node, register);
    }

    public TypeRegister getType(ShiftReduceSyntaxTreeNode node) {
        return typeContext.get(node);
    }

    public boolean hasType(ShiftReduceSyntaxTreeNode node) {
        return typeContext.has(node);
    }

    public void moveTypeBinding(ShiftReduceSyntaxTreeNode from, ShiftReduceSyntaxTreeNode to) {
        typeContext.move(from, to);
    }

    // endregion

    // region constants
    public void bindConstantValue(ShiftReduceSyntaxTreeNode node, ConstantValue value) {
        constantValueContext.bind(node, value);
    }

    public ConstantValue getConstantValue(ShiftReduceSyntaxTreeNode node) {
        return constantValueContext.get(node);
    }

    public boolean hasConstantValue(ShiftReduceSyntaxTreeNode node) {
        return constantValueContext.has(node);
    }
    // endregion
}
