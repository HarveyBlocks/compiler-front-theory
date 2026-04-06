package org.harvey.vie.theory.util;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 18:57
 */
public class AfterIterable<T> implements Iterable<T> {
    private final int offset;
    private final IRandomAccess<T> randomAccess;

    public AfterIterable(int offset, IRandomAccess<T> randomAccess) {
        this.offset = offset;
        this.randomAccess = randomAccess;
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return new Iterator();
    }

    private class Iterator implements java.util.Iterator<T> {
        private int pos = offset + 1;

        @Override
        public boolean hasNext() {
            return pos < randomAccess.size();
        }

        @Override
        public T next() {
            return randomAccess.get(pos++);
        }
    }
}
