package org.harvey.vie.theory.syntax.grammar.produce;

import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

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
/**
 * 函数功能：创建 GrammarProductionBuilderImpl 对象。
 * 输入：
 * - head：HeadDefineSymbol 类型参数。
 * - contextBuilder：ProductionSetContextBuilder 类型参数。
 * 输出：无。
 */

    public GrammarProductionBuilderImpl(
            HeadDefineSymbol head, ProductionSetContextBuilder contextBuilder) {
        this.head = head;
        this.contextBuilder = contextBuilder;
    }
/**
 * 函数功能：添加终结符候选产生式。
 * 输入：
 * - factor：TerminalFactor 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */


    @Override
    public GrammarProductionBuilder alternateTerminal(TerminalFactor factor) {
        return alternate(contextBuilder.createTerminal(factor));
    }
/**
 * 函数功能：添加非终结符候选产生式。
 * 输入：
 * - definition：String 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder alternateDefinition(String definition) {
        return alternate(contextBuilder.define(definition).getHead());
    }
/**
 * 函数功能：添加候选语法符号。
 * 输入：
 * - concatenable：GrammarSymbol 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    private GrammarProductionBuilder alternate(GrammarSymbol concatenable) {
        if (body == null) {
            body = new GrammarAlternationImpl();
        }
        if (concatenable.isEpsilon()) {
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
/**
 * 函数功能：添加空串候选产生式。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder alternateEpsilon() {
        return alternate(GrammarSymbol.epsilon());
    }
/**
 * 函数功能：添加自身候选产生式。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder alternateSelf() {
        return alternateDefinition(head.getName());
    }
/**
 * 函数功能：添加占位候选产生式。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder alternatePlaceholder() {
        if (placeholder) {
            throw new IllegalStateException("placeholder alternated twice");
        }
        this.placeholder = true;
        return this;
    }
/**
 * 函数功能：在最后一个候选式末尾连接终结符。
 * 输入：
 * - factor：TerminalFactor 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder concatenateTerminalLast(TerminalFactor factor) {
        return concatenateLast(contextBuilder.createTerminal(factor));
    }
/**
 * 函数功能：在最后一个候选式末尾连接非终结符。
 * 输入：
 * - definition：String 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder concatenateDefinitionLast(String definition) {
        return concatenateLast(contextBuilder.define(definition).getHead());
    }
/**
 * 函数功能：在最后一个候选式末尾连接自身符号。
 * 输入：
 * - 无。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder concatenateSelfLast() {
        return concatenateDefinitionLast(head.getName());
    }
/**
 * 函数功能：在最后一个候选式末尾连接语法符号。
 * 输入：
 * - concatenable：ConcatenableSymbol 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

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
/**
 * 函数功能：在指定候选式中连接终结符。
 * 输入：
 * - i：int 类型参数。
 * - factor：TerminalFactor 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder concatenateTerminal(int i, TerminalFactor factor) {
        return concatenate(i, contextBuilder.createTerminal(factor));
    }
/**
 * 函数功能：在指定候选式中连接非终结符。
 * 输入：
 * - i：int 类型参数。
 * - definition：String 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder concatenateDefinition(int i, String definition) {
        return concatenate(i, contextBuilder.define(definition).getHead());
    }
/**
 * 函数功能：在指定候选式中连接自身符号。
 * 输入：
 * - i：int 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder concatenateSelf(int i) {
        return concatenateDefinition(i, head.getName());
    }
/**
 * 函数功能：连接语法符号。
 * 输入：
 * - i：int 类型参数。
 * - concatenable：ConcatenableSymbol 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    private GrammarProductionBuilder concatenate(int i, ConcatenableSymbol concatenable) {
        initLastAlternation(i);
        concatenate0(i, concatenable);
        return this;
    }
/**
 * 函数功能：初始化指定候选式。
 * 输入：
 * - i：int 类型参数。
 * 输出：无。
 */

    private void initLastAlternation(int i) {
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
    }
/**
 * 函数功能：执行底层语法符号连接。
 * 输入：
 * - i：int 类型参数。
 * - concatenable：GrammarSymbol 类型参数。
 * 输出：无。
 */


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
/**
 * 函数功能：为最后一个候选式添加语义标签。
 * 输入：
 * - tag：SemanticTag... 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder tagLast(SemanticTag... tag) {
        return tag(body.size() - 1, tag);
    }
/**
 * 函数功能：为指定候选式添加语义标签。
 * 输入：
 * - i：int 类型参数。
 * - tag：SemanticTag... 类型参数。
 * 输出：GrammarProductionBuilder 类型返回值。
 */

    @Override
    public GrammarProductionBuilder tag(int i, SemanticTag... tag) {
        if (tag == null || tag.length == 0) {
            return this;
        }
        initLastAlternation(i);
        AlterableSymbol symbol = body.get(i);
        symbol.addTag(tag);
        return this;
    }
/**
 * 函数功能：构建目标对象。
 * 输入：
 * - 无。
 * 输出：GrammarDefineProduction 类型返回值。
 */

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
