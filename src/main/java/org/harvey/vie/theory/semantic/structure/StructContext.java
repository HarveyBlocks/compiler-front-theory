package org.harvey.vie.theory.semantic.structure;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry of declared struct types.
 *
 * @author Temper
 */
public class StructContext {
    private final Map<IdentifierKey, StructRecord> records = new LinkedHashMap<>();

    /**
     * 函数功能：判断指定对象是否存在。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：判断结果布尔值。
     */

    public boolean exists(SourceToken token) {
        return exists(IdentifierKey.generate(token));
    }

    /**
     * 函数功能：判断指定对象是否存在。
     * 输入：
     * - key：IdentifierKey 类型参数。
     * 输出：判断结果布尔值。
     */

    public boolean exists(IdentifierKey key) {
        return records.containsKey(key);
    }

    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：StructRecord 类型返回值。
     */

    public StructRecord get(SourceToken token) {
        return get(IdentifierKey.generate(token));
    }

    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - key：IdentifierKey 类型参数。
     * 输出：StructRecord 类型返回值。
     */

    public StructRecord get(IdentifierKey key) {
        return records.get(key);
    }

    /**
     * 函数功能：注册指定对象。
     * 输入：
     * - record：StructRecord 类型参数。
     * 输出：无。
     */

    public void register(StructRecord record) {
        records.put(record.getNameKey(), record);
    }

    /**
     * 函数功能：获取元素数量。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    public int size() {
        return records.size();
    }

    /**
     * 函数功能：获取记录集合。
     * 输入：
     * - 无。
     * 输出：Collection<StructRecord> 类型集合或迭代结果。
     */

    public Collection<StructRecord> records() {
        return records.values();
    }
}
