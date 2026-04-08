package org.harvey.vie.theory.syntax.callback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-08 13:03
 */
public class CallbackRegisterImpl<T extends SemanticCallback> implements CallbackRegister<T> {
    private final List<T> list = new ArrayList<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public void add(T callable) {
        list.add(callable);
    }

}
