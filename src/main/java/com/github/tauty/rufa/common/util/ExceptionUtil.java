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

import com.github.tauty.rufa.common.exception.WrapException;

/**
 * A utility class which enables you to use exceptions and errors easily.
 */
public class ExceptionUtil {

    public static RuntimeException wrapIfChecked(Throwable t) {
        if (t instanceof RuntimeException) return (RuntimeException) t;
        if (t instanceof Error) throw (Error) t;
        return new WrapException(t);
    }

    public static boolean isCaused(Class<?> throwableClass, Throwable t) {
        if (t == null) return false;
        return throwableClass.isInstance(t) || isCaused(throwableClass, t.getCause());
    }
}
