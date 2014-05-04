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
 * Immutable tuple class which contains 2 values.
 *
 * @see com.github.tauty.rufa.common.tuple.Pair
 * @see com.github.tauty.rufa.common.tuple.MutablePair
 * @see com.github.tauty.rufa.common.tuple.Tuples
 * @author tauty
 */
public class Tuple2<T1, T2> {
    public final T1 _1;
    public final T2 _2;

    public Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public MutableTuple2<T1, T2> toMutable() {
        return new MutableTuple2<T1, T2>(_1, _2);
    }
}
