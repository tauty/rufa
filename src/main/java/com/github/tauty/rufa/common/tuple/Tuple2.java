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

import static com.github.tauty.rufa.common.util.CommonUtil.*;

/**
 * Immutable tuple class which contains 2 values.
 *
 * @author tauty
 * @see com.github.tauty.rufa.common.tuple.Pair
 * @see com.github.tauty.rufa.common.tuple.MutablePair
 * @see com.github.tauty.rufa.common.tuple.Tuples
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Tuple2) {
            Tuple2 t = (Tuple2) obj;
            return equals(t._1, t._2);
        } else if (obj instanceof MutableTuple2) {
            MutableTuple2 t = (MutableTuple2) obj;
            return equals(t._1, t._2);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashSum(_1, _2);
    }

    public boolean equals(Object val1, Object val2) {
        return equalsSafely(_1, val1) && equalsSafely(_2, val2);
    }
}
