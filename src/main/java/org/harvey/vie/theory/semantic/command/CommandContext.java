package org.harvey.vie.theory.semantic.command;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TokenTranslator;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 16:25
 */
public class CommandContext extends Stack<CommandContext.CommandNodeRegister> {

    public interface CommandNodeRegister {
        void register(CommandNodeBuilder outer);
    }

    public interface Label {
        void setIndex(int index);
        int getIndex();
    }

    @Data
    public static class DefaultLabel {
        private int index;
    }

    public interface CommandNode extends IRandomAccess<CommandNode> {
        void flat(java.util.List<SemanticCommand> result);
    }

    @Getter
    public static class HeadNode extends IRandomAccess.ArrayImpl<CommandNode> implements CommandNode {
        private final SimpleGrammarProduction production;

        public HeadNode(CommandNode[] children, SimpleGrammarProduction production) {
            super(children);
            this.production = production;
        }

        @Override
        public void flat(java.util.List<SemanticCommand> result) {
            // 递归
            for (CommandNode child : this) {
                child.flat(result);
            }
        }
    }


    @Getter
    public static class TerminalNode extends IRandomAccess.EmptyImpl<CommandNode> implements CommandNode {
        private final SourceToken token;
        private final SemanticCommand command;

        public TerminalNode(SourceToken token, SemanticCommand command) {
            this.token = token;
            this.command = command;
        }

        @Override
        public void flat(java.util.List<SemanticCommand> result) {
            result.add(command);
        }

    }

    @Getter
    @Setter
    public static class LabelNode  extends IRandomAccess.EmptyImpl<CommandNode> implements CommandNode {
        private final Label label;
        public LabelNode(Label label) {this.label = label;}
        @Override
        public void flat(java.util.List<SemanticCommand> result) {
            label.setIndex(result.size());
        }

    }

    public interface TokenTranslatorStrategy {
        TokenTranslator get(TokenType type);
    }

    public interface CommandTranslatorStrategy {
        CommandTranslator get(int productionId);
    }
}
