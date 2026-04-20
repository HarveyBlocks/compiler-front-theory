package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.CommandNodeListBuilder;
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
public class SimpleMapTranslator implements CommandTranslator {
    @Override
    public CommandContext.CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandContext.CommandNodeRegister[] children) {
        if (children.length == 0) {
            return new Register(new CommandContext.CommandNode[0], production);
        } else {
            if (children.length != 1) {
                log.warn(
                        "It is recommended to carry out additional special processing for more complex generative formulas: {}",
                        production
                );
            }
            CommandNodeListBuilder listBuilder = new CommandNodeListBuilder();
            Arrays.stream(children).forEach(c -> c.register(listBuilder));
            CommandContext.CommandNode[] childrenNode = listBuilder.toArray();
            return new Register(childrenNode, production);
        }
    }

    @AllArgsConstructor
    public static class Register implements CommandContext.CommandNodeRegister {
        private final CommandContext.CommandNode[] childrenNode;
        private final SimpleGrammarProduction production;

        @Override
        public void register(CommandNodeBuilder outer) {
            outer.add(new CommandContext.HeadNode(childrenNode, production));
        }
    }


}
