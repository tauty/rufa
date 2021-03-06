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
 * An alias of MutableTuple2.
 *
 * @see com.github.tauty.rufa.common.tuple.MutableTuple2
 * @see com.github.tauty.rufa.common.tuple.Pair
 * @see com.github.tauty.rufa.common.tuple.Tuples
 * @author tauty
 */
public class MutablePair<T1, T2> extends MutableTuple2<T1, T2> {

    public MutablePair(T1 _1, T2 _2) {
        super(_1, _2);
    }

    @Override
    public Pair<T1, T2> toImmutable() {
        return new Pair<T1, T2>(_1, _2);
    }
}
