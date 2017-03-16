package com.github.tauty.rufa.common.util;

import com.github.tauty.rufa.Rufa;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by tetz on 2017/03/03.
 */
public class PureIterator<E> {
    private final Collection<E> collection;
    private final Iterator<E> iterator;
    private final boolean hasNext;
    private final E head;
    private PureIterator<E> tail = null;

    public PureIterator(Collection<E> collection) {
        this(collection, collection.iterator());
    }

    private PureIterator(Collection<E> collection, Iterator<E> iterator) {
        this.collection = collection;
        this.iterator = iterator;
        this.hasNext = iterator.hasNext();
        this.head = iterator.hasNext() ? iterator.next() : null;
    }

    public E head() {
        return this.head;
    }

    public PureIterator<E> tail() {
        if (this.tail == null) {
            this.tail = new PureIterator<E>(this.collection, this.iterator);
        }
        return this.tail;
    }

    public boolean hasNext() {
        return this.hasNext;
    }

    public boolean contains(Object o) {
        return this.collection.contains(o);
    }
}
