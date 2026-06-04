package org.harvey.vie.theory.semantic.function;

import lombok.Getter;

/**
 * 当前函数体的一些配置信息, 一般都会在函数Record上记录
 *
 * @author Temper
 */
@Getter
public class FunctionBodyState {
    private final FunctionRecord function;
    /**
     * 函数功能：创建 FunctionBodyState 对象。
     * 输入：
     * - function：FunctionRecord 类型参数。
     * 输出：无。
     */
    public FunctionBodyState(FunctionRecord function) {
        this.function = function;
    }

}

