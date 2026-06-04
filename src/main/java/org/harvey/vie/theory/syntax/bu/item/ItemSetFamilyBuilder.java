package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.first.FirstSet;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-05 23:32
 */
public class ItemSetFamilyBuilder {
    private final Map<ItemSetBuilder, Integer> family = new HashMap<>();
    private final FirstMap firstMap;
    private IdGenerator idGenerator;
    private ItemSetBuilder[] sorted;
/**
 * 函数功能：创建 ItemSetFamilyBuilder 对象。
 * 输入：
 * - firstMap：FirstMap 类型参数。
 * 输出：无。
 */

    ItemSetFamilyBuilder(FirstMap firstMap) {
        this.firstMap = firstMap;
    }
/**
 * 函数功能：设置起始项目集。
 * 输入：
 * - 无。
 * 输出：ItemSetBuilder 类型返回值。
 */

    public ItemSetBuilder start() {
        idGenerator = new IdGenerator(0);
        int id = idGenerator.next();
        ItemSetBuilder itemSetBuilder = new ItemSetBuilder(this);
        itemSetBuilder.setIndex(id);
        family.put(itemSetBuilder, itemSetBuilder.getIndex());
        return itemSetBuilder;
    }
/**
 * 函数功能：添加指定元素。
 * 输入：
 * - value：ItemSetBuilder 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean add(ItemSetBuilder value) {
        Integer id = family.get(value);
        if (id != null) {
            value.setIndex(id);
            return false;
        }
        if (idGenerator == null) {
            throw new IllegalStateException("Add must not be called before constructing start");
        }
        value.setIndex(idGenerator.next());
        family.put(value, value.getIndex());
        return true;
    }
/**
 * 函数功能：获取 FIRST 集合。
 * 输入：
 * - afterIterable：Iterable<GrammarUnitSymbol> 类型参数。
 * 输出：FirstSet 类型返回值。
 */

    public FirstSet first(Iterable<GrammarUnitSymbol> afterIterable) {
        return firstMap.first(afterIterable);
    }
/**
 * 函数功能：构建目标对象。
 * 输入：
 * - 无。
 * 输出：ItemSetFamily 类型返回值。
 */

    public ItemSetFamily build() {
        this.sorted = family.keySet()
                .stream()
                .sorted(Comparator.comparingInt(ItemSetBuilder::getIndex))
                .toArray(ItemSetBuilder[]::new);
        ItemSet[] array = Arrays.stream(sorted).map(ItemSetBuilder::build).toArray(ItemSet[]::new);
        return new ItemSetFamilyImpl(0, array);
    }
/**
 * 函数功能：获取项目集构建器。
 * 输入：
 * - setId：int 类型参数。
 * 输出：ItemSetBuilder 类型返回值。
 */

    public ItemSetBuilder getItemSetBuilder(int setId) {
        if (sorted == null) {
            throw new IllegalStateException(
                    "Incorrect call timing: The ItemSetFamily is not yet ready to build, and this call can only be made after the build has been started.");
        }
        return sorted[setId];
    }
/**
 * 函数功能：判断指定符号是否可推出空串。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean nullable(HeadSymbol head) {
        return firstMap.nullable(List.of(head));
    }
}
