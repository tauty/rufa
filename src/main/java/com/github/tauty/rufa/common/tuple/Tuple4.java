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
 * Immutable tuple class which contains 4 values.
 *
 * @see com.github.tauty.rufa.common.tuple.MutableTuple3
 * @see com.github.tauty.rufa.common.tuple.Tuples
 * @author tauty
 */
public class Tuple4<T1, T2, T3, T4> {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;
    public final T4 _4;

    Tuple4(T1 _1, T2 _2, T3 _3, T4 _4) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
    }

    public MutableTuple4<T1, T2, T3, T4> toMutable() {
        return new MutableTuple4<T1, T2, T3, T4>(_1, _2, _3, _4);
    }
}
