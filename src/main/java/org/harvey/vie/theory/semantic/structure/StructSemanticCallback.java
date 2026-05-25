package org.harvey.vie.theory.semantic.structure;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.sequence.SyntaxTreeListIterator;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeRegister;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers struct declarations and field layouts.
 *
 * @author Temper
 */
public class StructSemanticCallback implements ShiftReduceCallback {
    private final StructFieldStepper fieldStepper = new StructFieldStepper();

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        if (!context.getTreeContext().isEmpty() && context.getTreeContext().peek().isHead()) {
            HeadNode head = context.getTreeContext().peek().toHead();
            if (isStructDeclaration(head)) {
                registerStruct(context, head, production);
            }
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private void registerStruct(ShiftReduceSemanticContext context, HeadNode head, SimpleGrammarProduction production) {
        SourceToken nameToken = head.get(1).toToken().getSource();
        if (context.existStruct(nameToken)) {
            SemanticDiagnostics.reject(context, nameToken, "duplicate struct declaration is not allowed.");
        }
        List<StructField> fields = collectFields(context, head.get(3).toHead());
        StructRecord record = new StructRecord(nameToken, fields, head);
        context.registerStruct(record);
        for (StructField field : fields) {
            context.requireDeclaredType(field.getType(), field.getNameToken(), "struct field type is not declared.");
        }
    }

    private List<StructField> collectFields(ShiftReduceSemanticContext context, HeadNode listHead) {
        List<StructField> fields = new ArrayList<>();
        SyntaxTreeListIterator<HeadNode> iterator = new SyntaxTreeListIterator<>(listHead, fieldStepper);
        int offset = 0;
        while (iterator.hasNext()) {
            HeadNode fieldHead = iterator.next();
            TypeRegister register = context.getType(fieldHead.get(0));
            if (register == null) {
                throw new IllegalStateException("struct field type is missing.");
            }
            SemanticType type = register.requireType("struct field type is required.");
            SourceToken fieldName = fieldHead.get(1).toToken().getSource();
            SemanticDiagnostics.requireNotVoid(context, type, fieldName, "void cannot be used as struct field type.");
            for (StructField field : fields) {
                if (field.isNamed(fieldName)) {
                    SemanticDiagnostics.reject(context, fieldName, "duplicate struct field declaration is not allowed.");
                }
            }
            fields.add(new StructField(fieldName, type, offset++));
        }
        return fields;
    }

    private boolean isStructDeclaration(HeadNode head) {
        return head.getSymbol().isDefine() && "struct_decl".equals(head.getSymbol().toDefine().getName());
    }

    @FunctionalInterface
    private interface ReduceAction {
        ReduceAction NOOP = (context, head, production) -> {
        };

        void accept(ShiftReduceSemanticContext context, HeadNode head, SimpleGrammarProduction production);
    }
}
