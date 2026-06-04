package org.harvey.vie.theory.semantic.identifier.table;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:05
 */
@Slf4j
public class IdentifierTableBuilder {

    private final List<ScopeBuilder> identifierTable = new ArrayList<>();
    private final IdGenerator recordIdGenerator = new IdGenerator();

    /**
     * 函数功能：创建 IdentifierTableBuilder 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */

    public IdentifierTableBuilder() {
        identifierTable.add(new ScopeBuilder(0));
    }

    /**
     * 函数功能：判断标识符是否存在。
     * 输入：
     * - identifierToken：SourceToken 类型参数。
     * 输出：判断结果布尔值。
     */

    public boolean existIdentifier(SourceToken identifierToken) {
        return getIdentifier(identifierToken) != null;
    }

    /**
     * 函数功能：获取标识符记录。
     * 输入：
     * - identifierToken：SourceToken 类型参数。
     * 输出：IdentifierRecord 类型返回值。
     */

    public IdentifierRecord getIdentifier(SourceToken identifierToken) {
        IdentifierKey identifierKey = IdentifierKey.generate(identifierToken);
        for (int i = identifierTable.size() - 1; i >= 0; i--) {
            IdentifierRecord identifierRecord = identifierTable.get(i).get(identifierKey);
            if (identifierRecord != null) {
                return identifierRecord;
            }
        }
        return null;
    }

    /**
     * 函数功能：注册标识符记录。
     * 输入：
     * - typeHeadNode：HeadNode 类型参数。
     * - declaredType：SemanticType 类型参数。
     * - identifierToken：SourceToken 类型参数。
     * - initialized：boolean 类型参数。
     * - ownerFunction：FunctionRecord 类型参数。
     * - constantValue：ConstantValue 类型参数。
     * 输出：IdentifierRecord 类型返回值。
     */

    public IdentifierRecord registerIdentifier(
            HeadNode typeHeadNode,
            SemanticType declaredType,
            SourceToken identifierToken,
            boolean initialized,
            FunctionRecord ownerFunction,
            ConstantValue constantValue) {
        ScopeBuilder last = getLast();
        int no = recordIdGenerator.next();
        int offset = last.nextNo();
        IdentifierRecord identifierRecord = new IdentifierRecord(
                no,
                offset,
                typeHeadNode,
                declaredType,
                identifierToken.getLexeme(),
                initialized,
                ownerFunction,
                constantValue
        );
        last.put(IdentifierKey.generate(identifierToken), identifierRecord);
        log.trace("register identifier: " + identifierRecord.displayString());
        log.trace("now identifier table: \n" +
                  identifierTable.stream().map(r -> "\t" + r).collect(Collectors.joining("\n")));
        return identifierRecord;
    }

    /**
     * 函数功能：进入新的作用域。
     * 输入：
     * - 无。
     * 输出：无。
     */


    public void scopeInto() {
        log.trace("scope into");
        ScopeBuilder last = getLast();
        identifierTable.add(new ScopeBuilder(last.currentId()));
    }

    /**
     * 函数功能：退出当前作用域。
     * 输入：
     * - 无。
     * 输出：IdentifierRecord[] 类型数组。
     */
    public IdentifierRecord[] scopeExist() {
        log.trace("scope exist");
        return removeLast().records()
                .stream()
                .sorted(Comparator.comparingInt(IdentifierRecord::getNo))
                .toArray(IdentifierRecord[]::new);
    }

    /**
     * 函数功能：移除最后一个作用域。
     * 输入：
     * - 无。
     * 输出：ScopeBuilder 类型返回值。
     */

    private ScopeBuilder removeLast() {
        return identifierTable.remove(identifierTable.size() - 1);
    }

    /**
     * 函数功能：获取最后一个元素。
     * 输入：
     * - 无。
     * 输出：ScopeBuilder 类型返回值。
     */

    private ScopeBuilder getLast() {
        return identifierTable.get(identifierTable.size() - 1);
    }

    private static class ScopeBuilder {
        private final Map<IdentifierKey, IdentifierRecord> map = new HashMap<>();
        private final IdGenerator idGenerator;

        /**
         * 函数功能：创建 ScopeBuilder 对象。
         * 输入：
         * - initialValue：int 类型参数。
         * 输出：无。
         */

        public ScopeBuilder(int initialValue) {
            this.idGenerator = new IdGenerator(initialValue);
        }

        /**
         * 函数功能：判断当前作用域是否包含指定键。
         * 输入：
         * - identifierKey：IdentifierKey 类型参数。
         * 输出：判断结果布尔值。
         */

        public boolean containsKey(IdentifierKey identifierKey) {
            return map.containsKey(identifierKey);
        }

        /**
         * 函数功能：写入指定键值关系。
         * 输入：
         * - identifierKey：IdentifierKey 类型参数。
         * - identifierRecord：IdentifierRecord 类型参数。
         * 输出：无。
         */

        public void put(IdentifierKey identifierKey, IdentifierRecord identifierRecord) {
            map.put(identifierKey, identifierRecord);
        }

        /**
         * 函数功能：获取当前作用域编号。
         * 输入：
         * - 无。
         * 输出：整数结果。
         */

        public int currentId() {
            return idGenerator.current();
        }

        /**
         * 函数功能：获取记录集合。
         * 输入：
         * - 无。
         * 输出：Collection<IdentifierRecord> 类型集合或迭代结果。
         */

        public Collection<IdentifierRecord> records() {
            return map.values();
        }

        /**
         * 函数功能：生成下一个记录编号。
         * 输入：
         * - 无。
         * 输出：整数结果。
         */

        public int nextNo() {
            return idGenerator.next();
        }

        /**
         * 函数功能：获取指定键或索引对应的对象。
         * 输入：
         * - identifierKey：IdentifierKey 类型参数。
         * 输出：IdentifierRecord 类型返回值。
         */

        public IdentifierRecord get(IdentifierKey identifierKey) {
            return map.get(identifierKey);
        }

        /**
         * 函数功能：返回当前对象的字符串表示。
         * 输入：
         * - 无。
         * 输出：字符串结果。
         */

        @Override
        public String toString() {
            return map.values()
                    .stream()
                    .map(IdentifierRecord::displayString)
                    .collect(Collectors.joining(",", "{", "}"));
        }
    }


}
