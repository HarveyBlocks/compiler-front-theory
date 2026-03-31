package org.harvey.vie.theory.demo;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.syntax.grammar.produce.GrammarProductionBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContextBuilderImpl;
import org.harvey.vie.theory.syntax.td.LeftFactoringEliminator;
import org.harvey.vie.theory.syntax.td.LeftFactoringEliminatorImpl;
import org.harvey.vie.theory.syntax.td.LeftRecursionEliminator;
import org.harvey.vie.theory.syntax.td.LeftRecursionEliminatorImpl;
import org.harvey.vie.theory.syntax.td.first.FirstMap;
import org.harvey.vie.theory.syntax.td.first.FirstMapFactory;
import org.harvey.vie.theory.syntax.td.first.FirstMapFactoryImpl;
import org.harvey.vie.theory.syntax.td.follow.FollowMap;
import org.harvey.vie.theory.syntax.td.follow.FollowSetFactory;
import org.harvey.vie.theory.syntax.td.follow.FollowSetFactoryImpl;
import org.harvey.vie.theory.syntax.td.table.AnalysisTable;
import org.harvey.vie.theory.syntax.td.table.AnalysisTableFactory;
import org.harvey.vie.theory.syntax.td.table.DeterministicAnalysisTableFactory;

/**
 * 词法分析器Demo
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:19
 */
@Slf4j
public class SyntaxDemo {


    public static void main(String[] args) {
        ProductionSetContext context = build4();
        System.out.println(context);
        System.out.println("-----------------------");
        LeftRecursionEliminator leftRecursionEliminator = new LeftRecursionEliminatorImpl(s -> s + '\'');
        LeftFactoringEliminator leftFactoringEliminator = new LeftFactoringEliminatorImpl((s, i) -> s + i);
        ProductionSetContext eliminated = leftFactoringEliminator.eliminate(leftRecursionEliminator.eliminate(context));
        System.out.println(eliminated);
        System.out.println("-----------------------");
        FirstMapFactory firstMapFactory = new FirstMapFactoryImpl();
        FirstMap firstMap = firstMapFactory.first(eliminated);
        firstMap.forEach(System.out::println);
        System.out.println("-----------------------");
        FollowSetFactory followSetFactory = new FollowSetFactoryImpl();
        FollowMap followMap = followSetFactory.follow("expression", eliminated, firstMap);
        followMap.entrySet().forEach(System.out::println);
        System.out.println("-----------------------");
        AnalysisTableFactory tableFactory = new DeterministicAnalysisTableFactory();
        AnalysisTable analysisTable = tableFactory.produce(eliminated, firstMap, followMap);
        System.out.println(analysisTable);
        System.out.println("-----------------------");
    }


    private static ProductionSetContext build1() {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl();
        GrammarProductionBuilder itemBuilder = contextBuilder.define("item");
        GrammarProductionBuilder digitBuilder = contextBuilder.define("digit");
        itemBuilder.alternateSelf()
                .concatenateTerminalLast("+")
                .concatenateDefinitionLast("digit")
                .alternateTerminal("digit");
        digitBuilder.alternateTerminal("0")
                .alternateTerminal("1")
                .alternateTerminal("2")
                .alternateTerminal("3")
                .alternateTerminal("4");
        GrammarProductionBuilder digitBuilder2 = contextBuilder.define("digit");
        digitBuilder2.alternateTerminal("5")
                .alternateTerminal("6")
                .alternateTerminal("7")
                .alternateTerminal("8")
                .alternateTerminal("9");
        return contextBuilder.build();
    }

    // 消解左递归, 有间接递归
    private static ProductionSetContext build2() {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl();
        GrammarProductionBuilder sBuilder = contextBuilder.define("S");
        GrammarProductionBuilder aBuilder = contextBuilder.define("A");
        sBuilder.alternateDefinition("A").concatenateTerminalLast("a").alternateTerminal("b");
        aBuilder.alternateSelf()
                .concatenateTerminalLast("c")
                .alternateDefinition("S")
                .concatenateTerminalLast("d")
                .alternateEpsilon();
        return contextBuilder.build();
    }

    // 消解左相同因子
    private static ProductionSetContext build3() {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl();
        contextBuilder.define("B").alternateEpsilon();
        contextBuilder.define("M").alternateTerminal("N").alternateTerminal("N").concatenateTerminalLast("O");
        contextBuilder.define("X")
                .alternateEpsilon()
                .alternateTerminal("A")
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast("C")
                .concatenateTerminalLast("E")
                .concatenateTerminalLast("F")
                .concatenateTerminalLast("G")
                .alternateTerminal("A")
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast("C")
                .concatenateTerminalLast("D")
                .concatenateTerminalLast("E")
                .concatenateTerminalLast("F")
                .alternateTerminal("A")
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast("D")
                .alternateTerminal("A")
                .concatenateTerminalLast("D")
                .alternateTerminal("D")
                .alternateTerminal("A")
                .concatenateDefinitionLast("B")
                .concatenateTerminalLast("D")
                .concatenateTerminalLast("E")
                .alternateTerminal("A")
                .concatenateTerminalLast("D")
                .concatenateTerminalLast("E")
                .alternateDefinition("B")
                .concatenateTerminalLast("A")
                .concatenateTerminalLast("C")
                .concatenateTerminalLast("D")
                .concatenateTerminalLast("E")
                .alternateDefinition("B")
                .concatenateTerminalLast("A")
                .concatenateTerminalLast("C")
                .alternateEpsilon();
        return contextBuilder.build();
    }

    private static ProductionSetContext build4() {
        ProductionSetContextBuilder contextBuilder = new ProductionSetContextBuilderImpl();
        contextBuilder.define("expression")
                .alternateDefinition("expression")
                .concatenateTerminalLast("+")
                .concatenateDefinitionLast("term")
                .alternateDefinition("term");
        contextBuilder.define("term")
                .alternateDefinition("term")
                .concatenateTerminalLast("*")
                .concatenateDefinitionLast("factor")
                .alternateDefinition("factor");

        contextBuilder.define("factor")
                .alternateTerminal("(")
                .concatenateDefinitionLast("expression")
                .concatenateTerminalLast(")")
                .alternateTerminal("id");
        return contextBuilder.build();
    }
}