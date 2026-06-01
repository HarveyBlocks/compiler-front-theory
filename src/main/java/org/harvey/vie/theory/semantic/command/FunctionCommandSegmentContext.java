package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 函数命令段登记表。
 * <p>
 * 编译一个文件时可能有多个函数定义，{@link org.harvey.vie.theory.semantic.command.translator.command.FunctionDefinitionTranslator}
 * 每遇到一个函数体就调用 {@link #register(FunctionCommandSegment)}。使用 {@link LinkedHashMap}
 * 是为了保留函数出现顺序，报告和测试中的函数表下标也更稳定。
 * <p>
 * 讲完本类回到 {@link SemanticResultCallback}，看函数段如何进入最终结果。
 *
 * @author Temper
 */
public class FunctionCommandSegmentContext {
    private final Map<IdentifierKey, FunctionCommandSegment> segments = new LinkedHashMap<>();

    public void register(FunctionCommandSegment segment) {
        segments.put(segment.getFunction().getNameKey(), segment);
    }

    public FunctionCommandSegment get(IdentifierKey key) {
        return segments.get(key);
    }

    public Collection<FunctionCommandSegment> values() {
        return segments.values();
    }

    public boolean isEmpty() {
        return segments.isEmpty();
    }

    public List<FunctionCommandSegment> snapshot() {
        return List.copyOf(segments.values());
    }
}
