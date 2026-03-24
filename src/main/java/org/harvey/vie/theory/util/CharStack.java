package org.harvey.vie.theory.util;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 11:18
 */
public class CharStack {
    private static final int DEFAULT_INIT_CAP = 8;
    private char[] stack;
    private int cur;

    public CharStack(int initCapacity) {
        stack = new char[Math.max(initCapacity, DEFAULT_INIT_CAP)];
        cur = -1;
    }

    public CharStack() {
        this(DEFAULT_INIT_CAP);
    }

    public boolean isEmpty() {
        return cur == -1;
    }

    public int size() {
        return cur + 1;
    }

    public char top() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("stack is empty.");
        }
        return stack[cur];
    }

    public char pop() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("stack is empty.");
        }
        char result = stack[cur--];
        if (needShrink()) {
            shrink();
        }
        return result;
    }

    public void push(char c) {
        stack[++cur] = c;
        if (needGrow()) {
            grow();
        }
    }

    private void grow() {
        stack = Arrays.copyOf(stack, stack.length << 1);
    }

    private boolean needGrow() {
        // cur << 2 >=  cap<< 1 + cap
        int cap = stack.length;
        return (cur << 2) - cap >= cap << 1;
    }

    private void shrink() {
        stack = Arrays.copyOf(stack, stack.length >> 1);
    }

    private boolean needShrink() {
        int cap = stack.length;
        return cap > DEFAULT_INIT_CAP && (cur << 1) + cap <= cap;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",","[","]");
        for (int i = 0; i <= cur; i++) {
            sj.add(stack[i]+"");
        }
        return sj.toString();
    }
}
