package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.Arrays;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:40
 */
@Slf4j
public class SimpleShrinkTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length == 0) {
            return new PlaceholderNodeRegister();
        } else {
            if (children.length != 1) {
                log.warn(
                        "It is recommended to carry out additional special processing for more complex generative formulas: {}",
                        production
                );
            }
            CommandNodeBuilder listBuilder = new CommandNodeListBuilder();
            Arrays.stream(children).forEach(c -> c.register(listBuilder));
            CommandNode[] childrenNode = listBuilder.build();
            return new NormalCommandNodeRegister(childrenNode, production);
        }
    }


}
