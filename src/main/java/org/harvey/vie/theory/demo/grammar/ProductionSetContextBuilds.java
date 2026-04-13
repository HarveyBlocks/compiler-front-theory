package org.harvey.vie.theory.demo.grammar;

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

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 19:12
 */
public class ProductionSetContextBuilds {

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

    // 消解左递归, 有间接递归
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

    // 消解左相同因子
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

    public static ProductionSetContext build4(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        return classic(contextBuilder);
    }

    private static TokenType of(String hint) {
        return new StringTokenType(hint);
    }

    public static ProductionSetContext build5(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S").alternateDefinition("E");
        return classic(contextBuilder);
    }

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

    @AllArgsConstructor
    static class StringTokenType implements TokenType {
        private final String hint;

        @Override
        public int store(OutputStream os) throws IOException {
            throw new UnsupportedEncodingException("just test");
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public @NonNull String hint() {
            return hint;
        }
    }

    public static ProductionSetContext buildSchoolWork1(TerminalFactory terminalFactory) {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl(terminalFactory);
        contextBuilder.define("S")
                .alternateTerminal(of("0"))
                .concatenateSelfLast()
                .concatenateTerminalLast(of("1"));
        return contextBuilder.build();
    }
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
