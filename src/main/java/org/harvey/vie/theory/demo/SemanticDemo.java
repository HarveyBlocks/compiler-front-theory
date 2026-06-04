package org.harvey.vie.theory.demo;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.demo.semantic.callable.TreeBuilderPredictiveCallback;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.callback.bu.*;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegisterImpl;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.command.SemanticCommandPrintCallback;
import org.harvey.vie.theory.semantic.command.SemanticResultCallback;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.TokenTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.SimpleShrinkTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.*;
import org.harvey.vie.theory.semantic.error.PassiveErrorCallback;
import org.harvey.vie.theory.semantic.function.FunctionSemanticCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierScopeCallback;
import org.harvey.vie.theory.semantic.identifier.IdentifierTableBuildCallback;
import org.harvey.vie.theory.semantic.log.TreeLogCallback;
import org.harvey.vie.theory.semantic.structure.StructSemanticCallback;
import org.harvey.vie.theory.semantic.tag.TagReducePredicateFactory;
import org.harvey.vie.theory.semantic.tag.TagStrategyCompose;
import org.harvey.vie.theory.semantic.tree.TreeBuildCallback;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.TypeBuildCallback;
import org.harvey.vie.theory.semantic.value.ConstantValueBuildCallback;
import org.harvey.vie.theory.semantic.value.IdentifierConstantStateCallback;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Temper
 */
public class SemanticDemo {
    /**
     * 函数功能：构建仅包含基础语义回调的移进归约回调注册器。
     * 输入：
     * - 无。
     * 输出：配置完成的 ShiftReduceCallbackRegister 注册器。
     */
    public static ShiftReduceCallbackRegister buildSimpleShiftReduceRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(new TreeLogCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    /**
     * 函数功能：构建完整语义分析使用的移进归约回调注册器。
     * 输入：
     * - 无。
     * 输出：配置完成的 ShiftReduceCallbackRegister 注册器。
     */
    public static ShiftReduceCallbackRegister buildShiftReduceRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(new TreeLogCallback());
        register.add(instanceIdentifierScopeCallback());
        register.add(new TypeBuildCallback());
        register.add(new ConstantValueBuildCallback());
        register.add(new StructSemanticCallback());
        register.add(new FunctionSemanticCallback());
        register.add(instanceIdentifierTableBuildCallback());
        register.add(instanceSemanticCommandPrintCallback());
        register.add(instanceSyntaxDirectedTranslationCallback());
        register.add(new IdentifierConstantStateCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    /**
     * 函数功能：构建语义测试使用的移进归约回调注册器。
     * 输入：
     * - 无。
     * 输出：配置完成的 ShiftReduceCallbackRegister 注册器。
     */
    public static ShiftReduceCallbackRegister buildShiftReduceTestRegister() {
        ShiftReduceCallbackRegister register = new ShiftReduceCallbackRegisterImpl();
        register.add(new TreeBuildCallback());
        register.add(instanceIdentifierScopeCallback());
        register.add(new TypeBuildCallback());
        register.add(new ConstantValueBuildCallback());
        register.add(new StructSemanticCallback());
        register.add(new FunctionSemanticCallback());
        register.add(instanceIdentifierTableBuildCallback());
        register.add(new SemanticResultCallback());
        register.add(instanceSyntaxDirectedTranslationCallback());
        register.add(new IdentifierConstantStateCallback());
        register.add(new PassiveErrorCallback());
        return register;
    }

    static final TokenTranslator defaultTokenTranslator = new DoNothingTokenTranslator();
    static final CommandTranslator defaultCommandTranslator = new SimpleShrinkTranslator();

    /**
     * 函数功能：创建语法制导翻译回调。
     * 输入：
     * - 无。
     * 输出：用于构建语义命令的 ShiftReduceCallback 回调。
     */
    private static ShiftReduceCallback instanceSyntaxDirectedTranslationCallback() {
        return new CommandBuildCallback(shiftStrategies(), reduceStrategies0());
    }

    /**
     * 函数功能：创建移进阶段的词法单元翻译策略。
     * 输入：
     * - 无。
     * 输出：按词法单元类型选择翻译器的 TokenTranslatorStrategy。
     */
    private static TokenTranslatorStrategy shiftStrategies() {
        Map<TokenType, TokenTranslator> shiftStrategies = new HashMap<>();
        TokenTranslator loadIdentifierAddressTokenTranslator = new LoadIdentifierAddressTokenTranslator();
        shiftStrategies.put(ProgramTokenType.IDENTIFIER, loadIdentifierAddressTokenTranslator);
        TokenTranslator simpleStringTokenTranslator = new SimpleStringTokenTranslator();
        shiftStrategies.put(ProgramTokenType.CONSTANT_STRING, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_CHARACTER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_INTEGER, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_FLOAT, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_TRUE, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_BOOLEAN_FALSE, simpleStringTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONSTANT_NULL, simpleStringTokenTranslator);
        TokenTranslator typeTokenTranslator = new TypeTokenTranslator();
        shiftStrategies.put(ProgramTokenType.TYPE_BOOLEAN, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_CHARACTER, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_INT32, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_FLOAT64, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_STRING, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_VOID, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.TYPE_IDENTIFIER, typeTokenTranslator);
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_BREAK, new BreakTokenTranslator());
        shiftStrategies.put(ProgramTokenType.CONTROL_STRUCTURES_CONTINUE, new ContinueTokenTranslator());
        return t -> shiftStrategies.getOrDefault(t, defaultTokenTranslator);
    }

    /**
     * 函数功能：创建归约阶段的命令翻译策略。
     * 输入：
     * - 无。
     * 输出：按语义标签选择命令翻译器的 CommandTranslatorStrategy。
     */
    private static CommandTranslatorStrategy reduceStrategies0() {
        return TagStrategyCompose.preciseStringCommand();
    }

    /**
     * 函数功能：创建语义命令打印回调。
     * 输入：
     * - 无。
     * 输出：用于打印语义命令的 ShiftReduceCallback 回调。
     */
    private static ShiftReduceCallback instanceSemanticCommandPrintCallback() {
        return new SemanticCommandPrintCallback();
    }

    /**
     * 函数功能：创建标识符作用域维护回调。
     * 输入：
     * - 无。
     * 输出：用于维护作用域的 ShiftReduceCallback 回调。
     */
    private static ShiftReduceCallback instanceIdentifierScopeCallback() {
        ShiftPredicate scopeIntoPredicate = t -> t.getType() == ProgramTokenType.OPERATOR_BRACE_OPEN;
        ReducePredicate scopeExistPredicate = TagReducePredicateFactory.predicate(
                ProgramSemanticTag.BLOCK,
                ProgramSemanticTag.COMMAND
        );
        return new IdentifierScopeCallback(scopeIntoPredicate, scopeExistPredicate);
    }

    /**
     * 函数功能：创建标识符表构建回调。
     * 输入：
     * - 无。
     * 输出：用于构建标识符表的 ShiftReduceCallback 回调。
     */
    private static ShiftReduceCallback instanceIdentifierTableBuildCallback() {
        ReducePredicate usingPredicate = TagReducePredicateFactory.predicate(
                ProgramSemanticTag.IDENTIFIER,
                ProgramSemanticTag.USE
        );
        ReducePredicate declaringPredicate = production ->
                production.matchTags(ProgramSemanticTag.DECLARATION);
        IdentifierTableBuildCallback.UsingIdentifierSupplier usingIdentifierSupplier =
                usingIdentifierReducedNode -> usingIdentifierReducedNode.get(0).toToken().getSource();

        IdentifierTableBuildCallback.DeclarationRecordSupplier declarationRecordSupplier =
                new IdentifierTableBuildCallback.DeclarationRecordSupplier() {
                    /**
                     * 函数功能：从声明归约节点中取得被声明的标识符。
                     * 输入：
                     * - declarationReducedNode：声明产生式归约后的语法树头节点。
                     * 输出：声明标识符对应的 SourceToken。
                     */
                    @Override
                    public SourceToken identifier(HeadNode declarationReducedNode) {
                        return declarationReducedNode.get(1).toToken().getSource();
                    }

                    /**
                     * 函数功能：判断声明归约节点是否包含初始化信息。
                     * 输入：
                     * - declarationReducedNode：声明产生式归约后的语法树头节点。
                     * 输出：是否已初始化的布尔值。
                     */
                    @Override
                    public boolean initialized(HeadNode declarationReducedNode) {
                        return declarationReducedNode.containsTag(ProgramSemanticTag.INITIALIZED);
                    }

                    /**
                     * 函数功能：从声明归约节点中取得类型节点。
                     * 输入：
                     * - declarationReducedNode：声明产生式归约后的语法树头节点。
                     * 输出：声明类型对应的 HeadNode。
                     */
                    @Override
                    public HeadNode typeHeadNode(HeadNode declarationReducedNode) {
                        return declarationReducedNode.get(0).toHead();
                    }

                    /**
                     * 函数功能：从声明归约节点中取得初始化常量值。
                     * 输入：
                     * - context：当前移进归约语义上下文。
                     * - declarationReducedNode：声明产生式归约后的语法树头节点。
                     * 输出：初始化常量值；无初始化时返回 null。
                     */
                    @Override
                    public org.harvey.vie.theory.semantic.value.ConstantValue initializerValue(
                            org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext context,
                            HeadNode declarationReducedNode) {
                        if (!declarationReducedNode.containsTag(ProgramSemanticTag.INITIALIZED)) {
                            return null;
                        }
                        return context.getConstantValue(declarationReducedNode.get(3).toHead());
                    }
                };
        return new IdentifierTableBuildCallback(
                usingPredicate,
                declaringPredicate,
                usingIdentifierSupplier,
                declarationRecordSupplier
        );
    }

    /**
     * 函数功能：构建预测分析使用的回调注册器。
     * 输入：
     * - 无。
     * 输出：配置完成的 PredictiveCallbackRegister 注册器。
     */
    public static PredictiveCallbackRegister buildPredicativeRegister() {
        TreeBuilderPredictiveCallback callback = new TreeBuilderPredictiveCallback(LexicalConflictResolver.passive());
        PredictiveCallbackRegister register = new PredictiveCallbackRegisterImpl();
        register.add(callback);
        return register;
    }
}

