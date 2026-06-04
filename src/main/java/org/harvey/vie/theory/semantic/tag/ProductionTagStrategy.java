package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Temper
 */
public class ProductionTagStrategy<T> {
    private final List<Rule<T>> rules = new ArrayList<>();
    private final T defaultValue;
/**
 * 函数功能：创建 ProductionTagStrategy 对象。
 * 输入：
 * - defaultValue：T 类型参数。
 * 输出：无。
 */

    public ProductionTagStrategy(T defaultValue) {
        this.defaultValue = defaultValue;
    }
/**
 * 函数功能：创建按条件触发的产生式标签策略。
 * 输入：
 * - value：T 类型参数。
 * - expected：SemanticTag... 类型参数。
 * 输出：ProductionTagStrategy<T> 类型返回值。
 */

    public ProductionTagStrategy<T> when(T value, SemanticTag... expected) {
        rules.add(new Rule<>(value, expected));
        return this;
    }
/**
 * 函数功能：解析并返回目标对象。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：T 类型返回值。
 */

    public T resolve(SimpleGrammarProduction production) {
        // 有点暴力
        for (Rule<T> rule : rules) {
            if (rule.matches(production)) {
                return rule.value;
            }
        }
        return defaultValue;
    }

    private static final class Rule<T> {
        private final T value;
        private final SemanticTag[] expected;
/**
 * 函数功能：创建 Rule 对象。
 * 输入：
 * - value：T 类型参数。
 * - expected：SemanticTag[] 类型参数。
 * 输出：无。
 */

        private Rule(T value, SemanticTag[] expected) {
            this.value = value;
            this.expected = expected;
        }
/**
 * 函数功能：判断语义标签是否匹配。
 * 输入：
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：判断结果布尔值。
 */

        private boolean matches(SimpleGrammarProduction production) {
            return production.matchTags(expected);
        }
    }
}

