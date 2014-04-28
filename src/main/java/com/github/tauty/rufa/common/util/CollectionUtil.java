/*
 * Copyright 2014 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tauty.rufa.common.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tetsuo.uchiumi on 4/23/14.
 */
public class CollectionUtil {

    public static final <E> List<E> newList() {
        return newArrayList();
    }

    public static final <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    public static final <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }

    public static final <E> Set<E> newSet() {
        return newHashSet();
    }

    public static final <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    public static final <K, V> Map<K, V> newMap() {
        return newHashMap();
    }

    public static final <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static final <K, V> ConcurrentHashMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<K, V>();
    }

    public static <K, V> ChainMap $(K key, V value) {
        return new ChainMap().$(key, value);
    }

    public static <E> List<E> join(List<E> list1, List<E> list2) {
        List<E> joinedList = newList();
        if(list1 != null) {
            joinedList.addAll(list1);
        }
        if(list2 != null) {
            joinedList.addAll(list2);
        }
        return joinedList;
    }

    public static Iterable<?> toIterable(final Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Iterable) {
            return (Iterable<?>) obj;
        } else if (obj.getClass().isArray()) {
            return new Iterable<Object>() {
                @Override
                public Iterator<Object> iterator() {
                    return new ImmutableIterator<Object>() {
                        final int LENGTH = Array.getLength(obj);
                        int index = 0;

                        @Override
                        public boolean hasNext() {
                            return index < LENGTH;
                        }

                        @Override
                        public Object next() {
                            return Array.get(obj, index++);
                        }
                    };
                }
            };
        }
        return null;
    }

    public static Iterator<?> toIterator(final Object obj) {
        Iterable<?> itr = toIterable(obj);
        return itr == null ? null : itr.iterator();
    }

    public static class ChainMap<K, V> extends LinkedHashMap {
        public ChainMap $(K key, V value) {
            this.put(key, value);
            return this;
        }
    }

    public static abstract class ImmutableIterator<E> implements Iterator<E> {

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Doesn't support 'remove' method.");
        }
    }
}
