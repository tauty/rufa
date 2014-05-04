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
package com.github.tauty.rufa.common.tuple;

/**
 * A utility class which enable you to use tuple classes easily.
 * Example:
 * <pre>
 *     Pair<String, List<String>> pair = pair( "person", Arrays.asList("Bill", "Jobs") );
 *     pair = pair.toMutable()._1("people").toImmutable();
 *
 *     MutableTriple<Integer, Integer, Integer> triple = mutable.triple(1, 2, 3);
 *     triple._3++;
 * </pre>
 *
 * @author tauty
 */
public class Tuples {

    public static <T1, T2> Pair<T1, T2> pair(T1 _1, T2 _2) {
        return new Pair<T1, T2>(_1, _2);
    }

    public static <T1, T2, T3> Triple<T1, T2, T3> triple(T1 _1, T2 _2, T3 _3) {
        return new Triple<T1, T2, T3>(_1, _2, _3);
    }

    public static <T1, T2> Tuple2<T1, T2> tuple(T1 _1, T2 _2) {
        return new Tuple2<T1, T2>(_1, _2);
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(T1 _1, T2 _2, T3 _3) {
        return new Tuple3<T1, T2, T3>(_1, _2, _3);
    }

    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(T1 _1, T2 _2, T3 _3, T4 _4) {
        return new Tuple4<T1, T2, T3, T4>(_1, _2, _3, _4);
    }

    public static class mutable {
        public static <T1, T2> MutablePair<T1, T2> pair(T1 _1, T2 _2) {
            return new MutablePair<T1, T2>(_1, _2);
        }

        public static <T1, T2, T3> MutableTriple<T1, T2, T3> triple(T1 _1, T2 _2, T3 _3) {
            return new MutableTriple<T1, T2, T3>(_1, _2, _3);
        }

        public static <T1, T2> MutableTuple2<T1, T2> tuple(T1 _1, T2 _2) {
            return new MutableTuple2<T1, T2>(_1, _2);
        }

        public static <T1, T2, T3> MutableTuple3<T1, T2, T3> tuple(T1 _1, T2 _2, T3 _3) {
            return new MutableTuple3<T1, T2, T3>(_1, _2, _3);
        }

        public static <T1, T2, T3, T4> MutableTuple4<T1, T2, T3, T4> tuple(T1 _1, T2 _2, T3 _3, T4 _4) {
            return new MutableTuple4<T1, T2, T3, T4>(_1, _2, _3, _4);
        }
    }
}
