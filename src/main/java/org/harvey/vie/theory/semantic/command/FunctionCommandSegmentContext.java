package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Temper
 */
public class FunctionCommandSegmentContext {
    private final Map<IdentifierKey, FunctionCommandSegment> segments = new LinkedHashMap<>();
/**
 * 函数功能：注册指定对象。
 * 输入：
 * - segment：FunctionCommandSegment 类型参数。
 * 输出：无。
 */

    public void register(FunctionCommandSegment segment) {
        segments.put(segment.getFunction().getNameKey(), segment);
    }
/**
 * 函数功能：获取指定键或索引对应的对象。
 * 输入：
 * - key：IdentifierKey 类型参数。
 * 输出：FunctionCommandSegment 类型返回值。
 */

    public FunctionCommandSegment get(IdentifierKey key) {
        return segments.get(key);
    }
/**
 * 函数功能：获取值集合。
 * 输入：
 * - 无。
 * 输出：Collection<FunctionCommandSegment> 类型集合或迭代结果。
 */

    public Collection<FunctionCommandSegment> values() {
        return segments.values();
    }
/**
 * 函数功能：判断当前对象是否为空。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    public boolean isEmpty() {
        return segments.isEmpty();
    }
/**
 * 函数功能：生成函数命令片段快照。
 * 输入：
 * - 无。
 * 输出：List<FunctionCommandSegment> 类型集合或迭代结果。
 */

    public List<FunctionCommandSegment> snapshot() {
        return List.copyOf(segments.values());
    }
}
