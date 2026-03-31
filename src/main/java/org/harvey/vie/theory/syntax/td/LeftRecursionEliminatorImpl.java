package org.harvey.vie.theory.syntax.td;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.produce.*;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 16:33
 */
@AllArgsConstructor
public class LeftRecursionEliminatorImpl implements LeftRecursionEliminator {
    private final DefineNameFactory factory;

    @Override
    public ProductionSetContext eliminate(ProductionSetContext context) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl();
        for (int i = 0; i < context.length(); i++) {
            GrammarDefineProduction production = context.get(i);
            // 间接左递归变成直接左递归
            GrammarAlternation directiveBody = toDirective(context, production, i);
            // 消除直接左递归
            eliminateDirectRecursion(contextBuilder, production, directiveBody);
        }
        return contextBuilder.build();
    }


    private static GrammarAlternation toDirective(
            ProductionSetContext context, GrammarDefineProduction production, int i) {
        GrammarAlternation productionBody = production.getBody();
        GrammarAlternation directAlternation = new GrammarAlternationImpl();
        for (int j = 0; j < i; j++) {
            // 后面就变成directAlternation了
            productionBody = toDirectiveEach(productionBody, directAlternation, context.get(j));
        }
        return productionBody;
    }

    private static GrammarAlternation toDirectiveEach(
            GrammarAlternation productionBody,
            GrammarAlternation directAlternation,
            GrammarDefineProduction subProduction) {
        for (int i = 0, iEnd = productionBody.size(); i < iEnd; i++) {
            GrammarConcatenation concatenation = concatenation(productionBody.get(i));
            if (concatenation == null) {
                directAlternation.alternateEpsilon();
            } else if (concatenation.get(0) == subProduction.getHead()) { // 间接左递归的可能
                eliminateIndirect(directAlternation, subProduction, concatenation);
            } else {
                directAlternation.alternate(concatenation);
            }
        }
        return directAlternation;
    }

    private static void eliminateIndirect(
            GrammarAlternation directAlternation,
            GrammarDefineProduction subProduction,
            GrammarConcatenation concatenation) {
        for (GrammarSymbol symbol : subProduction.getBody()) {
            GrammarConcatenation subConcatenation = concatenation(symbol);
            if (subConcatenation == null) {
                continue; // 直接忽略
            }
            GrammarConcatenation newConcatenation = new GrammarConcatenationImpl();
            newConcatenation.concatenate(subConcatenation);
            for (int i = 1; i < concatenation.size(); i++) {
                newConcatenation.concatenate(concatenation.get(i));
            }
            directAlternation.alternate(newConcatenation);
        }
    }

    private void eliminateDirectRecursion(
            ProductionSetContextBuilder contextBuilder,
            GrammarDefineProduction production,
            GrammarAlternation directiveBody) {
        HeadDefineSymbol define = production.getDefine();
        String defineName = define.getName();
        GrammarProductionBuilder newDefineBuilder = contextBuilder.define(defineName);
        String intermediaryDefineName = factory.create(defineName);
        GrammarProductionBuilder intermediaryDefineBuilder = contextBuilder.define(intermediaryDefineName);
        for (GrammarSymbol symbol : directiveBody) {
            GrammarConcatenation concatenation = concatenation(symbol);
            if (concatenation == null) { // epsilon
                // 直接加末尾
                newDefineBuilder.alternateDefinition(intermediaryDefineName);
            } else if (concatenation.get(0) == define) { // 左递归了
                eliminateDirectRecursion(intermediaryDefineBuilder, concatenation);
            } else { // 没有左递归
                concatenationNoRecursion(newDefineBuilder, concatenation, intermediaryDefineName);
            }
        }
        intermediaryDefineBuilder.alternateEpsilon();
    }

    private static GrammarConcatenation concatenation(GrammarSymbol symbol) {
        if (symbol.isEpsilon()) {
            return null;
        } else if (!symbol.isConcatenation()) {
            throw new IllegalStateException("Unexpected type: " + symbol.getClass());
        }
        return symbol.toConcatenation();
    }

    private static void concatenationNoRecursion(
            GrammarProductionBuilder newDefineBuilder,
            GrammarConcatenation concatenation,
            String intermediaryDefineName) {
        newDefineBuilder.alternatePlaceholder();
        for (int i = 0; i < concatenation.size(); i++) {
            concatenateLast(newDefineBuilder, concatenation.get(i));
        }
        newDefineBuilder.concatenateDefinitionLast(intermediaryDefineName);
    }


    private static void eliminateDirectRecursion(
            GrammarProductionBuilder intermediaryDefineBuilder, GrammarConcatenation concatenation) {
        intermediaryDefineBuilder.alternatePlaceholder();
        for (int i = 1; i < concatenation.size(); i++) {
            concatenateLast(intermediaryDefineBuilder, concatenation.get(i));
        }
        intermediaryDefineBuilder.concatenateSelfLast();
    }

    private static void concatenateLast(GrammarProductionBuilder builder, ConcatenableSymbol symbol) {
        Objects.requireNonNull(symbol);
        if (symbol.isEpsilon()) {
            builder.alternateEpsilon();
        } else if (symbol.isTerminal()) {
            builder.concatenateTerminalLast(symbol.toTerminal().getValue());
        } else {
            HeadSymbol head = symbol.toHead();
            if (head.isDefine()) {
                builder.concatenateDefinitionLast(head.toDefine().getName());
            } else {
                throw new IllegalStateException("It is not allowed that concatenating with this type: " +
                                                symbol.getClass());
            }
        }
    }
}
