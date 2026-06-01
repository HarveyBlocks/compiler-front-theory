package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;

import java.util.List;

/**
 * 讲解主线第 4 站：命令片段注册器。
 * <p>
 * 语法制导翻译在规约时是局部工作的：一个产生式只能看到自己右部的若干子结果。
 * 为了让父产生式继续组合这些子结果，每个 {@link CommandTranslator} 都返回
 * {@link CommandNodeRegister}，而不是直接返回最终线性代码。
 * <p>
 * 这个接口承担两个编译原理概念：
 * 第一，综合属性的传递：{@link #register(CommandNodeBuilder)} 把子树按正确顺序写入父节点；
 * 第二，控制流回填：{@code break}/{@code continue} 先以 {@link UncertainLabelGotoCommand}
 * 向外传播，等遇到最近循环时再绑定标签，这和教材里的 backpatching 思想一致。
 * <p>
 * 主要实现：
 * {@link TokenCommandRegister} 包一条终结命令；
 * {@link NormalCommandNodeRegister} 包一次规约后的非终结节点；
 * {@link PlaceholderNodeRegister} 表示“不产生命令”；
 * {@link MergedCommandNodeRegister} 用于常量分支裁剪后仍保留死分支里的未绑定跳转诊断信息。
 * <p>
 * 主线下一站：{@link CommandNode}。下一站会讲注册器写入的命令节点树如何表示中间代码结构。
 *
 * @author Temper
 */
public interface CommandNodeRegister {
    /**
     * 把当前注册器代表的命令节点写入外层 builder。
     * <p>
     * 这是“组合命令树”的入口：外层翻译器不关心子节点到底是 token、普通 head 还是占位；
     * 它只调用这一句，让实现类自己决定要不要写入节点。
     */
    void register(CommandNodeBuilder outer);

    /**
     * 返回当前子树里尚未绑定目标标签的 break 跳转。
     */
    default List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return List.of();
    }

    /**
     * 返回当前子树里尚未绑定目标标签的 continue 跳转。
     */
    default List<UncertainLabelGotoCommand> getUncertainContinues() {
        return List.of();
    }
}
