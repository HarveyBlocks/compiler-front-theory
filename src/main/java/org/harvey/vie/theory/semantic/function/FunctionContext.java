package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;

import java.util.*;

/**
 * @author Temper
 */
public class FunctionContext {
    private final Map<IdentifierKey, FunctionRecord> functions = new LinkedHashMap<>();
    private final ArrayDeque<FunctionRecord> functionStack = new ArrayDeque<>();
    private final ArrayDeque<FunctionBodyState> bodyStateStack = new ArrayDeque<>();

    /**
     * 函数功能：判断指定对象是否存在。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：判断结果布尔值。
     */

    public boolean exists(SourceToken token) {
        return functions.containsKey(IdentifierKey.generate(token));
    }

    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：FunctionRecord 类型返回值。
     */

    public FunctionRecord get(SourceToken token) {
        return functions.get(IdentifierKey.generate(token));
    }

    /**
     * 函数功能：获取记录集合。
     * 输入：
     * - 无。
     * 输出：Collection<FunctionRecord> 类型集合或迭代结果。
     */

    public Collection<FunctionRecord> records() {
        return functions.values();
    }

    /**
     * 函数功能：注册指定对象。
     * 输入：
     * - record：FunctionRecord 类型参数。
     * 输出：无。
     */

    public void register(FunctionRecord record) {
        functions.put(record.getSignature().getNameKey(), record);
    }

    /**
     * 函数功能：进入新的语义作用域或上下文。
     * 输入：
     * - record：FunctionRecord 类型参数。
     * 输出：无。
     */

    public void enter(FunctionRecord record) {
        functionStack.push(record);
        bodyStateStack.push(new FunctionBodyState(record));
    }

    /**
     * 函数功能：退出当前语义作用域或上下文。
     * 输入：
     * - 无。
     * 输出：FunctionBodyState 类型返回值。
     */

    public FunctionBodyState exit() {
        FunctionBodyState state = bodyStateStack.pop();
        FunctionRecord function = functionStack.pop();
        if (state.getFunction() != function) {
            throw new IllegalStateException("function body state stack is inconsistent");
        }
        return state;
    }

    /**
     * 函数功能：获取当前元素。
     * 输入：
     * - 无。
     * 输出：Optional<FunctionRecord> 类型集合或迭代结果。
     */

    public Optional<FunctionRecord> current() {
        return Optional.ofNullable(functionStack.peek());
    }

    /**
     * 函数功能：判断当前是否位于函数内部。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    public boolean insideFunction() {
        return !functionStack.isEmpty();
    }

    /**
     * 函数功能：判断是否存在函数体状态。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    public boolean hasFunctionBodyState() {
        return !bodyStateStack.isEmpty();
    }

    /**
     * 函数功能：获取当前函数返回类型。
     * 输入：
     * - 无。
     * 输出：SemanticType 类型返回值。
     */

    public SemanticType currentReturnType() {
        FunctionRecord current = functionStack.peek();
        return current == null ? null : current.getSignature().getReturnType();
    }

    /**
     * 函数功能：获取当前函数并要求其存在。
     * 输入：
     * - 无。
     * 输出：FunctionRecord 类型返回值。
     */

    public FunctionRecord requireCurrent() {
        FunctionRecord current = functionStack.peek();
        if (current == null) {
            throw new IllegalStateException("current function is absent");
        }
        return current;
    }

    /**
     * 函数功能：获取当前函数体状态。
     * 输入：
     * - 无。
     * 输出：FunctionBodyState 类型返回值。
     */

    public FunctionBodyState currentBodyState() {
        return bodyStateStack.peek();
    }

}

