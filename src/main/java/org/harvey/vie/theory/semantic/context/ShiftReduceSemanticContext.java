package org.harvey.vie.theory.semantic.context;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.error.SemanticErrorMessage;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierTableBuilder;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.TreeContext;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaseContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;

import java.util.Iterator;
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
    @Setter
    private SemanticResult result;
    private final ShiftReduceCallbackRegister register;
    private final ShiftReducePhaseContext syntaxContext;
    private Iterator<ShiftReduceCallback> callbackIter;
    @Getter
    private final TreeContext treeContext = new TreeContext();
    @Getter
    private final CommandContext commandContext = new CommandContext();
    private final IdentifierTableBuilder identifierTableBuilder = new IdentifierTableBuilder();

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

    // region error context
    public void addError(int offset, String message) {
        syntaxContext.getErrorContext().addError(new SemanticErrorMessage(offset,message));
    }
    // endregion

    // region identifier table builder
    public boolean existIdentifier(SourceToken identifierToken) {
        return identifierTableBuilder.existIdentifier(identifierToken);
    }

    public IdentifierRecord getIdentifier(SourceToken identifierToken) {
        return identifierTableBuilder.getIdentifier(identifierToken);
    }

    public void registerIdentifier(HeadNode typeHeadNode, SourceToken identifierToken, boolean initialized) {
        identifierTableBuilder.registerIdentifier(typeHeadNode, identifierToken, initialized);
    }


    public void scopeInto() {
        identifierTableBuilder.scopeInto();
    }

    public IdentifierRecord[] scopeExist() {
        return identifierTableBuilder.scopeExist();
    }

    // endregion
}
