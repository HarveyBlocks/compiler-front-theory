package org.harvey.vie.theory.demo.grammar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.syntax.grammar.produce.GrammarProductionBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilderImpl;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 19:12
 */
public class ProductionSetContextBuilds {

    /**
     * 函数功能：构建数字加法演示文法的产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext build1(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        GrammarProductionBuilder itemBuilder = contextBuilder.define("item");
        GrammarProductionBuilder digitBuilder = contextBuilder.define("digit");
        itemBuilder.alternateSelf()
                .concatenateTerminalLast(of("+"))
                .concatenateDefinitionLast("digit")
                .alternateTerminal(of("digit"));
        digitBuilder.alternateTerminal(of("0"))
                .alternateTerminal(of("1"))
                .alternateTerminal(of("2"))
                .alternateTerminal(of("3"))
                .alternateTerminal(of("4"));
        GrammarProductionBuilder digitBuilder2 = contextBuilder.define("digit");
        digitBuilder2.alternateTerminal(of("5"))
                .alternateTerminal(of("6"))
                .alternateTerminal(of("7"))
                .alternateTerminal(of("8"))
                .alternateTerminal(of("9"));
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建包含间接左递归的演示文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext build2(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        GrammarProductionBuilder sBuilder = contextBuilder.define("S");
        GrammarProductionBuilder aBuilder = contextBuilder.define("A");
        sBuilder.alternateDefinition("A").concatenateTerminalLast(of("a")).alternateTerminal(of("b"));
        aBuilder.alternateSelf()
                .concatenateTerminalLast(of("c"))
                .alternateDefinition("S")
                .concatenateTerminalLast(of("d"))
                .alternateEpsilon();
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建包含公共左因子的演示文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext build3(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("B").alternateEpsilon();
        contextBuilder.define("M")
                .alternateTerminal(of("N"))
                .alternateTerminal(of("N"))
                .concatenateTerminalLast(of("O"));
        contextBuilder.define("X")
                .alternateEpsilon()
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("C"))
                .concatenateTerminalLast(of("E"))
                .concatenateTerminalLast(of("F"))
                .concatenateTerminalLast(of("G"))
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("C"))
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .concatenateTerminalLast(of("F"))
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("D"))
                .alternateTerminal(of("A"))
                .concatenateTerminalLast(of("D"))
                .alternateTerminal(of("D"))
                .alternateTerminal(of("A"))
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .alternateTerminal(of("A"))
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .alternateDefinition("B")
                .concatenateTerminalLast(of("A"))
                .concatenateTerminalLast(of("C"))
                .concatenateTerminalLast(of("D"))
                .concatenateTerminalLast(of("E"))
                .alternateDefinition("B")
                .concatenateTerminalLast(of("A"))
                .concatenateTerminalLast(of("C"))
                .alternateEpsilon();
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建经典表达式文法的产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext build4(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        return classic(contextBuilder);
    }

    /**
     * 函数功能：按提示文本获取字符串词法单元类型。
     * 输入：
     * - hint：词法单元的提示文本。
     * 输出：提示文本对应的 TokenType。
     */
    private static TokenType of(String hint) {
        return StringTokenType.of(hint);
    }

    /**
     * 函数功能：构建带开始符号包装的经典表达式文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext build5(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S").alternateDefinition("E");
        return classic(contextBuilder);
    }

    /**
     * 函数功能：向产生式上下文构建器加入经典表达式文法。
     * 输入：
     * - contextBuilder：待填充产生式的上下文构建器。
     * 输出：构建完成的 ProductionSetContext。
     */
    private static ProductionSetContext classic(ProductionSetContextBuilder contextBuilder) {
        contextBuilder.define("E")
                .alternateDefinition("E")
                .concatenateTerminalLast(of("+"))
                .concatenateDefinitionLast("T")
                .alternateDefinition("T");
        contextBuilder.define("T")
                .alternateDefinition("T")
                .concatenateTerminalLast(of("*"))
                .concatenateDefinitionLast("F")
                .alternateDefinition("F");
        contextBuilder.define("F")
                .alternateTerminal(of("("))
                .concatenateDefinitionLast("E")
                .concatenateTerminalLast(of(")"))
                .alternateTerminal(of("id"));
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建包含类型与表达式歧义的演示文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext build6(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        // S -> expr
        // expr -> expr > term
        // expr -> expr < term
        // expr -> term
        // term -> ( type ) factor
        // factor -> ( expr ) | id
        // type -> id < type_list >
        // type_list -> type , type_list
        // type_list -> type
        contextBuilder.define("S").alternateDefinition("expr");
        contextBuilder.define("expr")
                .alternateSelf()
                .concatenateTerminalLast(of(">"))
                .concatenateDefinitionLast("term")
                .alternateSelf()
                .concatenateTerminalLast(of("<"))
                .concatenateDefinitionLast("term")
                .alternateDefinition("term");
        contextBuilder.define("term")
                .alternateTerminal(of("("))
                .concatenateDefinitionLast("type")
                .concatenateTerminalLast(of(")"))
                .concatenateDefinitionLast("factor")
                .alternateDefinition("factor");
        contextBuilder.define("factor")
                .alternateTerminal(of("("))
                .concatenateDefinitionLast("expr")
                .concatenateTerminalLast(of(")"))
                .alternateTerminal(of("id"));
        contextBuilder.define("type")
                .alternateTerminal(of("id"))
                .concatenateTerminalLast(of("<"))
                .concatenateDefinitionLast("type_list")
                .concatenateTerminalLast(of(">"))
                .alternateTerminal(of("id"));
        contextBuilder.define("type_list")
                .alternateSelf()
                .concatenateTerminalLast(of(","))
                .concatenateDefinitionLast("type")
                .alternateDefinition("type");
        return contextBuilder.build();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class StringTokenType implements TokenType {
        private final String hint;
        private static final Map<String, StringTokenType> POOL = new HashMap<>();

        /**
         * 函数功能：按提示文本获取共享的字符串词法单元类型。
         * 输入：
         * - hint：词法单元的提示文本。
         * 输出：提示文本对应的 TokenType。
         */
        public static TokenType of(String hint) {
            return POOL.computeIfAbsent(hint, StringTokenType::new);
        }

        /**
         * 函数功能：序列化字符串词法单元类型。
         * 输入：
         * - os：接收序列化数据的输出流。
         * 输出：写入的字节数。
         */
        @Override
        public int store(OutputStream os) throws IOException {
            throw new UnsupportedEncodingException("just test");
        }

        /**
         * 函数功能：获取字符串词法单元类型的优先级。
         * 输入：
         * - 无。
         * 输出：词法单元优先级整数。
         */
        @Override
        public int getPriority() {
            return 0;
        }

        /**
         * 函数功能：获取字符串词法单元类型的提示文本。
         * 输入：
         * - 无。
         * 输出：词法单元提示字符串。
         */
        @Override
        public @NonNull String hint() {
            return hint;
        }
    }

    /**
     * 函数功能：构建作业题一的文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext buildSchoolWork1(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S")
                .alternateTerminal(of("0"))
                .concatenateSelfLast()
                .concatenateTerminalLast(of("1"));
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建作业题二的文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext buildSchoolWork2(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S")
                .alternateTerminal(of("+"))
                .concatenateSelfLast()
                .concatenateSelfLast()
                .alternateTerminal(of("*"))
                .concatenateSelfLast()
                .concatenateSelfLast()
                .alternateTerminal(of("a"));
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建作业题三的文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext buildSchoolWork3(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S")
                .alternateEpsilon()
                .alternateSelf()
                .concatenateTerminalLast(of("("))
                .concatenateSelfLast()
                .concatenateTerminalLast(of(")"))
                .concatenateSelfLast();
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建作业题四的文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext buildSchoolWork4(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S")
                .alternateSelf()
                .concatenateTerminalLast(of("+"))
                .concatenateSelfLast()
                .alternateSelf()
                .concatenateSelfLast()
                .alternateTerminal(of("("))
                .concatenateSelfLast()
                .concatenateTerminalLast(of(")"))
                .alternateSelf()
                .concatenateTerminalLast(of("*"))
                .alternateTerminal(of("a"));
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建作业题五的文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext buildSchoolWork5(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S")
                .alternateTerminal(of("("))
                .concatenateDefinitionLast("L")
                .concatenateTerminalLast(of(")"))
                .alternateTerminal(of("a"));
        contextBuilder.define("L")
                .alternateSelf()
                .concatenateTerminalLast(of(","))
                .concatenateDefinitionLast("S")
                .alternateDefinition("S");
        return contextBuilder.build();
    }

    /**
     * 函数功能：构建作业题七的布尔表达式文法产生式上下文。
     * 输入：
     * - terminalFactory：用于创建终结符号的工厂。
     * 输出：构建完成的 ProductionSetContext。
     */
    public static ProductionSetContext buildSchoolWork7(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("bexpr")
                .alternateSelf()
                .concatenateTerminalLast(of("or"))
                .concatenateDefinitionLast("bterm")
                .alternateDefinition("bterm");
        contextBuilder.define("bterm")
                .alternateSelf()
                .concatenateTerminalLast(of("and"))
                .concatenateDefinitionLast("bfactor")
                .alternateDefinition("bfactor");
        contextBuilder.define("bfactor")
                .alternateTerminal(of("not"))
                .concatenateSelfLast()
                .alternateTerminal(of("("))
                .concatenateDefinitionLast("bexpr")
                .concatenateTerminalLast(of(")"))
                .alternateTerminal(of("true"))
                .alternateTerminal(of("false"));
        return contextBuilder.build();
    }
}
