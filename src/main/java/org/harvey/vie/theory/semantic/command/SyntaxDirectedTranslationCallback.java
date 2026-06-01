package org.harvey.vie.theory.semantic.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;

/**
 * 语法制导翻译回调的占位类。
 * <p>
 * 当前真正承担“移进/规约事件 -> 中间命令”的实现是 {@link CommandBuildCallback}。
 * 本类没有覆写任何 {@link ShiftReduceCallback} 方法，因此不会产生命令；讲解主线请跳到
 * {@link CommandBuildCallback}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 04:37
 */
@AllArgsConstructor
public class SyntaxDirectedTranslationCallback implements ShiftReduceCallback {

}
