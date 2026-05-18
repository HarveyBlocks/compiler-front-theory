package org.harvey.vie.theory.syntax.grammar.produce;

import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:28
 */
public class GrammarProductionBuilderImpl implements GrammarProductionBuilder {
    private final ProductionSetContextBuilder contextBuilder;

    @Getter
    private final HeadDefineSymbol head;
    private GrammarAlternation body;
    private boolean placeholder;

    public GrammarProductionBuilderImpl(
            HeadDefineSymbol head, ProductionSetContextBuilder contextBuilder) {
        this.head = head;
        this.contextBuilder = contextBuilder;
    }


    @Override
    public GrammarProductionBuilder alternateTerminal(TerminalFactor factor) {
        return alternate(contextBuilder.createTerminal(factor));
    }

    @Override
    public GrammarProductionBuilder alternateDefinition(String definition) {
        return alternate(contextBuilder.define(definition).getHead());
    }

    private GrammarProductionBuilder alternate(GrammarSymbol concatenable) {
        if (body == null) {
            body = new GrammarAlternationImpl();
        }
        if (concatenable == GrammarSymbol.EPSILON) {
            placeholder = false;
            body.alternateEpsilon();
            return this;
        }
        if (placeholder) {
            throw new IllegalStateException("you must do concatenate after alternate placeholder!");
        }
        body.alternate(new GrammarConcatenationImpl());

        concatenate0(body.size() - 1, concatenable);
        return this;
    }

    @Override
    public GrammarProductionBuilder alternateEpsilon() {
        return alternate(GrammarSymbol.EPSILON);
    }

    @Override
    public GrammarProductionBuilder alternateSelf() {
        return alternateDefinition(head.getName());
    }

    @Override
    public GrammarProductionBuilder alternatePlaceholder() {
        if (placeholder) {
            throw new IllegalStateException("placeholder alternated twice");
        }
        this.placeholder = true;
        return this;
    }

    @Override
    public GrammarProductionBuilder concatenateTerminalLast(TerminalFactor factor) {
        return concatenateLast(contextBuilder.createTerminal(factor));
    }

    @Override
    public GrammarProductionBuilder concatenateDefinitionLast(String definition) {
        return concatenateLast(contextBuilder.define(definition).getHead());
    }

    @Override
    public GrammarProductionBuilder concatenateSelfLast() {
        return concatenateDefinitionLast(head.getName());
    }

    private GrammarProductionBuilder concatenateLast(ConcatenableSymbol concatenable) {
        if (body == null) {
            placeholder = false;
            body = new GrammarAlternationImpl();
            body.alternate(new GrammarConcatenationImpl());
        } else if (placeholder) {
            placeholder = false;
            body.alternate(new GrammarConcatenationImpl());
        }
        concatenate0(body.size() - 1, concatenable);
        return this;
    }

    @Override
    public GrammarProductionBuilder concatenateTerminal(int i, TerminalFactor factor) {
        return concatenate(i, contextBuilder.createTerminal(factor));
    }

    @Override
    public GrammarProductionBuilder concatenateDefinition(int i, String definition) {
        return concatenate(i, contextBuilder.define(definition).getHead());
    }

    @Override
    public GrammarProductionBuilder concatenateSelf(int i) {
        return concatenateDefinition(i, head.getName());
    }

    private GrammarProductionBuilder concatenate(int i, ConcatenableSymbol concatenable) {
        if (body == null) {
            if (i != 0) {
                throw new IllegalArgumentException(
                        "The argument of `i` must be zero while the body have not been initialized");
            }
            placeholder = false;
            body = new GrammarAlternationImpl();
            body.alternate(new GrammarConcatenationImpl());
        } else if (placeholder) {
            if (i == body.size()) {
                // good
                placeholder = false;
                body.alternate(new GrammarConcatenationImpl());
            }
        }
        concatenate0(i, concatenable);
        return this;
    }

    private void concatenate0(int i, GrammarSymbol concatenable) {
        AlterableSymbol symbol = body.get(i);
        if (!concatenable.isConcatenable()) {
            throw new IllegalStateException(
                    "Non-ConcatenableSymbols are not allowed to be concatenated to concatenation");
        }
        if (!symbol.isConcatenation()) {
            throw new IllegalStateException("Symbols are not allowed to be concatenated to non-GrammarConcatenation");
        }
        symbol.toConcatenation().concatenate(concatenable.toConcatenable());
    }

    @Override
    public GrammarDefineProduction build() {
        Objects.requireNonNull(head, "require head for build grammar production");
        Objects.requireNonNull(body, "require body for build grammar production");
        if (body.isEmpty()) {
            throw new IllegalStateException("Body have not been defined for " +
                                            head.getName() +
                                            " ! It is not allowed that body is empty. ");
        }
        if (placeholder) {
            throw new IllegalStateException("Body' placeholder have not been concatenated");
        }
        return new GrammarDefineProductionImpl(head, body);
    }


}
