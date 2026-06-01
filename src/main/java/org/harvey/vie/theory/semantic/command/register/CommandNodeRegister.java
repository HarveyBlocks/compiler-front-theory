package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;

import java.util.List;

/**
 * 讲解主线第 4 站：命令翻译器返回的统一包装接口。
 * <p>
 * 每个 {@link CommandTranslator} 不直接返回 {@link CommandNode} 数组，而是返回
 * {@link CommandNodeRegister}。原因有两个：
 * 1. 外层产生式需要调用 {@link #register(CommandNodeBuilder)}，把子树按正确顺序写进自己的 builder；
 * 2. {@code break}/{@code continue} 需要先以 {@link UncertainLabelGotoCommand} 的形式向外冒泡，
 * 等最近的循环翻译器再绑定到真实标签。
 * <p>
 * 主要实现：
 * {@link TokenCommandRegister} 包一个终结命令，
 * {@link NormalCommandNodeRegister} 包一个规约后的非终结节点，
 * {@link PlaceholderNodeRegister} 表示“不产生命令”，
 * {@link MergedCommandNodeRegister} 用于常量分支裁剪后仍保留另一支路里的未绑定跳转信息。
 * <p>
 * 讲完本接口，下一站看具体节点结构 {@link CommandNode}，再看线性展开工具
 * {@link org.harvey.vie.theory.semantic.command.CommandSegmentSupport}。
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
